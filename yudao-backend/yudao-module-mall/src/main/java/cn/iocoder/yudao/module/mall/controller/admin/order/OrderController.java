package cn.iocoder.yudao.module.mall.controller.admin.order;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.mall.controller.admin.order.vo.OrderPageReqVO;
import cn.iocoder.yudao.module.mall.controller.admin.order.vo.OrderRespVO;
import cn.iocoder.yudao.module.mall.controller.admin.order.vo.OrderUpdateStatusReqVO;
import cn.iocoder.yudao.module.mall.service.order.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 订单管理")
@RestController
@RequestMapping("/mall/order")
@Validated
public class OrderController {

    @Resource
    private OrderService orderService;

    @GetMapping("/get")
    @Operation(summary = "获得订单详情")
    @Parameter(name = "id", description = "编号", required = true)
    public CommonResult<OrderRespVO> getOrder(@RequestParam("id") Long id) {
        return success(orderService.getOrder(id));
    }

    @GetMapping("/page")
    @Operation(summary = "获得订单分页")
    public CommonResult<PageResult<OrderRespVO>> getOrderPage(@Valid OrderPageReqVO pageReqVO) {
        return success(orderService.getOrderPage(pageReqVO));
    }

    @PutMapping("/update-status")
    @Operation(summary = "更新订单状态")
    public CommonResult<Boolean> updateOrderStatus(@Valid @RequestBody OrderUpdateStatusReqVO updateReqVO) {
        orderService.updateOrderStatus(updateReqVO);
        return success(true);
    }

}
