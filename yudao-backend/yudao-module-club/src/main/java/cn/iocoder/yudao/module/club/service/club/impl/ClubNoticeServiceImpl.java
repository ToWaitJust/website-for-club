package cn.iocoder.yudao.module.club.service.club.impl;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.club.controller.admin.club.vo.ClubNoticePageReqVO;
import cn.iocoder.yudao.module.club.controller.admin.club.vo.ClubNoticeRespVO;
import cn.iocoder.yudao.module.club.controller.admin.club.vo.ClubNoticeSaveReqVO;
import cn.iocoder.yudao.module.club.dal.dataobject.club.ClubNoticeDO;
import cn.iocoder.yudao.module.club.dal.mysql.club.ClubNoticeMapper;
import cn.iocoder.yudao.module.club.service.club.ClubNoticeService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 公告 Service 实现
 */
@Service
public class ClubNoticeServiceImpl implements ClubNoticeService {

    @Resource
    private ClubNoticeMapper clubNoticeMapper;

    @Override
    public Long createNotice(ClubNoticeSaveReqVO createReqVO) {
        ClubNoticeDO noticeDO = new ClubNoticeDO();
        BeanUtils.copyProperties(createReqVO, noticeDO);
        if (noticeDO.getStatus() == null) {
            noticeDO.setStatus(1); // 默认发布
        }
        clubNoticeMapper.insert(noticeDO);
        return noticeDO.getId();
    }

    @Override
    public void updateNotice(ClubNoticeSaveReqVO updateReqVO) {
        ClubNoticeDO noticeDO = new ClubNoticeDO();
        BeanUtils.copyProperties(updateReqVO, noticeDO);
        clubNoticeMapper.updateById(noticeDO);
    }

    @Override
    public void deleteNotice(Long id) {
        clubNoticeMapper.deleteById(id);
    }

    @Override
    public ClubNoticeRespVO getNotice(Long id) {
        ClubNoticeDO noticeDO = clubNoticeMapper.selectById(id);
        ClubNoticeRespVO respVO = new ClubNoticeRespVO();
        BeanUtils.copyProperties(noticeDO, respVO);
        return respVO;
    }

    @Override
    public List<ClubNoticeRespVO> getNoticeList() {
        List<ClubNoticeDO> list = clubNoticeMapper.selectList(
                new LambdaQueryWrapperX<ClubNoticeDO>()
                        .eq(ClubNoticeDO::getStatus, 1)
                        .orderByDesc(ClubNoticeDO::getPublishTime));
        return list.stream().map(noticeDO -> {
            ClubNoticeRespVO respVO = new ClubNoticeRespVO();
            BeanUtils.copyProperties(noticeDO, respVO);
            return respVO;
        }).collect(Collectors.toList());
    }

    @Override
    public PageResult<ClubNoticeRespVO> getNoticePage(ClubNoticePageReqVO pageReqVO) {
        PageResult<ClubNoticeDO> pageResult = clubNoticeMapper.selectPage(pageReqVO,
                new LambdaQueryWrapperX<ClubNoticeDO>()
                        .likeIfPresent(ClubNoticeDO::getTitle, pageReqVO.getTitle())
                        .orderByDesc(ClubNoticeDO::getId));
        List<ClubNoticeRespVO> list = pageResult.getList().stream().map(noticeDO -> {
            ClubNoticeRespVO respVO = new ClubNoticeRespVO();
            BeanUtils.copyProperties(noticeDO, respVO);
            return respVO;
        }).collect(Collectors.toList());
        return new PageResult<>(list, pageResult.getTotal());
    }

}
