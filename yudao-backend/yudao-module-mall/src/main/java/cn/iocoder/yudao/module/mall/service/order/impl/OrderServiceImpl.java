package cn.iocoder.yudao.module.mall.service.order.impl;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.mall.controller.admin.order.vo.OrderItemRespVO;
import cn.iocoder.yudao.module.mall.controller.admin.order.vo.OrderPageReqVO;
import cn.iocoder.yudao.module.mall.controller.admin.order.vo.OrderRespVO;
import cn.iocoder.yudao.module.mall.controller.admin.order.vo.OrderUpdateStatusReqVO;
import cn.iocoder.yudao.module.mall.controller.app.order.vo.AppOrderCreateReqVO;
import cn.iocoder.yudao.module.mall.dal.dataobject.mall.OrderDO;
import cn.iocoder.yudao.module.mall.dal.dataobject.mall.OrderItemDO;
import cn.iocoder.yudao.module.mall.dal.dataobject.mall.ProductDO;
import cn.iocoder.yudao.module.mall.dal.mysql.mall.OrderItemMapper;
import cn.iocoder.yudao.module.mall.dal.mysql.mall.OrderMapper;
import cn.iocoder.yudao.module.mall.service.order.OrderService;
import cn.iocoder.yudao.module.mall.service.product.ProductService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.mall.enums.ErrorCodeConstants.*;

