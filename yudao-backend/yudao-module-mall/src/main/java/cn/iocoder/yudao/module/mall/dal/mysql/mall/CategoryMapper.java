package cn.iocoder.yudao.module.mall.dal.mysql.mall;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.mall.dal.dataobject.mall.CategoryDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品分类 Mapper
 */
@Mapper
public interface CategoryMapper extends BaseMapperX<CategoryDO> {
}
