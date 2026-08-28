package cn.iocoder.yudao.module.mall.dal.dataobject.mall;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 商品 DO
 */
@TableName("mall_product")
@Data
@EqualsAndHashCode(callSuper = true)
public class ProductDO extends BaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 分类ID
     */
    private Long categoryId;
    /**
     * 商品名称
     */
    private String name;
    /**
     * 封面图URL
     */
    private String coverUrl;
    /**
     * 详情图URL(多个用逗号分隔)
     */
    private String detailUrls;
    /**
     * 售价
     */
    private BigDecimal price;
    /**
     * 库存数量
     */
    private Integer stock;
    /**
     * 销量
     */
    private Integer sales;
    /**
     * 商品简介
     */
    private String description;
    /**
     * 上架状态: 0-下架, 1-上架
     */
    private Integer status;

}
