package cn.iocoder.yudao.module.club.controller.admin.club;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.club.controller.admin.club.vo.ClubBusinessLinePageReqVO;
import cn.iocoder.yudao.module.club.controller.admin.club.vo.ClubBusinessLineRespVO;
import cn.iocoder.yudao.module.club.controller.admin.club.vo.ClubBusinessLineSaveReqVO;
import cn.iocoder.yudao.module.club.service.club.ClubBusinessLineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - 业务线
 */
@Tag(name = "管理后台 - 业务线")
@RestController
@RequestMapping("/club/business-line")
@Validated
public class ClubBusinessLineController {

    @Resource
    private ClubBusinessLineService businessLineService;

    @PostMapping("/create")
    @Operation(summary = "创建业务线")
    public CommonResult<Long> createBusinessLine(@Valid @RequestBody ClubBusinessLineSaveReqVO createReqVO) {
        return success(businessLineService.createBusinessLine(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新业务线")
    public CommonResult<Boolean> updateBusinessLine(@Valid @RequestBody ClubBusinessLineSaveReqVO updateReqVO) {
        businessLineService.updateBusinessLine(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除业务线")
    public CommonResult<Boolean> deleteBusinessLine(@RequestParam("id") Long id) {
        businessLineService.deleteBusinessLine(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得业务线")
    public CommonResult<ClubBusinessLineRespVO> getBusinessLine(@RequestParam("id") Long id) {
        return success(businessLineService.getBusinessLine(id));
    }

    @GetMapping("/list")
    @Operation(summary = "获得业务线列表")
    public CommonResult<List<ClubBusinessLineRespVO>> getBusinessLineList() {
        return success(businessLineService.getBusinessLineList());
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询业务线")
    public CommonResult<PageResult<ClubBusinessLineRespVO>> getBusinessLinePage(@Valid ClubBusinessLinePageReqVO pageReqVO) {
        return success(businessLineService.getBusinessLinePage(pageReqVO));
    }

}
