package cn.iocoder.yudao.module.mall.controller.admin.statistics.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Schema(description = "管理后台 - 商城统计数据 Response VO")
@Data
public class MallStatisticsRespVO {

    @Schema(description = "商品总数", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long productCount;

    @Schema(description = "分类总数", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long categoryCount;

    @Schema(description = "订单总数", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long orderCount;

    @Schema(description = "销售总额", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal totalSales;

    @Schema(description = "待付款订单数", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long pendingPaymentCount;

    @Schema(description = "待发货订单数", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long pendingShipCount;

    @Schema(description = "已完成订单数", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long completedCount;

    @Schema(description = "近7天销售额趋势")
    private List<SalesTrendItem> salesTrend;

    @Schema(description = "商品销量排行")
    private List<ProductSalesRankItem> productSalesRank;

    @Schema(description = "订单状态分布")
    private List<OrderStatusItem> orderStatusDistribution;

    @Data
    @Schema(description = "销售趋势项")
    public static class SalesTrendItem {
        @Schema(description = "日期")
        private String date;

        @Schema(description = "销售额")
        private BigDecimal amount;

        @Schema(description = "订单数")
        private Long orderCount;
    }

    @Data
    @Schema(description = "商品销量排行项")
    public static class ProductSalesRankItem {
        @Schema(description = "商品ID")
        private Long productId;

        @Schema(description = "商品名称")
        private String productName;

        @Schema(description = "销量")
        private Integer salesCount;

        @Schema(description = "销售额")
        private BigDecimal salesAmount;
    }

    @Data
    @Schema(description = "订单状态分布项")
    public static class OrderStatusItem {
        @Schema(description = "状态")
        private Integer status;

        @Schema(description = "状态名称")
        private String statusName;

        @Schema(description = "数量")
        private Long count;
    }

}
