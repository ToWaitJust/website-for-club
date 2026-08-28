package cn.iocoder.yudao.module.mall.controller.admin.order.vo;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 订单-状态更新 VO
 */
@Data
public class OrderUpdateStatusReqVO {

    @NotNull(message = "订单编号不能为空")
    private Long id;
    @NotNull(message = "状态不能为空")
    private Integer status;

}
