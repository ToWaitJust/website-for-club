package cn.iocoder.yudao.module.club.dal.dataobject.club;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 公告 DO
 */
@TableName("club_notice")
@Data
@EqualsAndHashCode(callSuper = true)
public class ClubNoticeDO extends BaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;
    /** 标题 */
    private String title;
    /** 内容 */
    private String content;
    /** 发布时间 */
    private LocalDateTime publishTime;
    /** 状态: 1=发布 0=下架 */
    private Integer status;

}
