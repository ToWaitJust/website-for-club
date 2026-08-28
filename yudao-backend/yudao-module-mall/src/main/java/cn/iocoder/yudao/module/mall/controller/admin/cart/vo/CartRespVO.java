package cn.iocoder.yudao.module.mall.controller.admin.cart.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 购物车-响应 VO
 */
@Data
public class CartRespVO {

    private Long id;
    private Long userId;
    private Long productId;
    private Integer count;
    private Boolean selected;
    private LocalDateTime createTime;

}
