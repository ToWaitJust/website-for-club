package cn.iocoder.yudao.module.club.dal.dataobject.club;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 报名记录 DO
 * 公开接口创建(@PermitAll),后台分页/状态流转
 */
@TableName("club_register")
@Data
@EqualsAndHashCode(callSuper = true)
public class ClubRegisterDO extends BaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;
    /** 业务线 slug */
    private String businessLine;
    /** 姓名 */
    private String name;
    /** 学号 */
    private String studentId;
    /** 学院 */
    private String college;
    /** 专业 */
    private String major;
    /** 手机号 */
    private String phone;
    /** 微信号 */
    private String wechat;
    /** 报名动机 */
    private String motivation;
    /** 状态: 0=待处理 1=已联系 2=已录取 3=未录取 */
    private Integer status;

}
