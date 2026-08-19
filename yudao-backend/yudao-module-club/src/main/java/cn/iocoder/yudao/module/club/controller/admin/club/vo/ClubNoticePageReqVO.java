package cn.iocoder.yudao.module.club.controller.admin.club.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 公告-分页查询 VO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ClubNoticePageReqVO extends PageParam {

    private String title;

}
