package cn.iocoder.yudao.module.mall.dal.dataobject.mall;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 购物车 DO
 */
@TableName("mall_cart")
@Data
@EqualsAndHashCode(callSuper = true)
public class CartDO extends BaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 商品ID
     */
    private Long productId;
    /**
     * 购买数量
     */
    private Integer count;
    /**
     * 选中状态: 0-未选中, 1-选中
     */
    private Boolean selected;

}
