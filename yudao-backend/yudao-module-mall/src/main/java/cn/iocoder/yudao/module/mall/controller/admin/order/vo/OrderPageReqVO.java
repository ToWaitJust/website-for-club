package cn.iocoder.yudao.module.mall.controller.admin.order.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 订单-分页查询 VO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OrderPageReqVO extends PageParam {

    private String orderNo;
    private Long userId;
    private Integer status;

}
