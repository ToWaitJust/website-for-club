package cn.iocoder.yudao.module.club.controller.admin.club.vo;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 报名-状态更新 VO
 */
@Data
public class ClubRegisterUpdateStatusReqVO {

    @NotNull(message = "报名记录编号不能为空")
    private Long id;
    /** 状态: 0=待处理 1=已联系 2=已录取 3=未录取 */
    @NotNull(message = "状态不能为空")
    private Integer status;

}
