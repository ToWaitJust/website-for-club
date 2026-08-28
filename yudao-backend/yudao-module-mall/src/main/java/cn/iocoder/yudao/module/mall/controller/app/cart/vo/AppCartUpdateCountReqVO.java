package cn.iocoder.yudao.module.mall.controller.app.cart.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Schema(description = "用户 App - 购物车更新数量 Request VO")
@Data
public class AppCartUpdateCountReqVO {

    @Schema(description = "购物车记录编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "购物车记录编号不能为空")
    private Long id;

    @Schema(description = "购买数量", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotNull(message = "购买数量不能为空")
    @Min(value = 1, message = "购买数量不能小于 1")
    private Integer count;

}
