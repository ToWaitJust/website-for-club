package cn.iocoder.yudao.module.club.controller.admin.club.vo;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

/**
 * 业务线-保存 VO
 */
@Data
public class ClubBusinessLineSaveReqVO {

    private Long id;
    @NotEmpty(message = "业务线标识不能为空")
    private String slug;
    @NotEmpty(message = "业务线名称不能为空")
    private String name;
    private String tagline;
    private String contentMd;
    private Boolean registerOpen;
    private Integer sort;

}
