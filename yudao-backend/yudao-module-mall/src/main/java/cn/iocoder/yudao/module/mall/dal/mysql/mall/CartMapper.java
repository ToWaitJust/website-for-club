package cn.iocoder.yudao.module.mall.dal.mysql.mall;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.mall.dal.dataobject.mall.CartDO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 购物车 Mapper
 */
@Mapper
public interface CartMapper extends BaseMapperX<CartDO> {

    @Select("SELECT * FROM mall_cart WHERE user_id = #{userId} AND product_id = #{productId} AND deleted = 1 LIMIT 1")
    CartDO selectDeletedByUserAndProduct(@Param("userId") Long userId, @Param("productId") Long productId);

    @Delete("DELETE FROM mall_cart WHERE user_id = #{userId} AND product_id = #{productId} AND deleted = 1")
    int physicalDeleteByUserAndProduct(@Param("userId") Long userId, @Param("productId") Long productId);
}
