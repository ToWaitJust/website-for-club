package cn.iocoder.yudao.module.club.controller.admin.club;

import jakarta.annotation.security.PermitAll;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.club.controller.admin.club.vo.ClubRegisterPageReqVO;
import cn.iocoder.yudao.module.club.controller.admin.club.vo.ClubRegisterRespVO;
import cn.iocoder.yudao.module.club.controller.admin.club.vo.ClubRegisterSaveReqVO;
import cn.iocoder.yudao.module.club.controller.admin.club.vo.ClubRegisterUpdateStatusReqVO;
import cn.iocoder.yudao.module.club.service.club.ClubRegisterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - 报名
 * 契约: docs/04-API-CONTRACT.md §2.1
 */
@Tag(name = "管理后台 - 报名")
@RestController
@RequestMapping("/club/register")
@Validated
public class ClubRegisterController {

    @Resource
    private ClubRegisterService registerService;

    /** 创建报名(公开接口,门户调用,无需登录) */
    @PostMapping("")
    @PermitAll
    @Operation(summary = "创建报名")
    public CommonResult<Long> createRegister(@Valid @RequestBody ClubRegisterSaveReqVO createReqVO) {
        return success(registerService.createRegister(createReqVO));
    }

    /** 更新报名状态(后台) */
    @PutMapping("/update-status")
    @Operation(summary = "更新报名状态")
    public CommonResult<Boolean> updateRegisterStatus(@Valid @RequestBody ClubRegisterUpdateStatusReqVO updateReqVO) {
        registerService.updateRegisterStatus(updateReqVO);
        return success(true);
    }

    /** 分页查询报名(后台) */
    @GetMapping("/page")
    @Operation(summary = "分页查询报名")
    public CommonResult<PageResult<ClubRegisterRespVO>> getRegisterPage(@Valid ClubRegisterPageReqVO pageReqVO) {
        return success(registerService.getRegisterPage(pageReqVO));
    }

}
