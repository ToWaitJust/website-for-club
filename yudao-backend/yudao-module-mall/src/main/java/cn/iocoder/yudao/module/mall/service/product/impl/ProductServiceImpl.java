package cn.iocoder.yudao.module.mall.service.product.impl;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.mall.controller.admin.product.vo.ProductPageReqVO;
import cn.iocoder.yudao.module.mall.controller.admin.product.vo.ProductRespVO;
import cn.iocoder.yudao.module.mall.controller.admin.product.vo.ProductSaveReqVO;
import cn.iocoder.yudao.module.mall.dal.dataobject.mall.OrderItemDO;
import cn.iocoder.yudao.module.mall.dal.dataobject.mall.ProductDO;
import cn.iocoder.yudao.module.mall.dal.mysql.mall.OrderItemMapper;
import cn.iocoder.yudao.module.mall.dal.mysql.mall.ProductMapper;
import cn.iocoder.yudao.module.mall.service.product.ProductService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.mall.enums.ErrorCodeConstants.*;

/**
 * 商品 Service 实现
 */
@Service
public class ProductServiceImpl implements ProductService {

    @Resource
    private ProductMapper productMapper;
    @Resource
    private OrderItemMapper orderItemMapper;

    @Override
    public Long createProduct(ProductSaveReqVO createReqVO) {
        validateStock(createReqVO.getStock());
        ProductDO productDO = new ProductDO();
        BeanUtils.copyProperties(createReqVO, productDO);
        productDO.setSales(0);
        productMapper.insert(productDO);
        return productDO.getId();
    }

    @Override
    public void updateProduct(ProductSaveReqVO updateReqVO) {
        validateExists(updateReqVO.getId());
        validateStock(updateReqVO.getStock());
        ProductDO productDO = new ProductDO();
        BeanUtils.copyProperties(updateReqVO, productDO);
        productMapper.updateById(productDO);
    }

    @Override
    public void deleteProduct(Long id) {
        validateExists(id);
        Long orderCount = orderItemMapper.selectCount(
                new LambdaQueryWrapperX<OrderItemDO>().eq(OrderItemDO::getProductId, id));
        if (orderCount > 0) {
            throw exception(PRODUCT_HAS_ORDERS);
        }
        productMapper.deleteById(id);
    }

    @Override
    public ProductRespVO getProduct(Long id) {
        ProductDO productDO = productMapper.selectById(id);
        ProductRespVO respVO = new ProductRespVO();
        BeanUtils.copyProperties(productDO, respVO);
        return respVO;
    }

    @Override
    public List<ProductRespVO> getProductList() {
        List<ProductDO> list = productMapper.selectList(null);
        return list.stream().map(productDO -> {
            ProductRespVO respVO = new ProductRespVO();
            BeanUtils.copyProperties(productDO, respVO);
            return respVO;
        }).collect(Collectors.toList());
    }

    @Override
    public PageResult<ProductRespVO> getProductPage(ProductPageReqVO pageReqVO) {
        PageResult<ProductDO> pageResult = productMapper.selectPage(pageReqVO,
                new LambdaQueryWrapperX<ProductDO>()
                        .likeIfPresent(ProductDO::getName, pageReqVO.getName())
                        .eqIfPresent(ProductDO::getCategoryId, pageReqVO.getCategoryId())
                        .eqIfPresent(ProductDO::getStatus, pageReqVO.getStatus())
                        .orderByDesc(ProductDO::getId));
        List<ProductRespVO> list = pageResult.getList().stream().map(productDO -> {
            ProductRespVO respVO = new ProductRespVO();
            BeanUtils.copyProperties(productDO, respVO);
            return respVO;
        }).collect(Collectors.toList());
        return new PageResult<>(list, pageResult.getTotal());
    }

    @Override
    public ProductDO getProductDO(Long id) {
        return productMapper.selectById(id);
    }

    @Override
    public boolean deductStock(Long productId, Integer count) {
        int rows = productMapper.deductStock(productId, count);
        return rows > 0;
    }

    @Override
    public void restoreStock(Long productId, Integer count) {
        productMapper.restoreStock(productId, count);
    }

    @Override
    public void addSales(Long productId, Integer count) {
        productMapper.addSales(productId, count);
    }

    @Override
    public void decreaseSales(Long productId, Integer count) {
        productMapper.decreaseSales(productId, count);
    }

    private void validateExists(Long id) {
        if (productMapper.selectById(id) == null) {
            throw exception(PRODUCT_NOT_EXISTS);
        }
    }

    private void validateStock(Integer stock) {
        if (stock != null && stock < 0) {
            throw exception(PRODUCT_STOCK_NEGATIVE);
        }
    }

}
