package cn.iocoder.yudao.module.mall.service.statistics;

import cn.iocoder.yudao.module.mall.controller.admin.statistics.vo.MallStatisticsRespVO;

/**
 * 商城统计 Service 接口
 */
public interface StatisticsService {

    /**
     * 获取商城统计数据
     *
     * @return 统计数据
     */
    MallStatisticsRespVO getStatistics();

}
