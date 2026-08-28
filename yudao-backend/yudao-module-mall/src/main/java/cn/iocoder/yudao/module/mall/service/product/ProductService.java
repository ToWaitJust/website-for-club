package cn.iocoder.yudao.module.mall.service.product;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.mall.controller.admin.product.vo.ProductPageReqVO;
import cn.iocoder.yudao.module.mall.controller.admin.product.vo.ProductRespVO;
import cn.iocoder.yudao.module.mall.controller.admin.product.vo.ProductSaveReqVO;
import cn.iocoder.yudao.module.mall.dal.dataobject.mall.ProductDO;

import java.util.List;

/**
 * 商品 Service
 */
public interface ProductService {

    Long createProduct(ProductSaveReqVO createReqVO);

    void updateProduct(ProductSaveReqVO updateReqVO);

    void deleteProduct(Long id);

    ProductRespVO getProduct(Long id);

    List<ProductRespVO> getProductList();

    PageResult<ProductRespVO> getProductPage(ProductPageReqVO pageReqVO);

    /**
     * 获取商品 DO（内部使用）
     */
    ProductDO getProductDO(Long id);

    /**
     * 扣减库存（原子操作，防止超卖）
     *
     * @param productId 商品编号
     * @param count     扣减数量
     * @return 是否扣减成功
     */
    boolean deductStock(Long productId, Integer count);

    /**
     * 恢复库存（取消订单时使用）
     *
     * @param productId 商品编号
     * @param count     恢复数量
     */
    void restoreStock(Long productId, Integer count);

    /**
     * 增加销量
     *
     * @param productId 商品编号
     * @param count     增加数量
     */
    void addSales(Long productId, Integer count);

    /**
     * 减少销量（取消订单时使用）
     *
     * @param productId 商品编号
     * @param count     减少数量
     */
    void decreaseSales(Long productId, Integer count);

}
