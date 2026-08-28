package cn.iocoder.yudao.module.mall.service.cart.impl;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.mall.controller.admin.cart.vo.CartPageReqVO;
import cn.iocoder.yudao.module.mall.controller.admin.cart.vo.CartRespVO;
import cn.iocoder.yudao.module.mall.controller.admin.cart.vo.CartSaveReqVO;
import cn.iocoder.yudao.module.mall.dal.dataobject.mall.CartDO;
import cn.iocoder.yudao.module.mall.dal.dataobject.mall.ProductDO;
import cn.iocoder.yudao.module.mall.dal.mysql.mall.CartMapper;
import cn.iocoder.yudao.module.mall.service.cart.CartService;
import cn.iocoder.yudao.module.mall.service.product.ProductService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.mall.enums.ErrorCodeConstants.*;

/**
 * 购物车 Service 实现
 */
@Service
public class CartServiceImpl implements CartService {

    @Resource
    private CartMapper cartMapper;
    @Resource
    private ProductService productService;

    @Override
    public Long createCart(CartSaveReqVO createReqVO) {
        CartDO cartDO = new CartDO();
        BeanUtils.copyProperties(createReqVO, cartDO);
        if (cartDO.getSelected() == null) {
            cartDO.setSelected(true);
        }
        cartMapper.insert(cartDO);
        return cartDO.getId();
    }

    @Override
    public void updateCart(CartSaveReqVO updateReqVO) {
        validateExists(updateReqVO.getId());
        CartDO cartDO = new CartDO();
        BeanUtils.copyProperties(updateReqVO, cartDO);
        cartMapper.updateById(cartDO);
    }

    @Override
    public void deleteCart(Long id) {
        validateExists(id);
        cartMapper.deleteById(id);
    }

    @Override
    public CartRespVO getCart(Long id) {
        CartDO cartDO = cartMapper.selectById(id);
        CartRespVO respVO = new CartRespVO();
        BeanUtils.copyProperties(cartDO, respVO);
        return respVO;
    }

    @Override
    public PageResult<CartRespVO> getCartPage(CartPageReqVO pageReqVO) {
        PageResult<CartDO> pageResult = cartMapper.selectPage(pageReqVO,
                new LambdaQueryWrapperX<CartDO>()
                        .eqIfPresent(CartDO::getUserId, pageReqVO.getUserId())
                        .eqIfPresent(CartDO::getProductId, pageReqVO.getProductId())
                        .orderByDesc(CartDO::getId));
        List<CartRespVO> list = pageResult.getList().stream().map(cartDO -> {
            CartRespVO respVO = new CartRespVO();
            BeanUtils.copyProperties(cartDO, respVO);
            return respVO;
        }).collect(Collectors.toList());
        return new PageResult<>(list, pageResult.getTotal());
    }

    // ========== 用户端 ==========

    @Override
    public List<CartDO> getCartListByUserId(Long userId) {
        return cartMapper.selectList(
                new LambdaQueryWrapperX<CartDO>()
                        .eq(CartDO::getUserId, userId)
                        .orderByDesc(CartDO::getId));
    }

    @Override
    public Long addCart(Long userId, Long productId, Integer count) {
        // 校验商品是否上架
        ProductDO product = productService.getProductDO(productId);
        if (product == null || product.getStatus() != 0) {
            throw exception(CART_PRODUCT_OFF_SHELF);
        }
        // 校验库存
        if (product.getStock() < count) {
            throw exception(CART_PRODUCT_STOCK_NOT_ENOUGH);
        }
        // 查询是否已在购物车中（先清理同商品已软删除的记录，避免唯一键冲突）
        cartMapper.physicalDeleteByUserAndProduct(userId, productId);
        CartDO existing = cartMapper.selectOne(
                new LambdaQueryWrapperX<CartDO>()
                        .eq(CartDO::getUserId, userId)
                        .eq(CartDO::getProductId, productId));
        if (existing != null) {
            // 已存在，累加数量
            int newCount = existing.getCount() + count;
            if (product.getStock() < newCount) {
                throw exception(CART_PRODUCT_STOCK_NOT_ENOUGH);
            }
            existing.setCount(newCount);
            cartMapper.updateById(existing);
            return existing.getId();
        }
        // 不存在，新增
        CartDO cartDO = new CartDO();
        cartDO.setUserId(userId);
        cartDO.setProductId(productId);
        cartDO.setCount(count);
        cartDO.setSelected(true);
        cartMapper.insert(cartDO);
        return cartDO.getId();
    }

    @Override
    public void updateCartCount(Long userId, Long id, Integer count) {
        CartDO cartDO = validateOwnership(userId, id);
        // 校验库存
        ProductDO product = productService.getProductDO(cartDO.getProductId());
        if (product == null || product.getStock() < count) {
            throw exception(CART_PRODUCT_STOCK_NOT_ENOUGH);
        }
        cartDO.setCount(count);
        cartMapper.updateById(cartDO);
    }

    @Override
    public void deleteCart(Long userId, Long id) {
        validateOwnership(userId, id);
        cartMapper.deleteById(id);
    }

    private CartDO validateOwnership(Long userId, Long id) {
        CartDO cartDO = cartMapper.selectById(id);
        if (cartDO == null) {
            throw exception(CART_ITEM_NOT_EXISTS);
        }
        if (!cartDO.getUserId().equals(userId)) {
            throw exception(CART_ITEM_NOT_EXISTS);
        }
        return cartDO;
    }

    private void validateExists(Long id) {
        if (cartMapper.selectById(id) == null) {
            throw exception(CART_ITEM_NOT_EXISTS);
        }
    }

}
