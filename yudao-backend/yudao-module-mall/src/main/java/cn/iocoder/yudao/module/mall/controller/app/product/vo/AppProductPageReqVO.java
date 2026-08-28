package cn.iocoder.yudao.module.mall.controller.app.product.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "用户 App - 商品分页 Request VO")
@Data
public class AppProductPageReqVO {

    @Schema(description = "分类编号", example = "1")
    private Long categoryId;

    @Schema(description = "商品名称", example = "耳机")
    private String name;

    @Schema(description = "页码", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer pageNo = 1;

    @Schema(description = "每页条数", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    private Integer pageSize = 10;

    @Schema(description = "上架状态（内部使用，不由用户传入）", hidden = true)
    private Integer status;

}
