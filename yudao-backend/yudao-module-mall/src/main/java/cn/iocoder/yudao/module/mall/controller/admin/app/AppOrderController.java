package cn.iocoder.yudao.module.mall.controller.admin.app;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mall.controller.admin.order.vo.OrderPageReqVO;
import cn.iocoder.yudao.module.mall.controller.app.order.vo.AppOrderCreateReqVO;
import cn.iocoder.yudao.module.mall.controller.app.order.vo.AppOrderDetailRespVO;
import cn.iocoder.yudao.module.mall.controller.app.order.vo.AppOrderRespVO;
import cn.iocoder.yudao.module.mall.dal.dataobject.mall.OrderDO;
import cn.iocoder.yudao.module.mall.dal.dataobject.mall.OrderItemDO;
import cn.iocoder.yudao.module.mall.dal.mysql.mall.OrderItemMapper;
import cn.iocoder.yudao.module.mall.service.order.OrderService;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "用户端 - 订单")
@RestController
@RequestMapping("/mall/app/order")
@Validated
public class AppOrderController {

    @Resource
    private OrderService orderService;
    @Resource
    private OrderItemMapper orderItemMapper;

    @PostMapping("/create")
    @Operation(summary = "创建订单")
    @PermitAll
    public CommonResult<Long> createOrder(@RequestHeader("user-id") Long userId,
                                           @Valid @RequestBody AppOrderCreateReqVO reqVO) {
        Long orderId = orderService.createOrder(userId, reqVO);
        return success(orderId);
    }

    @GetMapping("/page")
    @Operation(summary = "获得订单分页（含明细）")
    @PermitAll
    public CommonResult<PageResult<AppOrderRespVO>> getOrderPage(
            @RequestHeader("user-id") Long userId,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer pageNo,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        OrderPageReqVO pageReqVO = new OrderPageReqVO();
        pageReqVO.setUserId(userId);
        pageReqVO.setStatus(status);
        pageReqVO.setPageNo(pageNo);
        pageReqVO.setPageSize(pageSize);
        PageResult<OrderDO> pageResult = orderService.getAppOrderPage(pageReqVO);

        List<AppOrderRespVO> respList = BeanUtils.toBean(pageResult.getList(), AppOrderRespVO.class);
        if (!respList.isEmpty()) {
            List<String> orderNos = respList.stream().map(AppOrderRespVO::getOrderNo).collect(Collectors.toList());
            List<OrderItemDO> allItems = orderItemMapper.selectList(
                    new LambdaQueryWrapperX<OrderItemDO>().in(OrderItemDO::getOrderNo, orderNos));
            Map<String, List<OrderItemDO>> itemMap = allItems.stream()
                    .collect(Collectors.groupingBy(OrderItemDO::getOrderNo));
            respList.forEach(order -> order.setItems(
                    BeanUtils.toBean(itemMap.get(order.getOrderNo()), AppOrderDetailRespVO.Item.class)));
        }
        return success(new PageResult<>(respList, pageResult.getTotal()));
    }

    @GetMapping("/detail")
    @Operation(summary = "获得订单详情")
    @Parameter(name = "id", description = "订单编号", required = true, example = "1")
    @PermitAll
    public CommonResult<AppOrderDetailRespVO> getOrderDetail(@RequestHeader("user-id") Long userId,
                                                               @RequestParam("id") Long id) {
        OrderDO order = orderService.getOrder(userId, id);
        AppOrderDetailRespVO detail = BeanUtils.toBean(order, AppOrderDetailRespVO.class);
        List<OrderItemDO> items = orderItemMapper.selectList(
                new LambdaQueryWrapperX<OrderItemDO>().eq(OrderItemDO::getOrderNo, order.getOrderNo()));
        detail.setItems(BeanUtils.toBean(items, AppOrderDetailRespVO.Item.class));
        return success(detail);
    }

    @PutMapping("/cancel")
    @Operation(summary = "取消订单")
    @Parameter(name = "id", description = "订单编号", required = true, example = "1")
    @PermitAll
    public CommonResult<Boolean> cancelOrder(@RequestHeader("user-id") Long userId,
                                              @RequestParam("id") Long id) {
        orderService.cancelOrder(userId, id);
        return success(true);
    }

    @PutMapping("/pay")
    @Operation(summary = "支付订单")
    @Parameter(name = "id", description = "订单编号", required = true, example = "1")
    @PermitAll
    public CommonResult<Boolean> payOrder(@RequestHeader("user-id") Long userId,
                                          @RequestParam("id") Long id) {
        orderService.payOrder(userId, id);
        return success(true);
    }

    @PutMapping("/confirm")
    @Operation(summary = "确认收货")
    @Parameter(name = "id", description = "订单编号", required = true, example = "1")
    @PermitAll
    public CommonResult<Boolean> confirmReceipt(@RequestHeader("user-id") Long userId,
                                                 @RequestParam("id") Long id) {
        orderService.confirmReceipt(userId, id);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "用户端删除订单（仅对用户隐藏，管理员仍可见）")
    @Parameter(name = "id", description = "订单编号", required = true, example = "1")
    @PermitAll
    public CommonResult<Boolean> deleteUserOrder(@RequestHeader("user-id") Long userId,
                                                  @RequestParam("id") Long id) {
        orderService.deleteUserOrder(userId, id);
        return success(true);
    }

}
