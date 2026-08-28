package cn.iocoder.yudao.module.mall.enums;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;

/**
 * 商城错误码枚举
 * 使用 1-050-000-000 ~ 1-050-999-999 段
 */
public interface ErrorCodeConstants {

    // ========== 商品分类 1-050-001-000 ==========
    ErrorCode CATEGORY_NOT_EXISTS = new ErrorCode(1_050_001_000, "商品分类不存在");
    ErrorCode CATEGORY_HAS_PRODUCTS = new ErrorCode(1_050_001_001, "该分类下存在关联商品，无法删除，请先移除关联商品");

    // ========== 商品 1-050-002-000 ==========
    ErrorCode PRODUCT_NOT_EXISTS = new ErrorCode(1_050_002_000, "商品不存在");
    ErrorCode PRODUCT_STOCK_NEGATIVE = new ErrorCode(1_050_002_001, "商品库存不能为负数");
    ErrorCode PRODUCT_HAS_ORDERS = new ErrorCode(1_050_002_002, "存在有效订单的商品禁止删除，请改为下架");
    ErrorCode PRODUCT_STOCK_NOT_ENOUGH = new ErrorCode(1_050_002_003, "商品库存不足");

    // ========== 购物车 1-050-003-000 ==========
    ErrorCode CART_ITEM_NOT_EXISTS = new ErrorCode(1_050_003_000, "购物车项不存在");
    ErrorCode CART_PRODUCT_OFF_SHELF = new ErrorCode(1_050_003_001, "商品已下架，无法加入购物车");
    ErrorCode CART_PRODUCT_STOCK_NOT_ENOUGH = new ErrorCode(1_050_003_002, "商品库存不足");

    // ========== 订单 1-050-004-000 ==========
    ErrorCode ORDER_NOT_EXISTS = new ErrorCode(1_050_004_000, "订单不存在");
    ErrorCode ORDER_STATUS_NOT_PENDING = new ErrorCode(1_050_004_001, "订单状态不是待付款，无法操作");
    ErrorCode ORDER_ITEM_INVALID = new ErrorCode(1_050_004_002, "存在失效商品，无法下单");
    ErrorCode ORDER_CREATE_STOCK_NOT_ENOUGH = new ErrorCode(1_050_004_003, "商品库存不足，无法下单");
    ErrorCode ORDER_STATUS_NOT_ALLOWED = new ErrorCode(1_050_004_004, "订单状态不允许该操作");
    ErrorCode ORDER_PRODUCT_NOT_EXISTS = new ErrorCode(1_050_004_005, "商品不存在，无法下单");
    ErrorCode ORDER_PRODUCT_OFF_SHELF = new ErrorCode(1_050_004_006, "商品已下架，无法下单");

}
