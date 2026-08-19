package cn.iocoder.yudao.module.club.controller.admin.club.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 业务线-响应 VO
 */
@Data
public class ClubBusinessLineRespVO {

    private Long id;
    private String slug;
    private String name;
    private String tagline;
    private String contentMd;
    private Boolean registerOpen;
    private Integer sort;
    private LocalDateTime createTime;

}
