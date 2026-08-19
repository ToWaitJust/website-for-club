package cn.iocoder.yudao.module.club.controller.admin.club.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 报名-分页查询 VO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ClubRegisterPageReqVO extends PageParam {

    private String businessLine;
    private Integer status;

}
