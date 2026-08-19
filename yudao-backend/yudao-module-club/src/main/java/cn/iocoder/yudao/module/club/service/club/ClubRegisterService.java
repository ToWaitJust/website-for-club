package cn.iocoder.yudao.module.club.service.club;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.club.controller.admin.club.vo.ClubRegisterPageReqVO;
import cn.iocoder.yudao.module.club.controller.admin.club.vo.ClubRegisterRespVO;
import cn.iocoder.yudao.module.club.controller.admin.club.vo.ClubRegisterSaveReqVO;
import cn.iocoder.yudao.module.club.controller.admin.club.vo.ClubRegisterUpdateStatusReqVO;

/**
 * 报名 Service
 */
public interface ClubRegisterService {

    /** 创建报名(公开接口) */
    Long createRegister(ClubRegisterSaveReqVO createReqVO);

    /** 更新报名状态(后台) */
    void updateRegisterStatus(ClubRegisterUpdateStatusReqVO updateReqVO);

    /** 分页查询报名(后台) */
    PageResult<ClubRegisterRespVO> getRegisterPage(ClubRegisterPageReqVO pageReqVO);

}
