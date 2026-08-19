package cn.iocoder.yudao.module.club.controller.admin.club.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 反馈-响应 VO
 */
@Data
public class ClubFeedbackRespVO {

    private Long id;
    private String page;
    private String name;
    private String content;
    private Integer status;
    private LocalDateTime createTime;

}
