package cn.iocoder.yudao.module.club.controller.admin.club.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 业务线-分页查询 VO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ClubBusinessLinePageReqVO extends PageParam {

    private String name;

}
