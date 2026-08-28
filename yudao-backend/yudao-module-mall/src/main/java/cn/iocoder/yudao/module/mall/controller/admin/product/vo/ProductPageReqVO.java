package cn.iocoder.yudao.module.mall.controller.admin.product.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商品-分页查询 VO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ProductPageReqVO extends PageParam {

    private String name;
    private Long categoryId;
    private Integer status;

}
