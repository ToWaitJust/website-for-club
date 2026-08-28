package cn.iocoder.yudao.module.mall.controller.admin.cart.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 购物车-分页查询 VO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CartPageReqVO extends PageParam {

    private Long userId;
    private Long productId;

}
