package cn.iocoder.yudao.module.club.controller.admin.club.vo;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 反馈-已读标记 VO
 */
@Data
public class ClubFeedbackUpdateStatusReqVO {

    @NotNull(message = "反馈编号不能为空")
    private Long id;
    @NotNull(message = "状态不能为空")
    private Integer status;

}
