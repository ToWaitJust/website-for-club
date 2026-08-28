package cn.iocoder.yudao.module.mall.dal.dataobject.mall;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商品分类 DO
 */
@TableName("mall_category")
@Data
@EqualsAndHashCode(callSuper = true)
public class CategoryDO extends BaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 分类名称
     */
    private String name;
    /**
     * 排序值(升序)
     */
    private Integer sort;
    /**
     * 启用状态: 0-禁用, 1-启用
     */
    private Integer status;

}
