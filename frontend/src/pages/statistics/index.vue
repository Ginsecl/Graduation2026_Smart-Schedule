<template>
  <div class="statistics-page page-container">
    <div class="stat-header">
      <h2 class="card-title">统计报表</h2>
      <el-button type="primary" :loading="loading" @click="loadStats" size="small">
        <el-icon><Refresh /></el-icon>刷新
      </el-button>
    </div>

    <el-row :gutter="20" class="overview-cards">
      <el-col :span="8" v-for="item in overviewCards" :key="item.label">
        <el-card shadow="hover">
          <div class="stat-card">
            <div class="stat-value" :style="{ color: item.color }">{{ item.value }}</div>
            <div class="stat-label">{{ item.label }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header><span>日程类型分布</span></template>
          <div ref="typeChartRef" style="height: 300px"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header><span>本周日程趋势</span></template>
          <div ref="weeklyChartRef" style="height: 300px"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick } from 'vue'
import { statisticsAPI } from '@/api'
import * as echarts from 'echarts'
import { Refresh } from '@element-plus/icons-vue'

const typeChartRef = ref<HTMLDivElement>()
const weeklyChartRef = ref<HTMLDivElement>()
const loading = ref(false)

const overviewCards = ref<{ label: string; value: number | string; color: string }[]>([
  { label: '总日程数', value: 0, color: '#409EFF' },
  { label: '已完成', value: 0, color: '#67C23A' },
  { label: '完成率', value: '0%', color: '#409EFF' },
  { label: '今日日程', value: 0, color: '#F56C6C' },
  { label: '重要日程', value: 0, color: '#FFD700' }
])

async function loadStats() {
  loading.value = true
  try {
    const [overviewRes, typeRes, weeklyRes] = await Promise.all([
      statisticsAPI.overview(),
      statisticsAPI.typeDistribution(),
      statisticsAPI.weekly()
    ])

    const overview = overviewRes.data.data
    overviewCards.value[0].value = overview.total
    overviewCards.value[1].value = overview.completed
    overviewCards.value[2].value = (overview.completionRate != null ? (overview.completionRate * 100).toFixed(0) + '%' : '0%')
    overviewCards.value[3].value = overview.todayCount
    overviewCards.value[4].value = overview.importantCount ?? 0

    await nextTick()

    if (typeChartRef.value) {
      const typeChart = echarts.init(typeChartRef.value)
      const dist = typeRes.data.data.distribution
      const names: Record<string, string> = {
        MEETING: '会议', TASK: '任务', DEADLINE: '截止日期',
        PERSONAL: '个人', BIRTHDAY: '生日', TRAVEL: '旅行', OTHER: '其他'
      }
      typeChart.setOption({
        tooltip: { trigger: 'item' },
        series: [{
          type: 'pie',
          radius: ['40%', '70%'],
          data: Object.entries(dist).map(([k, v]) => ({
            name: names[k] || k, value: v
          })),
          label: { show: true, formatter: '{b}: {c}' }
        }]
      })
    }

    if (weeklyChartRef.value) {
      const weeklyChart = echarts.init(weeklyChartRef.value)
      const dailyCount = weeklyRes.data.data.dailyCount
      weeklyChart.setOption({
        tooltip: { trigger: 'axis' },
        xAxis: { type: 'category', data: Object.keys(dailyCount) },
        yAxis: { type: 'value', minInterval: 1 },
        series: [{
          type: 'bar',
          data: Object.values(dailyCount),
          itemStyle: { color: '#409EFF', borderRadius: [4, 4, 0, 0] }
        }]
      })
    }
  } catch {
    // handled
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadStats()
})
</script>

<style scoped lang="scss">
.stat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 4px;
}

.overview-cards {
  .stat-card {
    text-align: center;
    padding: 10px 0;
  }

  .stat-value {
    font-size: 28px;
    font-weight: 700;
  }

  .stat-label {
    font-size: 12px;
    color: $text-secondary;
    margin-top: 4px;
  }
}
</style>