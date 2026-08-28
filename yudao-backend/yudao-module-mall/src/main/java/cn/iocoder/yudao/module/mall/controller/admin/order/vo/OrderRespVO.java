package cn.iocoder.yudao.module.mall.controller.admin.order.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单-响应 VO
 */
@Data
public class OrderRespVO {

    private Long id;
    private String orderNo;
    private Long userId;
    private BigDecimal totalAmount;
    private Integer status;
    private LocalDateTime payTime;
    private LocalDateTime finishTime;
    private LocalDateTime cancelTime;
    private String cancelReason;
    private LocalDateTime createTime;
    /**
     * 订单明细列表
     */
    private List<OrderItemRespVO> items;

}
