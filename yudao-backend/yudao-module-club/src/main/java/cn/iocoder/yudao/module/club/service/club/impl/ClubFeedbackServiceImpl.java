package cn.iocoder.yudao.module.club.service.club.impl;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.club.controller.admin.club.vo.ClubFeedbackPageReqVO;
import cn.iocoder.yudao.module.club.controller.admin.club.vo.ClubFeedbackRespVO;
import cn.iocoder.yudao.module.club.controller.admin.club.vo.ClubFeedbackSaveReqVO;
import cn.iocoder.yudao.module.club.controller.admin.club.vo.ClubFeedbackUpdateStatusReqVO;
import cn.iocoder.yudao.module.club.dal.dataobject.club.ClubFeedbackDO;
import cn.iocoder.yudao.module.club.dal.mysql.club.ClubFeedbackMapper;
import cn.iocoder.yudao.module.club.service.club.ClubFeedbackService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 反馈 Service 实现
 */
@Service
public class ClubFeedbackServiceImpl implements ClubFeedbackService {

    @Resource
    private ClubFeedbackMapper clubFeedbackMapper;

    @Override
    public Long createFeedback(ClubFeedbackSaveReqVO createReqVO) {
        ClubFeedbackDO feedbackDO = new ClubFeedbackDO();
        BeanUtils.copyProperties(createReqVO, feedbackDO);
        feedbackDO.setStatus(0); // 默认待处理
        clubFeedbackMapper.insert(feedbackDO);
        return feedbackDO.getId();
    }

    @Override
    public void updateFeedbackStatus(ClubFeedbackUpdateStatusReqVO updateReqVO) {
        clubFeedbackMapper.updateById(new ClubFeedbackDO() {{
            setId(updateReqVO.getId());
            setStatus(updateReqVO.getStatus());
        }});
    }

    @Override
    public PageResult<ClubFeedbackRespVO> getFeedbackPage(ClubFeedbackPageReqVO pageReqVO) {
        PageResult<ClubFeedbackDO> pageResult = clubFeedbackMapper.selectPage(pageReqVO,
                new LambdaQueryWrapperX<ClubFeedbackDO>()
                        .eqIfPresent(ClubFeedbackDO::getPage, pageReqVO.getPage())
                        .eqIfPresent(ClubFeedbackDO::getStatus, pageReqVO.getStatus())
                        .orderByDesc(ClubFeedbackDO::getId));
        List<ClubFeedbackRespVO> list = pageResult.getList().stream().map(feedbackDO -> {
            ClubFeedbackRespVO respVO = new ClubFeedbackRespVO();
            BeanUtils.copyProperties(feedbackDO, respVO);
            return respVO;
        }).collect(Collectors.toList());
        return new PageResult<>(list, pageResult.getTotal());
    }

}
