package cn.iocoder.yudao.module.mall.controller.app.cart.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Schema(description = "用户 App - 购物车添加 Request VO")
@Data
public class AppCartAddReqVO {

    @Schema(description = "商品编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "商品编号不能为空")
    private Long productId;

    @Schema(description = "购买数量", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "购买数量不能为空")
    @Min(value = 1, message = "购买数量不能小于 1")
    private Integer count;

}
