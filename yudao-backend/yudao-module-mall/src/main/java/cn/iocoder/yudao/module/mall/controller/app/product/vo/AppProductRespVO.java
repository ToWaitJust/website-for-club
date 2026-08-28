package cn.iocoder.yudao.module.mall.controller.app.product.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Schema(description = "用户 App - 商品列表 Response VO")
@Data
public class AppProductRespVO {

    @Schema(description = "商品编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "商品名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "无线蓝牙耳机")
    private String name;

    @Schema(description = "分类编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long categoryId;

    @Schema(description = "封面图", example = "https://xxx.com/cover.jpg")
    private String coverUrl;

    @Schema(description = "售价", requiredMode = Schema.RequiredMode.REQUIRED, example = "99.00")
    private BigDecimal price;

    @Schema(description = "销量", example = "100")
    private Integer sales;

}
