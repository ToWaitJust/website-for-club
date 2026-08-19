package cn.iocoder.yudao.module.club.controller.admin.club.vo;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

/**
 * 报名-保存/创建 VO(公开接口入参)
 */
@Data
public class ClubRegisterSaveReqVO {

    @NotEmpty(message = "业务线不能为空")
    private String businessLine;
    @NotEmpty(message = "姓名不能为空")
    private String name;
    private String studentId;
    private String college;
    private String major;
    private String phone;
    private String wechat;
    private String motivation;

}
