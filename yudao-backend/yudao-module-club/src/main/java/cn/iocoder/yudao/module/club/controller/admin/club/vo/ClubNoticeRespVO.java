package cn.iocoder.yudao.module.club.controller.admin.club.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 公告-响应 VO
 */
@Data
public class ClubNoticeRespVO {

    private Long id;
    private String title;
    private String content;
    private LocalDateTime publishTime;
    private Integer status;
    private LocalDateTime createTime;

}
