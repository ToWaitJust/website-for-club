package cn.iocoder.yudao.module.mall.dal.mysql.mall;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.mall.dal.dataobject.mall.OrderItemDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单明细 Mapper
 */
@Mapper
public interface OrderItemMapper extends BaseMapperX<OrderItemDO> {
}
