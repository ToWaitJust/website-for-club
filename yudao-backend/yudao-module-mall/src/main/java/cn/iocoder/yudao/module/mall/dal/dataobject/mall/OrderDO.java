package cn.iocoder.yudao.module.mall.dal.dataobject.mall;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单主表 DO
 */
@TableName("mall_order")
@Data
@EqualsAndHashCode(callSuper = true)
public class OrderDO extends BaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 订单号
     */
    private String orderNo;
    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 订单总金额
     */
    private BigDecimal totalAmount;
    /**
     * 订单状态: 0-待付款, 1-待发货, 2-已完成, 3-已取消
     */
    private Integer status;
    /**
     * 用户端是否删除: 0-未删除, 1-已删除
     */
    private Boolean userDeleted;
    /**
     * 支付时间
     */
    private LocalDateTime payTime;
    /**
     * 完成时间
     */
    private LocalDateTime finishTime;
    /**
     * 取消时间
     */
    private LocalDateTime cancelTime;
    /**
     * 取消原因
     */
    private String cancelReason;

}
