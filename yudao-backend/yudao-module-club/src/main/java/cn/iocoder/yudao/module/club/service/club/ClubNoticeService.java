package cn.iocoder.yudao.module.club.service.club;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.club.controller.admin.club.vo.ClubNoticePageReqVO;
import cn.iocoder.yudao.module.club.controller.admin.club.vo.ClubNoticeRespVO;
import cn.iocoder.yudao.module.club.controller.admin.club.vo.ClubNoticeSaveReqVO;

import java.util.List;

/**
 * 公告 Service
 */
public interface ClubNoticeService {

    Long createNotice(ClubNoticeSaveReqVO createReqVO);

    void updateNotice(ClubNoticeSaveReqVO updateReqVO);

    void deleteNotice(Long id);

    ClubNoticeRespVO getNotice(Long id);

    List<ClubNoticeRespVO> getNoticeList();

    PageResult<ClubNoticeRespVO> getNoticePage(ClubNoticePageReqVO pageReqVO);

}
