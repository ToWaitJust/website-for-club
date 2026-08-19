package cn.iocoder.yudao.module.club.controller.admin.club.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 反馈-分页查询 VO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ClubFeedbackPageReqVO extends PageParam {

    private String page;
    private Integer status;

}
