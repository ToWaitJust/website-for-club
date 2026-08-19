package cn.iocoder.yudao.module.club.controller.admin.club;

import jakarta.annotation.security.PermitAll;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.club.controller.admin.club.vo.ClubFeedbackPageReqVO;
import cn.iocoder.yudao.module.club.controller.admin.club.vo.ClubFeedbackRespVO;
import cn.iocoder.yudao.module.club.controller.admin.club.vo.ClubFeedbackSaveReqVO;
import cn.iocoder.yudao.module.club.controller.admin.club.vo.ClubFeedbackUpdateStatusReqVO;
import cn.iocoder.yudao.module.club.service.club.ClubFeedbackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - 反馈
 * 契约: docs/04-API-CONTRACT.md §2.2
 */
@Tag(name = "管理后台 - 反馈")
@RestController
@RequestMapping("/club/feedback")
@Validated
public class ClubFeedbackController {

    @Resource
    private ClubFeedbackService feedbackService;

    /** 创建反馈(公开接口,门户调用,无需登录) */
    @PostMapping("")
    @PermitAll
    @Operation(summary = "创建反馈")
    public CommonResult<Boolean> createFeedback(@Valid @RequestBody ClubFeedbackSaveReqVO createReqVO) {
        feedbackService.createFeedback(createReqVO);
        return success(true);
    }

    /** 更新反馈状态(后台,标记已读) */
    @PutMapping("/update-status")
    @Operation(summary = "更新反馈状态")
    public CommonResult<Boolean> updateFeedbackStatus(@Valid @RequestBody ClubFeedbackUpdateStatusReqVO updateReqVO) {
        feedbackService.updateFeedbackStatus(updateReqVO);
        return success(true);
    }

    /** 分页查询反馈(后台) */
    @GetMapping("/page")
    @Operation(summary = "分页查询反馈")
    public CommonResult<PageResult<ClubFeedbackRespVO>> getFeedbackPage(@Valid ClubFeedbackPageReqVO pageReqVO) {
        return success(feedbackService.getFeedbackPage(pageReqVO));
    }

}
