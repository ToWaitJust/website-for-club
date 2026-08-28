package cn.iocoder.yudao.module.mall.controller.admin.order.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 订单明细-响应 VO
 */
@Data
public class OrderItemRespVO {

    private Long id;
    private String orderNo;
    private Long productId;
    private String productName;
    private String productCoverUrl;
    private BigDecimal price;
    private Integer count;
    private BigDecimal totalAmount;

}
