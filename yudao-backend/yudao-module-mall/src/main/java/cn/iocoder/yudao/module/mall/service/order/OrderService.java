package cn.iocoder.yudao.module.mall.service.order;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.mall.controller.admin.order.vo.OrderPageReqVO;
import cn.iocoder.yudao.module.mall.controller.admin.order.vo.OrderRespVO;
import cn.iocoder.yudao.module.mall.controller.admin.order.vo.OrderUpdateStatusReqVO;
import cn.iocoder.yudao.module.mall.controller.app.order.vo.AppOrderCreateReqVO;
import cn.iocoder.yudao.module.mall.dal.dataobject.mall.OrderDO;

/**
 * 订单 Service
 */
public interface OrderService {

    OrderRespVO getOrder(Long id);

    PageResult<OrderRespVO> getOrderPage(OrderPageReqVO pageReqVO);

    void updateOrderStatus(OrderUpdateStatusReqVO updateReqVO);

    // ========== 用户端 ==========

    /**
     * 创建订单
     * 1. 校验商品是否上架
     * 2. 原子扣减库存（防止超卖）
     * 3. 生成订单号
     * 4. 保存订单主表 + 明细表（价格快照）
     * 5. 增加销量
     *
     * @param userId 用户编号
     * @param reqVO  订单创建请求
     * @return 订单编号
     */
    Long createOrder(Long userId, AppOrderCreateReqVO reqVO);

    /**
     * 获取用户订单详情（含明细）
     *
     * @param userId 用户编号
     * @param id     订单编号
     * @return 订单详情
     */
    OrderDO getOrder(Long userId, Long id);

    /**
     * 取消订单（恢复库存、减少销量）
     *
     * @param userId 用户编号
     * @param id     订单编号
     */
    void cancelOrder(Long userId, Long id);

    /**
     * 支付订单（待付款 → 待发货）
     *
     * @param userId 用户编号
     * @param id     订单编号
     */
    void payOrder(Long userId, Long id);

    /**
     * 确认收货（待发货 → 已完成）
     *
     * @param userId 用户编号
     * @param id     订单编号
     */
    void confirmReceipt(Long userId, Long id);

    /**
     * 获取用户订单分页
     *
     * @param pageReqVO 分页参数（含 userId）
     * @return 订单分页结果
     */
    PageResult<OrderDO> getAppOrderPage(OrderPageReqVO pageReqVO);

    /**
     * 用户端删除订单（仅标记 user_deleted，管理员仍可见）
     * 只允许删除已完成或已取消的订单
     *
     * @param userId 用户编号
     * @param id     订单编号
     */
    void deleteUserOrder(Long userId, Long id);

}
