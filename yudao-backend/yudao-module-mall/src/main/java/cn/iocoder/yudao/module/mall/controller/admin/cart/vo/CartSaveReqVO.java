package cn.iocoder.yudao.module.mall.controller.admin.cart.vo;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 购物车-保存 VO
 */
@Data
public class CartSaveReqVO {

    private Long id;
    @NotNull(message = "商品不能为空")
    private Long productId;
    @NotNull(message = "购买数量不能为空")
    private Integer count;
    private Boolean selected;

}
