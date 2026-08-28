package cn.iocoder.yudao.module.mall.controller.admin.category.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商品分类-分页查询 VO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CategoryPageReqVO extends PageParam {

    private String name;
    private Integer status;

}
