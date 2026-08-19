package cn.iocoder.yudao.module.club.service.club.impl;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.club.controller.admin.club.vo.ClubBusinessLinePageReqVO;
import cn.iocoder.yudao.module.club.controller.admin.club.vo.ClubBusinessLineRespVO;
import cn.iocoder.yudao.module.club.controller.admin.club.vo.ClubBusinessLineSaveReqVO;
import cn.iocoder.yudao.module.club.dal.dataobject.club.ClubBusinessLineDO;
import cn.iocoder.yudao.module.club.dal.mysql.club.ClubBusinessLineMapper;
import cn.iocoder.yudao.module.club.service.club.ClubBusinessLineService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 业务线 Service 实现
 */
@Service
public class ClubBusinessLineServiceImpl implements ClubBusinessLineService {

    @Resource
    private ClubBusinessLineMapper clubBusinessLineMapper;

    @Override
    public Long createBusinessLine(ClubBusinessLineSaveReqVO createReqVO) {
        ClubBusinessLineDO businessLineDO = new ClubBusinessLineDO();
        BeanUtils.copyProperties(createReqVO, businessLineDO);
        if (businessLineDO.getRegisterOpen() == null) {
            businessLineDO.setRegisterOpen(true);
        }
        clubBusinessLineMapper.insert(businessLineDO);
        return businessLineDO.getId();
    }

    @Override
    public void updateBusinessLine(ClubBusinessLineSaveReqVO updateReqVO) {
        ClubBusinessLineDO businessLineDO = new ClubBusinessLineDO();
        BeanUtils.copyProperties(updateReqVO, businessLineDO);
        clubBusinessLineMapper.updateById(businessLineDO);
    }

    @Override
    public void deleteBusinessLine(Long id) {
        clubBusinessLineMapper.deleteById(id);
    }

    @Override
    public ClubBusinessLineRespVO getBusinessLine(Long id) {
        ClubBusinessLineDO businessLineDO = clubBusinessLineMapper.selectById(id);
        ClubBusinessLineRespVO respVO = new ClubBusinessLineRespVO();
        BeanUtils.copyProperties(businessLineDO, respVO);
        return respVO;
    }

    @Override
    public List<ClubBusinessLineRespVO> getBusinessLineList() {
        List<ClubBusinessLineDO> list = clubBusinessLineMapper.selectList(
                new LambdaQueryWrapperX<ClubBusinessLineDO>().orderByAsc(ClubBusinessLineDO::getSort));
        return list.stream().map(businessLineDO -> {
            ClubBusinessLineRespVO respVO = new ClubBusinessLineRespVO();
            BeanUtils.copyProperties(businessLineDO, respVO);
            return respVO;
        }).collect(Collectors.toList());
    }

    @Override
    public PageResult<ClubBusinessLineRespVO> getBusinessLinePage(ClubBusinessLinePageReqVO pageReqVO) {
        PageResult<ClubBusinessLineDO> pageResult = clubBusinessLineMapper.selectPage(pageReqVO,
                new LambdaQueryWrapperX<ClubBusinessLineDO>()
                        .likeIfPresent(ClubBusinessLineDO::getName, pageReqVO.getName())
                        .orderByAsc(ClubBusinessLineDO::getSort));
        List<ClubBusinessLineRespVO> list = pageResult.getList().stream().map(businessLineDO -> {
            ClubBusinessLineRespVO respVO = new ClubBusinessLineRespVO();
            BeanUtils.copyProperties(businessLineDO, respVO);
            return respVO;
        }).collect(Collectors.toList());
        return new PageResult<>(list, pageResult.getTotal());
    }

}
