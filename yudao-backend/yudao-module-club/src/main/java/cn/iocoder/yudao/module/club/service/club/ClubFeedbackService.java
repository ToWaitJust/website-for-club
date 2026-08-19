package cn.iocoder.yudao.module.club.service.club;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.club.controller.admin.club.vo.ClubFeedbackPageReqVO;
import cn.iocoder.yudao.module.club.controller.admin.club.vo.ClubFeedbackRespVO;
import cn.iocoder.yudao.module.club.controller.admin.club.vo.ClubFeedbackSaveReqVO;
import cn.iocoder.yudao.module.club.controller.admin.club.vo.ClubFeedbackUpdateStatusReqVO;

/**
 * 反馈 Service
 */
public interface ClubFeedbackService {

    /** 创建反馈(公开接口) */
    Long createFeedback(ClubFeedbackSaveReqVO createReqVO);

    /** 更新反馈状态(后台) */
    void updateFeedbackStatus(ClubFeedbackUpdateStatusReqVO updateReqVO);

    /** 分页查询反馈(后台) */
    PageResult<ClubFeedbackRespVO> getFeedbackPage(ClubFeedbackPageReqVO pageReqVO);

}
