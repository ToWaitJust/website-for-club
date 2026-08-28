package cn.iocoder.yudao.module.mall.controller.admin.category.vo;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 商品分类-保存 VO
 */
@Data
public class CategorySaveReqVO {

    private Long id;
    @NotEmpty(message = "分类名称不能为空")
    private String name;
    @NotNull(message = "排序值不能为空")
    private Integer sort;
    @NotNull(message = "启用状态不能为空")
    private Integer status;

}
