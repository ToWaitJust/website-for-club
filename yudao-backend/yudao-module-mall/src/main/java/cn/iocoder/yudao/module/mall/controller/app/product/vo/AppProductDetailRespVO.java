package cn.iocoder.yudao.module.mall.controller.app.product.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "用户 App - 商品详情 Response VO")
@Data
public class AppProductDetailRespVO {

    @Schema(description = "商品编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "分类编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long categoryId;

    @Schema(description = "商品名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "无线蓝牙耳机")
    private String name;

    @Schema(description = "封面图", example = "https://xxx.com/cover.jpg")
    private String coverUrl;

    @Schema(description = "详情图", example = "https://xxx.com/detail1.jpg,https://xxx.com/detail2.jpg")
    private String detailUrls;

    @Schema(description = "售价", requiredMode = Schema.RequiredMode.REQUIRED, example = "99.00")
    private BigDecimal price;

    @Schema(description = "库存", requiredMode = Schema.RequiredMode.REQUIRED, example = "100")
    private Integer stock;

    @Schema(description = "销量", example = "50")
    private Integer sales;

    @Schema(description = "商品描述", example = "这是一款高品质蓝牙耳机")
    private String description;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}
