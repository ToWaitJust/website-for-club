package cn.iocoder.yudao.module.club.controller.admin.club.vo;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 公告-保存 VO
 */
@Data
public class ClubNoticeSaveReqVO {

    private Long id;
    @NotEmpty(message = "公告标题不能为空")
    private String title;
    private String content;
    private LocalDateTime publishTime;
    private Integer status;

}
