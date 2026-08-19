package cn.iocoder.yudao.module.club.dal.dataobject.club;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 反馈记录 DO
 * 公开接口创建(@PermitAll),后台分页/已读标记
 */
@TableName("club_feedback")
@Data
@EqualsAndHashCode(callSuper = true)
public class ClubFeedbackDO extends BaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;
    /** 来源页面标识 */
    private String page;
    /** 姓名 */
    private String name;
    /** 反馈内容 */
    private String content;
    /** 状态: 0=待处理 1=已读 */
    private Integer status;

}
