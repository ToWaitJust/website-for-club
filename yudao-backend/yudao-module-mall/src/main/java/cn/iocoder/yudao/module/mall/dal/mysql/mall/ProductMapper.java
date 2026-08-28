package cn.iocoder.yudao.module.mall.dal.mysql.mall;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.mall.dal.dataobject.mall.ProductDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 商品 Mapper
 */
@Mapper
public interface ProductMapper extends BaseMapperX<ProductDO> {

    /**
     * 原子扣减库存（防止超卖）
     *
     * @return 受影响行数，0 表示库存不足
     */
    @Update("UPDATE mall_product SET stock = stock - #{count} WHERE id = #{productId} AND stock >= #{count} AND deleted = 0")
    int deductStock(@Param("productId") Long productId, @Param("count") Integer count);

    /**
     * 恢复库存
     */
    @Update("UPDATE mall_product SET stock = stock + #{count} WHERE id = #{productId} AND deleted = 0")
    int restoreStock(@Param("productId") Long productId, @Param("count") Integer count);

    /**
     * 增加销量
     */
    @Update("UPDATE mall_product SET sales = sales + #{count} WHERE id = #{productId} AND deleted = 0")
    int addSales(@Param("productId") Long productId, @Param("count") Integer count);

    /**
     * 减少销量
     */
    @Update("UPDATE mall_product SET sales = sales - #{count} WHERE id = #{productId} AND sales >= #{count} AND deleted = 0")
    int decreaseSales(@Param("productId") Long productId, @Param("count") Integer count);

}
