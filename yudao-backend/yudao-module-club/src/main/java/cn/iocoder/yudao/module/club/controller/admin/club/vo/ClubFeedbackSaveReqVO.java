package cn.iocoder.yudao.module.club.controller.admin.club.vo;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

/**
 * 反馈-创建 VO(公开接口入参)
 */
@Data
public class ClubFeedbackSaveReqVO {

    @NotEmpty(message = "来源页面不能为空")
    private String page;
    @NotEmpty(message = "姓名不能为空")
    private String name;
    @NotEmpty(message = "反馈内容不能为空")
    private String content;

}
