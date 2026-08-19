package cn.iocoder.yudao.module.club.dal.dataobject.club;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 业务线 DO
 */
@TableName("club_business_line")
@Data
@EqualsAndHashCode(callSuper = true)
public class ClubBusinessLineDO extends BaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;
    /** 业务线标识: ai-it / ai-biz / ai-embed */
    private String slug;
    /** 业务线名称 */
    private String name;
    /** 一句话简介 */
    private String tagline;
    /** 简介 Markdown */
    private String contentMd;
    /** 报名是否开放: true=开放 */
    private Boolean registerOpen;
    /** 排序 */
    private Integer sort;

}
