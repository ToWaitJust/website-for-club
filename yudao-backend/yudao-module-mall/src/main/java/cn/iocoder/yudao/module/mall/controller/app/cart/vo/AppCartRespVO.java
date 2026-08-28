package cn.iocoder.yudao.module.mall.controller.app.cart.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Schema(description = "用户 App - 购物车 Response VO")
@Data
public class AppCartRespVO {

    @Schema(description = "购物车记录编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "商品编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long productId;

    @Schema(description = "商品名称", example = "无线蓝牙耳机")
    private String productName;

    @Schema(description = "商品封面图", example = "https://xxx.com/cover.jpg")
    private String productCoverUrl;

    @Schema(description = "商品单价", example = "99.00")
    private BigDecimal price;

    @Schema(description = "商品库存", example = "100")
    private Integer stock;

    @Schema(description = "购买数量", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    private Integer count;

    @Schema(description = "是否选中", example = "true")
    private Boolean selected;

}
