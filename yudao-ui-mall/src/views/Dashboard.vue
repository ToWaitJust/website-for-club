<template>
  <div class="dashboard-page" v-loading="loading" element-loading-text="正在加载统计数据...">
    <h2 class="page-title">
      <el-icon><DataAnalysis /></el-icon>
      商城数据大盘
    </h2>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stat-cards">
      <el-col :span="6">
        <div class="stat-card card-blue">
          <div class="stat-icon">
            <el-icon :size="36"><Goods /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.productCount || 0 }}</div>
            <div class="stat-label">商品总数</div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card card-green">
          <div class="stat-icon">
            <el-icon :size="36"><Menu /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.categoryCount || 0 }}</div>
            <div class="stat-label">分类总数</div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card card-orange">
          <div class="stat-icon">
            <el-icon :size="36"><Document /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.orderCount || 0 }}</div>
            <div class="stat-label">订单总数</div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card card-red">
          <div class="stat-icon">
            <el-icon :size="36"><Money /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">¥{{ formatMoney(stats.totalSales) }}</div>
            <div class="stat-label">销售总额</div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 订单状态卡片 -->
    <el-row :gutter="20" class="order-status-cards">
      <el-col :span="8">
        <div class="status-card status-pending">
          <div class="status-num">{{ stats.pendingPaymentCount || 0 }}</div>
          <div class="status-text">待付款</div>
        </div>
      </el-col>
      <el-col :span="8">
        <div class="status-card status-shipping">
          <div class="status-num">{{ stats.pendingShipCount || 0 }}</div>
          <div class="status-text">待发货</div>
        </div>
      </el-col>
      <el-col :span="8">
        <div class="status-card status-completed">
          <div class="status-num">{{ stats.completedCount || 0 }}</div>
          <div class="status-text">已完成</div>
        </div>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="charts-row">
      <el-col :span="16">
        <div class="chart-card">
          <h3 class="chart-title">
            <el-icon><TrendCharts /></el-icon>
            近7天销售趋势
          </h3>
          <div ref="salesEl" class="chart"></div>
        </div>
      </el-col>
      <el-col :span="8">
        <div class="chart-card">
          <h3 class="chart-title">
            <el-icon><PieChart /></el-icon>
            订单状态分布
          </h3>
          <div ref="statusEl" class="chart chart-pie"></div>
        </div>
      </el-col>
    </el-row>

    <!-- 销量排行 -->
    <div class="chart-card">
      <h3 class="chart-title">
        <el-icon><Trophy /></el-icon>
        商品销量排行 TOP10
      </h3>
      <div ref="rankEl" class="chart chart-bar"></div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import { getStatistics } from '../api/mall'

const stats = ref({})
const loading = ref(true)

const salesEl = ref(null)
const statusEl = ref(null)
const rankEl = ref(null)

let salesInstance = null
let statusInstance = null
let rankInstance = null

const formatMoney = (val) => {
  if (!val) return '0.00'
  return Number(val).toFixed(2)
}

const renderSalesChart = (trend) => {
  if (!salesEl.value) return
  if (!salesInstance) salesInstance = echarts.init(salesEl.value)
  salesInstance.setOption({
    tooltip: { trigger: 'axis' },
    legend: { data: ['销售额', '订单数'], top: 0 },
    grid: { left: '3%', right: '4%', bottom: '3%', top: '40px', containLabel: true },
    xAxis: { type: 'category', data: trend.map(t => t.date), boundaryGap: false },
    yAxis: [
      { type: 'value', name: '销售额(元)' },
      { type: 'value', name: '订单数' }
    ],
    series: [
      {
        name: '销售额', type: 'line', smooth: true,
        data: trend.map(t => t.amount || 0),
        areaStyle: { color: 'rgba(64,158,255,0.15)' },
        lineStyle: { color: '#409eff', width: 2 },
        itemStyle: { color: '#409eff' }
      },
      {
        name: '订单数', type: 'line', smooth: true, yAxisIndex: 1,
        data: trend.map(t => t.orderCount || 0),
        lineStyle: { color: '#67c23a', width: 2 },
        itemStyle: { color: '#67c23a' }
      }
    ]
  })
}