/**
 * 订单 Service 实现
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Resource
    private OrderMapper orderMapper;
    @Resource
    private OrderItemMapper orderItemMapper;
    @Resource
    private ProductService productService;

    /**
     * 订单号自增序号（每毫秒内自增）
     */
    private final AtomicLong orderSequence = new AtomicLong(0);

    @Override
    public OrderRespVO getOrder(Long id) {
        OrderDO orderDO = orderMapper.selectById(id);
        if (orderDO == null) {
            return null;
        }
        OrderRespVO respVO = new OrderRespVO();
        BeanUtils.copyProperties(orderDO, respVO);
        List<OrderItemDO> items = orderItemMapper.selectList(
                new LambdaQueryWrapperX<OrderItemDO>().eq(OrderItemDO::getOrderNo, orderDO.getOrderNo()));
        respVO.setItems(items.stream().map(itemDO -> {
            OrderItemRespVO itemVO = new OrderItemRespVO();
            BeanUtils.copyProperties(itemDO, itemVO);
            return itemVO;
        }).collect(Collectors.toList()));
        return respVO;
    }

    @Override
    public PageResult<OrderRespVO> getOrderPage(OrderPageReqVO pageReqVO) {
        PageResult<OrderDO> pageResult = orderMapper.selectPage(pageReqVO,
                new LambdaQueryWrapperX<OrderDO>()
                        .likeIfPresent(OrderDO::getOrderNo, pageReqVO.getOrderNo())
                        .eqIfPresent(OrderDO::getUserId, pageReqVO.getUserId())
                        .eqIfPresent(OrderDO::getStatus, pageReqVO.getStatus())
                        .orderByDesc(OrderDO::getId));
        List<OrderRespVO> list = pageResult.getList().stream().map(orderDO -> {
            OrderRespVO respVO = new OrderRespVO();
            BeanUtils.copyProperties(orderDO, respVO);
            return respVO;
        }).collect(Collectors.toList());
        return new PageResult<>(list, pageResult.getTotal());
    }

    @Override
    public void updateOrderStatus(OrderUpdateStatusReqVO updateReqVO) {
        OrderDO orderDO = orderMapper.selectById(updateReqVO.getId());
        if (orderDO == null) {
            throw exception(ORDER_NOT_EXISTS);
        }
        Integer targetStatus = updateReqVO.getStatus();
        Integer currentStatus = orderDO.getStatus();

        // 状态流转校验
        validateStatusTransition(currentStatus, targetStatus);

        OrderDO update = new OrderDO();
        update.setId(updateReqVO.getId());
        update.setStatus(targetStatus);

        if (targetStatus == 1 && currentStatus == 0) {
            // 待付款 → 待发货：设置支付时间
            update.setPayTime(LocalDateTime.now());
        } else if (targetStatus == 2 && currentStatus == 1) {
            // 待发货 → 已完成：设置完成时间
            update.setFinishTime(LocalDateTime.now());
        } else if (targetStatus == 3 && currentStatus == 0) {
            // 待付款 → 已取消：设置取消时间
            update.setCancelTime(LocalDateTime.now());
            update.setCancelReason("管理员取消");
        }

        orderMapper.updateById(update);
    }

    /**
     * 校验订单状态流转是否合法
     *
     * 状态流转图：
     * 0(待付款) → 1(待发货)  [支付]
     * 0(待付款) → 3(已取消)  [取消]
     * 1(待发货) → 2(已完成)  [发货/确认收货]
     * 1(待发货) → 3(已取消)  [取消]
     */
    private void validateStatusTransition(Integer currentStatus, Integer targetStatus) {
        boolean valid = false;
        if (currentStatus == 0 && (targetStatus == 1 || targetStatus == 3)) {
            valid = true;
        } else if (currentStatus == 1 && (targetStatus == 2 || targetStatus == 3)) {
            valid = true;
        }
        if (!valid) {
            throw exception(ORDER_STATUS_NOT_ALLOWED);
        }
    }

    // ========== 用户端 ==========

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createOrder(Long userId, AppOrderCreateReqVO reqVO) {
        List<AppOrderCreateReqVO.Item> items = reqVO.getItems();
        if (items == null || items.isEmpty()) {
            throw exception(ORDER_ITEM_INVALID);
        }

        // 1. 校验商品并准备订单明细
        List<OrderItemDO> orderItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;
        String orderNo = generateOrderNo();

        for (AppOrderCreateReqVO.Item item : items) {
            ProductDO product = productService.getProductDO(item.getProductId());
            if (product == null) {
                throw exception(ORDER_PRODUCT_NOT_EXISTS);
            }
            if (product.getStatus() != 0) {
                throw exception(ORDER_PRODUCT_OFF_SHELF);
            }
            // 原子扣减库存
            boolean success = productService.deductStock(item.getProductId(), item.getCount());
            if (!success) {
                throw exception(ORDER_CREATE_STOCK_NOT_ENOUGH);
            }
            // 增加销量
            productService.addSales(item.getProductId(), item.getCount());

            // 构建订单明细（价格快照）
            OrderItemDO orderItem = new OrderItemDO();
            orderItem.setOrderNo(orderNo);
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setProductCoverUrl(product.getCoverUrl());
            orderItem.setPrice(product.getPrice());
            orderItem.setCount(item.getCount());
            orderItem.setTotalAmount(product.getPrice().multiply(BigDecimal.valueOf(item.getCount())));
            orderItems.add(orderItem);

            // 累加总金额
            totalAmount = totalAmount.add(orderItem.getTotalAmount());
        }

        // 2. 创建订单主表
        OrderDO order = new OrderDO();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setStatus(0); // 待付款
        orderMapper.insert(order);

        // 3. 批量插入订单明细
        for (OrderItemDO item : orderItems) {
            orderItemMapper.insert(item);
        }

        return order.getId();
    }

    @Override
    public OrderDO getOrder(Long userId, Long id) {
        OrderDO order = orderMapper.selectById(id);
        if (order == null || !order.getUserId().equals(userId)) {
            throw exception(ORDER_NOT_EXISTS);
        }
        return order;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(Long userId, Long id) {
        OrderDO order = getOrder(userId, id);
        // 待付款(0)和待发货(1)状态都可以取消
        if (order.getStatus() != 0 && order.getStatus() != 1) {
            throw exception(ORDER_STATUS_NOT_ALLOWED);
        }

        // 查询订单明细
        List<OrderItemDO> items = orderItemMapper.selectList(
                new LambdaQueryWrapperX<OrderItemDO>().eq(OrderItemDO::getOrderNo, order.getOrderNo()));

        // 恢复库存、减少销量
        for (OrderItemDO item : items) {
            productService.restoreStock(item.getProductId(), item.getCount());
            productService.decreaseSales(item.getProductId(), item.getCount());
        }

        // 更新订单状态为已取消
        OrderDO update = new OrderDO();
        update.setId(id);
        update.setStatus(3); // 已取消
        update.setCancelTime(LocalDateTime.now());
        update.setCancelReason(order.getStatus() == 1 ? "待发货取消" : "用户取消");
        orderMapper.updateById(update);
    }

    @Override
    public void payOrder(Long userId, Long id) {
        OrderDO order = getOrder(userId, id);
        if (order.getStatus() != 0) {
            throw exception(ORDER_STATUS_NOT_PENDING);
        }
        OrderDO update = new OrderDO();
        update.setId(id);
        update.setStatus(1);
        update.setPayTime(LocalDateTime.now());
        orderMapper.updateById(update);
    }

    @Override
    public void confirmReceipt(Long userId, Long id) {
        OrderDO order = getOrder(userId, id);
        if (order.getStatus() != 1) {
            throw exception(ORDER_STATUS_NOT_ALLOWED);
        }
        OrderDO update = new OrderDO();
        update.setId(id);
        update.setStatus(2);
        update.setFinishTime(LocalDateTime.now());
        orderMapper.updateById(update);
    }

    @Override
    public PageResult<OrderDO> getAppOrderPage(OrderPageReqVO pageReqVO) {
        return orderMapper.selectPage(pageReqVO,
                new LambdaQueryWrapperX<OrderDO>()
                        .eqIfPresent(OrderDO::getUserId, pageReqVO.getUserId())
                        .eqIfPresent(OrderDO::getStatus, pageReqVO.getStatus())
                        .eq(OrderDO::getUserDeleted, false)
                        .orderByDesc(OrderDO::getId));
    }

    @Override
    public void deleteUserOrder(Long userId, Long id) {
        OrderDO order = getOrder(userId, id);
        // 只有已完成(2)或已取消(3)的订单才能删除
        if (order.getStatus() != 2 && order.getStatus() != 3) {
            throw exception(ORDER_STATUS_NOT_ALLOWED);
        }
        OrderDO update = new OrderDO();
        update.setId(id);
        update.setUserDeleted(true);
        orderMapper.updateById(update);
    }

    /**
     * 生成订单号：yyyyMMddHHmmssSSS(17位时间) + 3位序号
     * 使用毫秒级时间戳，同一毫秒内通过序号区分
     */
    private String generateOrderNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
        long seq = orderSequence.incrementAndGet() % 1000;
        return timestamp + String.format("%03d", seq);
    }

}
