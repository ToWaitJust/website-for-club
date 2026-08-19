package cn.iocoder.yudao.module.club.controller.admin.club;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.club.controller.admin.club.vo.ClubNoticePageReqVO;
import cn.iocoder.yudao.module.club.controller.admin.club.vo.ClubNoticeRespVO;
import cn.iocoder.yudao.module.club.controller.admin.club.vo.ClubNoticeSaveReqVO;
import cn.iocoder.yudao.module.club.service.club.ClubNoticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - 公告
 */
@Tag(name = "管理后台 - 公告")
@RestController
@RequestMapping("/club/notice")
@Validated
public class ClubNoticeController {

    @Resource
    private ClubNoticeService noticeService;

    @PostMapping("/create")
    @Operation(summary = "创建公告")
    public CommonResult<Long> createNotice(@Valid @RequestBody ClubNoticeSaveReqVO createReqVO) {
        return success(noticeService.createNotice(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新公告")
    public CommonResult<Boolean> updateNotice(@Valid @RequestBody ClubNoticeSaveReqVO updateReqVO) {
        noticeService.updateNotice(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除公告")
    public CommonResult<Boolean> deleteNotice(@RequestParam("id") Long id) {
        noticeService.deleteNotice(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得公告")
    public CommonResult<ClubNoticeRespVO> getNotice(@RequestParam("id") Long id) {
        return success(noticeService.getNotice(id));
    }

    @GetMapping("/list")
    @Operation(summary = "获得已发布公告列表")
    public CommonResult<List<ClubNoticeRespVO>> getNoticeList() {
        return success(noticeService.getNoticeList());
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询公告")
    public CommonResult<PageResult<ClubNoticeRespVO>> getNoticePage(@Valid ClubNoticePageReqVO pageReqVO) {
        return success(noticeService.getNoticePage(pageReqVO));
    }

}
