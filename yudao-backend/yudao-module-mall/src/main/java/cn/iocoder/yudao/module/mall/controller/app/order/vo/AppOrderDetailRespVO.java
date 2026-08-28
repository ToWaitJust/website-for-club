package cn.iocoder.yudao.module.mall.controller.app.order.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "用户 App - 订单详情 Response VO")
@Data
public class AppOrderDetailRespVO {

    @Schema(description = "订单编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "订单号", requiredMode = Schema.RequiredMode.REQUIRED, example = "2024010112345678")
    private String orderNo;

    @Schema(description = "订单金额", requiredMode = Schema.RequiredMode.REQUIRED, example = "199.00")
    private BigDecimal totalAmount;

    @Schema(description = "订单状态：0=待付款，1=待发货，2=已完成，3=已取消", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    private Integer status;

    @Schema(description = "付款时间")
    private LocalDateTime payTime;

    @Schema(description = "完成时间")
    private LocalDateTime finishTime;

    @Schema(description = "取消时间")
    private LocalDateTime cancelTime;

    @Schema(description = "取消原因", example = "不想买了")
    private String cancelReason;

    @Schema(description = "下单时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

    @Schema(description = "订单明细列表")
    private List<Item> items;

    @Schema(description = "订单明细项")
    @Data
    public static class Item {

        @Schema(description = "商品编号", example = "1")
        private Long productId;

        @Schema(description = "商品名称", example = "无线蓝牙耳机")
        private String productName;

        @Schema(description = "商品封面图", example = "https://xxx.com/cover.jpg")
        private String productCoverUrl;

        @Schema(description = "单价", example = "99.00")
        private BigDecimal price;

        @Schema(description = "数量", example = "2")
        private Integer count;

        @Schema(description = "小计", example = "198.00")
        private BigDecimal totalAmount;

    }

}
