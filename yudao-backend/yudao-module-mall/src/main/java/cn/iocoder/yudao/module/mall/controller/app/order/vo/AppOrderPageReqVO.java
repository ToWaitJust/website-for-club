package cn.iocoder.yudao.module.mall.controller.app.order.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "用户 App - 订单分页 Request VO")
@Data
public class AppOrderPageReqVO {

    @Schema(description = "订单状态：0=待付款，1=已完成，2=已取消", example = "0")
    private Integer status;

    @Schema(description = "用户编号（内部填充）", hidden = true)
    private Long userId;

    @Schema(description = "页码", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer pageNo = 1;

    @Schema(description = "每页条数", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    private Integer pageSize = 10;

}
