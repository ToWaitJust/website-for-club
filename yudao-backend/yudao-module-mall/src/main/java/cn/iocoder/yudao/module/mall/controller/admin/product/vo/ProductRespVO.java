package cn.iocoder.yudao.module.mall.controller.admin.product.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品-响应 VO
 */
@Data
public class ProductRespVO {

    private Long id;
    private Long categoryId;
    private String name;
    private String coverUrl;
    private String detailUrls;
    private BigDecimal price;
    private Integer stock;
    private Integer sales;
    private String description;
    private Integer status;
    private LocalDateTime createTime;

}
