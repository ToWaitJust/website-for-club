package cn.iocoder.yudao.module.mall.controller.app.order.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "用户 App - 订单列表 Response VO")
@Data
public class AppOrderRespVO {

    @Schema(description = "订单编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "订单号", requiredMode = Schema.RequiredMode.REQUIRED, example = "2024010112345678")
    private String orderNo;

    @Schema(description = "订单金额", requiredMode = Schema.RequiredMode.REQUIRED, example = "199.00")
    private BigDecimal totalAmount;

    @Schema(description = "订单状态：0=待付款，1=待发货，2=已完成，3=已取消", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    private Integer status;

    @Schema(description = "下单时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

    @Schema(description = "订单明细列表")
    private List<AppOrderDetailRespVO.Item> items;

}
