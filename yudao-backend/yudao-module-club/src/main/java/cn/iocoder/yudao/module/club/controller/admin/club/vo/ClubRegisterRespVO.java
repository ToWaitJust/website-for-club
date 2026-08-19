package cn.iocoder.yudao.module.club.controller.admin.club.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 报名-响应 VO
 */
@Data
public class ClubRegisterRespVO {

    private Long id;
    private String businessLine;
    private String name;
    private String studentId;
    private String college;
    private String major;
    private String phone;
    private String wechat;
    private String motivation;
    private Integer status;
    private LocalDateTime createTime;

}
