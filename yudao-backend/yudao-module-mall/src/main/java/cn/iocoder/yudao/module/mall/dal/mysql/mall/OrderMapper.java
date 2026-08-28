package cn.iocoder.yudao.module.mall.dal.mysql.mall;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.mall.dal.dataobject.mall.OrderDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单 Mapper
 */
@Mapper
public interface OrderMapper extends BaseMapperX<OrderDO> {
}
