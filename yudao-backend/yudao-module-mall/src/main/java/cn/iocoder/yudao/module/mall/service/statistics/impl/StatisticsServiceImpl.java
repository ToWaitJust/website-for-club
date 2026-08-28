package cn.iocoder.yudao.module.mall.service.statistics;

import cn.iocoder.yudao.module.mall.controller.admin.statistics.vo.MallStatisticsRespVO;
import cn.iocoder.yudao.module.mall.dal.dataobject.mall.OrderDO;
import cn.iocoder.yudao.module.mall.dal.dataobject.mall.OrderItemDO;
import cn.iocoder.yudao.module.mall.dal.mysql.mall.CategoryMapper;
import cn.iocoder.yudao.module.mall.dal.mysql.mall.OrderItemMapper;
import cn.iocoder.yudao.module.mall.dal.mysql.mall.OrderMapper;
import cn.iocoder.yudao.module.mall.dal.mysql.mall.ProductMapper;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Resource
    private ProductMapper productMapper;
    @Resource
    private CategoryMapper categoryMapper;
    @Resource
    private OrderMapper orderMapper;
    @Resource
    private OrderItemMapper orderItemMapper;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM-dd");

    @Override
    public MallStatisticsRespVO getStatistics() {
        MallStatisticsRespVO vo = new MallStatisticsRespVO();

        // 1. 基础统计 - 使用 selectCount，不加载全表数据
        vo.setProductCount(productMapper.selectCount(null));
        vo.setCategoryCount(categoryMapper.selectCount(null));
        vo.setOrderCount(orderMapper.selectCount(new LambdaQueryWrapperX<OrderDO>().eq(OrderDO::getDeleted, 0)));

        // 2. 订单状态分布 - 4次精准 count 查询
        long pendingPayment = orderMapper.selectCount(new LambdaQueryWrapperX<OrderDO>()
                .eq(OrderDO::getDeleted, 0).eq(OrderDO::getStatus, 0));
        long pendingShip = orderMapper.selectCount(new LambdaQueryWrapperX<OrderDO>()
                .eq(OrderDO::getDeleted, 0).eq(OrderDO::getStatus, 1));
        long completed = orderMapper.selectCount(new LambdaQueryWrapperX<OrderDO>()
                .eq(OrderDO::getDeleted, 0).eq(OrderDO::getStatus, 2));
        long cancelled = orderMapper.selectCount(new LambdaQueryWrapperX<OrderDO>()
                .eq(OrderDO::getDeleted, 0).eq(OrderDO::getStatus, 3));

        vo.setPendingPaymentCount(pendingPayment);
        vo.setPendingShipCount(pendingShip);
        vo.setCompletedCount(completed);

        List<MallStatisticsRespVO.OrderStatusItem> statusList = new ArrayList<>();
        statusList.add(buildStatusItem(0, "待付款", pendingPayment));
        statusList.add(buildStatusItem(1, "待发货", pendingShip));
        statusList.add(buildStatusItem(2, "已完成", completed));
        statusList.add(buildStatusItem(3, "已取消", cancelled));
        vo.setOrderStatusDistribution(statusList);

        // 3. 总销售额 - 只查已完成的订单，而非全表
        List<OrderDO> completedOrders = orderMapper.selectList(new LambdaQueryWrapperX<OrderDO>()
                .eq(OrderDO::getDeleted, 0).eq(OrderDO::getStatus, 2));
        BigDecimal totalSales = completedOrders.stream()
                .map(OrderDO::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        vo.setTotalSales(totalSales);

        // 4. 近7天销售趋势 - 只查最近7天的订单，而非全部历史订单
        LocalDate today = LocalDate.now();
        LocalDateTime weekAgo = today.minusDays(7).atStartOfDay();
        List<OrderDO> recentOrders = orderMapper.selectList(new LambdaQueryWrapperX<OrderDO>()
                .eq(OrderDO::getDeleted, 0)
                .ge(OrderDO::getCreateTime, weekAgo));

        Map<String, List<OrderDO>> ordersByDate = recentOrders.stream()
                .filter(o -> o.getCreateTime() != null)
                .collect(Collectors.groupingBy(o -> o.getCreateTime().toLocalDate().format(DATE_FORMATTER)));

        List<MallStatisticsRespVO.SalesTrendItem> salesTrend = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            String dateStr = date.format(DATE_FORMATTER);
            List<OrderDO> dayOrders = ordersByDate.getOrDefault(dateStr, Collections.emptyList());

            BigDecimal dayAmount = dayOrders.stream()
                    .filter(o -> o.getStatus() != null && o.getStatus() == 2)
                    .map(OrderDO::getTotalAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            MallStatisticsRespVO.SalesTrendItem item = new MallStatisticsRespVO.SalesTrendItem();
            item.setDate(dateStr);
            item.setAmount(dayAmount);
            item.setOrderCount((long) dayOrders.size());
            salesTrend.add(item);
        }
        vo.setSalesTrend(salesTrend);

        // 5. 商品销量排行 Top 10 - 只查已完成订单的明细
        Set<String> completedOrderNos = completedOrders.stream()
                .map(OrderDO::getOrderNo)
                .collect(Collectors.toSet());

        List<MallStatisticsRespVO.ProductSalesRankItem> rankList = new ArrayList<>();
        if (!completedOrderNos.isEmpty()) {
            List<OrderItemDO> completedItems = orderItemMapper.selectList(
                    new LambdaQueryWrapperX<OrderItemDO>()
                            .eq(OrderItemDO::getDeleted, 0)
                            .in(OrderItemDO::getOrderNo, completedOrderNos));

            Map<Long, List<OrderItemDO>> productItemMap = completedItems.stream()
                    .filter(item -> item.getProductId() != null)
                    .collect(Collectors.groupingBy(OrderItemDO::getProductId));

            for (Map.Entry<Long, List<OrderItemDO>> entry : productItemMap.entrySet()) {
                List<OrderItemDO> items = entry.getValue();
                int salesCount = items.stream().mapToInt(OrderItemDO::getCount).sum();
                BigDecimal salesAmount = items.stream()
                        .map(OrderItemDO::getTotalAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                if (salesCount > 0) {
                    MallStatisticsRespVO.ProductSalesRankItem rankItem = new MallStatisticsRespVO.ProductSalesRankItem();
                    rankItem.setProductId(entry.getKey());
                    rankItem.setProductName(items.get(0).getProductName());
                    rankItem.setSalesCount(salesCount);
                    rankItem.setSalesAmount(salesAmount);
                    rankList.add(rankItem);
                }
            }

            rankList.sort((a, b) -> b.getSalesCount().compareTo(a.getSalesCount()));
            if (rankList.size() > 10) {
                rankList = rankList.subList(0, 10);
            }
        }
        vo.setProductSalesRank(rankList);

        return vo;
    }

    private MallStatisticsRespVO.OrderStatusItem buildStatusItem(Integer status, String name, Long count) {
        MallStatisticsRespVO.OrderStatusItem item = new MallStatisticsRespVO.OrderStatusItem();
        item.setStatus(status);
        item.setStatusName(name);
        item.setCount(count);
        return item;
    }

}
