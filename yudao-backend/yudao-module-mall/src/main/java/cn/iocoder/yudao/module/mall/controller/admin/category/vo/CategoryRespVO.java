package cn.iocoder.yudao.module.mall.controller.admin.category.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 商品分类-响应 VO
 */
@Data
public class CategoryRespVO {

    private Long id;
    private String name;
    private Integer sort;
    private Integer status;
    private LocalDateTime createTime;

}