const renderStatusChart = (distribution) => {
  if (!statusEl.value) return
  if (!statusInstance) statusInstance = echarts.init(statusEl.value)
  const colors = ['#e6a23c', '#409eff', '#67c23a', '#909399']
  statusInstance.setOption({
    tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
    legend: { orient: 'vertical', right: '5%', top: 'center' },
    series: [{
      type: 'pie', radius: ['45%', '70%'], center: ['35%', '50%'],
      label: { show: false },
      emphasis: { label: { show: true, fontSize: 14, fontWeight: 'bold' } },
      labelLine: { show: false },
      data: distribution.map((item, i) => ({
        value: item.count || 0, name: item.statusName,
        itemStyle: { color: colors[i % colors.length] }
      }))
    }]
  })
}

const renderRankChart = (rank) => {
  if (!rankEl.value) return
  if (!rankInstance) rankInstance = echarts.init(rankEl.value)
  rankInstance.setOption({
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    grid: { left: '3%', right: '4%', bottom: '3%', top: '20px', containLabel: true },
    xAxis: { type: 'value', name: '销量(件)' },
    yAxis: {
      type: 'category',
      data: rank.map(r => r.productName).reverse(),
      axisLabel: { width: 120, overflow: 'truncate' }
    },
    series: [{
      name: '销量', type: 'bar',
      data: rank.map(r => r.salesCount).reverse(),
      itemStyle: {
        color: { type: 'linear', x: 0, y: 0, x2: 1, y2: 0,
          colorStops: [{ offset: 0, color: '#667eea' }, { offset: 1, color: '#764ba2' }]
        },
        borderRadius: [0, 4, 4, 0]
      },
      barWidth: 20
    }]
  })
}

const handleResize = () => {
  salesInstance?.resize()
  statusInstance?.resize()
  rankInstance?.resize()
}

onMounted(async () => {
  loading.value = true
  try {
    const data = await getStatistics()
    stats.value = data
    await nextTick()
    renderSalesChart(data.salesTrend || [])
    renderStatusChart(data.orderStatusDistribution || [])
    if (data.productSalesRank && data.productSalesRank.length > 0) {
      renderRankChart(data.productSalesRank)
    }
  } catch (e) {
    console.error('获取统计数据失败:', e)
  } finally {
    loading.value = false
  }
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  salesInstance?.dispose()
  statusInstance?.dispose()
  rankInstance?.dispose()
})
</script>

<style scoped>
.dashboard-page {
  min-height: 100%;
}

.chart-error {
  padding: 60px 0;
}

.page-title {
  font-size: 24px;
  font-weight: 700;
  margin-bottom: 24px;
  display: flex;
  align-items: center;
  gap: 10px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.stat-cards {
  margin-bottom: 20px;
}

.stat-card {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  transition: transform 0.2s, box-shadow 0.2s;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.stat-icon {
  width: 64px;
  height: 64px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  flex-shrink: 0;
}

.card-blue .stat-icon { background: linear-gradient(135deg, #667eea, #764ba2); }
.card-green .stat-icon { background: linear-gradient(135deg, #11998e, #38ef7d); }
.card-orange .stat-icon { background: linear-gradient(135deg, #f093fb, #f5576c); }
.card-red .stat-icon { background: linear-gradient(135deg, #fa709a, #fee140); }

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #303133;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.order-status-cards {
  margin-bottom: 20px;
}

.status-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  text-align: center;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  border-left: 4px solid;
}

.status-pending { border-left-color: #e6a23c; }
.status-shipping { border-left-color: #409eff; }
.status-completed { border-left-color: #67c23a; }

.status-num {
  font-size: 32px;
  font-weight: 700;
  color: #303133;
  margin-bottom: 4px;
}

.status-text {
  font-size: 14px;
  color: #606266;
}

.charts-row {
  margin-bottom: 20px;
}

.chart-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  margin-bottom: 20px;
}

.chart-title {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  gap: 8px;
  color: #303133;
}

.chart {
  width: 100%;
  height: 320px;
}

.chart-pie {
  height: 280px;
}

.chart-bar {
  height: 360px;
}
</style>
