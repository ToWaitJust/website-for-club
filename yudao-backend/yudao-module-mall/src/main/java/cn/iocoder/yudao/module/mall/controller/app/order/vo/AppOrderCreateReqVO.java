package cn.iocoder.yudao.module.mall.controller.app.order.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Schema(description = "用户 App - 订单创建 Request VO")
@Data
public class AppOrderCreateReqVO {

    @Schema(description = "订单项列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "订单项不能为空")
    @Valid
    private List<Item> items;

    @Schema(description = "订单商品项")
    @Data
    public static class Item {

        @Schema(description = "商品编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
        private Long productId;

        @Schema(description = "购买数量", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
        private Integer count;

    }

}
