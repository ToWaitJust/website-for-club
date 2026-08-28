package cn.iocoder.yudao.module.mall.controller.admin.product.vo;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 商品-保存 VO
 */
@Data
public class ProductSaveReqVO {

    private Long id;
    @NotNull(message = "分类不能为空")
    private Long categoryId;
    @NotEmpty(message = "商品名称不能为空")
    private String name;
    private String coverUrl;
    private String detailUrls;
    @NotNull(message = "售价不能为空")
    private BigDecimal price;
    @NotNull(message = "库存不能为空")
    private Integer stock;
    private String description;
    @NotNull(message = "上架状态不能为空")
    private Integer status;

}
