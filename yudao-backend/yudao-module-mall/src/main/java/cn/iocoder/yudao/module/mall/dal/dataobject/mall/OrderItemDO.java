package cn.iocoder.yudao.module.mall.dal.dataobject.mall;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 订单明细 DO
 */
@TableName("mall_order_item")
@Data
@EqualsAndHashCode(callSuper = true)
public class OrderItemDO extends BaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 订单号
     */
    private String orderNo;
    /**
     * 商品ID
     */
    private Long productId;
    /**
     * 商品名称快照
     */
    private String productName;
    /**
     * 商品封面图快照
     */
    private String productCoverUrl;
    /**
     * 下单时单价快照
     */
    private BigDecimal price;
    /**
     * 购买数量
     */
    private Integer count;
    /**
     * 该商品小计金额
     */
    private BigDecimal totalAmount;

}
