package cn.iocoder.yudao.module.club.service.club;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.club.controller.admin.club.vo.ClubBusinessLinePageReqVO;
import cn.iocoder.yudao.module.club.controller.admin.club.vo.ClubBusinessLineRespVO;
import cn.iocoder.yudao.module.club.controller.admin.club.vo.ClubBusinessLineSaveReqVO;

import java.util.List;

/**
 * 业务线 Service
 */
public interface ClubBusinessLineService {

    Long createBusinessLine(ClubBusinessLineSaveReqVO createReqVO);

    void updateBusinessLine(ClubBusinessLineSaveReqVO updateReqVO);

    void deleteBusinessLine(Long id);

    ClubBusinessLineRespVO getBusinessLine(Long id);

    List<ClubBusinessLineRespVO> getBusinessLineList();

    PageResult<ClubBusinessLineRespVO> getBusinessLinePage(ClubBusinessLinePageReqVO pageReqVO);

}
