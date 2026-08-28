package cn.iocoder.yudao.module.mall.service.cart;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.mall.controller.admin.cart.vo.CartPageReqVO;
import cn.iocoder.yudao.module.mall.controller.admin.cart.vo.CartRespVO;
import cn.iocoder.yudao.module.mall.controller.admin.cart.vo.CartSaveReqVO;
import cn.iocoder.yudao.module.mall.dal.dataobject.mall.CartDO;

import java.util.List;

/**
 * 购物车 Service
 */
public interface CartService {

    Long createCart(CartSaveReqVO createReqVO);

    void updateCart(CartSaveReqVO updateReqVO);

    void deleteCart(Long id);

    CartRespVO getCart(Long id);

    PageResult<CartRespVO> getCartPage(CartPageReqVO pageReqVO);

    // ========== 用户端 ==========

    /**
     * 获取用户购物车列表
     */
    List<CartDO> getCartListByUserId(Long userId);

    /**
     * 添加商品到购物车（已存在则累加数量）
     *
     * @param userId    用户编号
     * @param productId 商品编号
     * @param count     数量
     * @return 购物车记录编号
     */
    Long addCart(Long userId, Long productId, Integer count);

    /**
     * 更新购物车商品数量
     *
     * @param userId 用户编号
     * @param id     购物车记录编号
     * @param count  新数量
     */
    void updateCartCount(Long userId, Long id, Integer count);

    /**
     * 删除购物车商品
     *
     * @param userId 用户编号
     * @param id     购物车记录编号
     */
    void deleteCart(Long userId, Long id);

}
