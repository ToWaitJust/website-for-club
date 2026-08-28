package cn.iocoder.yudao.module.mall.controller.admin.statistics;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.mall.controller.admin.statistics.vo.MallStatisticsRespVO;
import cn.iocoder.yudao.module.mall.service.statistics.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 商城统计")
@RestController
@RequestMapping("/mall/statistics")
@Validated
public class StatisticsController {

    @Resource
    private StatisticsService statisticsService;

    @GetMapping("/overview")
    @Operation(summary = "获取商城统计概览")
    public CommonResult<MallStatisticsRespVO> getStatistics() {
        return success(statisticsService.getStatistics());
    }

}
