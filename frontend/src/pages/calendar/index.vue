<template>
  <div class="calendar-page">
    <div class="calendar-header">
      <div class="header-left">
        <el-button-group>
          <el-button :type="viewMode === 'month' ? 'primary' : ''" @click="viewMode = 'month'">月</el-button>
          <el-button :type="viewMode === 'week' ? 'primary' : ''" @click="viewMode = 'week'">周</el-button>
          <el-button :type="viewMode === 'day' ? 'primary' : ''" @click="viewMode = 'day'">日</el-button>
          <el-button :type="viewMode === 'list' ? 'primary' : ''" @click="viewMode = 'list'">列表</el-button>
        </el-button-group>
        <el-button-group class="nav-btns">
          <el-button @click="navigatePrev"><el-icon><ArrowLeft /></el-icon></el-button>
          <el-button @click="goToday">今天</el-button>
          <el-button @click="navigateNext"><el-icon><ArrowRight /></el-icon></el-button>
        </el-button-group>
        <h2 class="date-title">{{ dateTitle }}</h2>
      </div>
      <div class="header-right">
        <el-button type="primary" @click="showCreateDialog = true">
          <el-icon><Plus /></el-icon>新建日程
        </el-button>
      </div>
    </div>

    <div class="nlp-bar">
      <el-input
        v-model="nlpInput"
        placeholder="试试说：明天下午3点开会讨论项目进展"
        @keyup.enter="handleNlpParse"
      >
        <template #prefix>
          <el-icon color="#909399"><MagicStick /></el-icon>
        </template>
        <template #append>
          <el-button :loading="nlpLoading" @click="handleNlpParse">智能解析</el-button>
        </template>
      </el-input>
    </div>

    <div class="calendar-body" v-loading="scheduleStore.loading">
      <div v-if="viewMode === 'month'" class="month-view">
        <div class="weekday-header">
          <div v-for="d in weekdays" :key="d" class="weekday-cell">{{ d }}</div>
        </div>
        <div class="month-grid">
          <div v-for="(day, idx) in monthDays" :key="idx"
            class="day-cell"
            :class="{
              'other-month': day.otherMonth,
              'is-today': day.isToday,
              'is-weekend': day.isWeekend
            }"
            @click="selectDay(day.date)"
          >
            <span class="day-num">{{ day.dayNum }}</span>
            <span class="lunar-label" v-if="day.lunarDate">{{ day.lunarDate }}</span>
            <span class="holiday-label" v-if="getHolidayName(day.date)">{{ getHolidayName(day.date) }}</span>
            <div class="day-schedules">
              <div v-for="s in getDaySchedules(day.date, 3)" :key="s.id"
                class="schedule-dot"
                :class="{ 'is-important': s.important, 'is-completed': s.status === 'COMPLETED' }"
                :style="{ background: getScheduleColor(s) }"
                @click.stop="openScheduleDetail(s)"
              >
                <el-icon v-if="s.important" class="star-icon"><StarFilled /></el-icon>
                {{ s.title }}
              </div>
              <div v-if="getDaySchedules(day.date).length > 3" class="more-schedules">
                +{{ getDaySchedules(day.date).length - 3 }}
              </div>
            </div>
          </div>
        </div>
      </div>

      <div v-else-if="viewMode === 'week'" class="time-view">
        <div class="time-view-header-row">
          <div class="time-gutter"></div>
          <div v-for="day in weekDays" :key="day.date"
            class="day-col-header"
            :class="{ 'is-today': day.isToday }">
            <span class="day-name">{{ day.dayName }}</span>
            <span class="day-num-text">{{ day.dayNum }}</span>
          </div>
        </div>
        <div class="time-view-body">
          <div v-for="h in 24" :key="h" class="time-row">
            <span class="time-label">{{ String(h - 1).padStart(2, '0') }}:00</span>
            <div v-for="day in weekDays" :key="day.date" class="time-slot"></div>
          </div>
          <div class="schedule-overlay">
            <div v-for="day in weekDays" :key="'overlay-' + day.date"
              class="day-overlay-col"
              :style="{ width: (100 / 7) + '%', left: ((weekDays.indexOf(day)) * 100 / 7) + '%' }">
              <div v-for="s in getDaySchedules(day.date)" :key="s.id"
                class="schedule-block"
                :class="{ 'is-important-block': s.important, 'is-completed-block': s.status === 'COMPLETED' }"
                :style="getScheduleBlockStyle(s)"
                @click="openScheduleDetail(s)">
                <span class="block-time">{{ dayjs(s.startTime).format('HH:mm') }}</span>
                <span class="block-title">
                  <el-icon v-if="s.important" class="star-icon"><StarFilled /></el-icon>
                  {{ s.title }}
                </span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div v-else-if="viewMode === 'day'" class="time-view">
        <div class="time-view-body">
          <div class="day-schedule-overlay">
            <div v-for="s in getDaySchedules(currentDate.format('YYYY-MM-DD'))" :key="s.id"
              class="schedule-block day-block"
              :class="{ 'is-important-block': s.important, 'is-completed-block': s.status === 'COMPLETED' }"
              :style="getScheduleBlockStyle(s)"
              @click="openScheduleDetail(s)">
              <span class="block-time">{{ dayjs(s.startTime).format('HH:mm') }}</span>
              <span class="block-title">
                <el-icon v-if="s.important" class="star-icon"><StarFilled /></el-icon>
                {{ s.title }}
              </span>
              <span class="block-desc" v-if="s.location">{{ s.location }}</span>
            </div>
          </div>
          <div v-for="h in 24" :key="h" class="time-row">
            <span class="time-label">{{ String(h - 1).padStart(2, '0') }}:00</span>
            <div class="time-slot"></div>
          </div>
        </div>
      </div>

      <div v-else-if="viewMode === 'list'" class="list-view">
        <div class="list-toolbar">
          <el-input v-model="listSearch" placeholder="搜索日程..." clearable class="search-input" />
          <el-select v-model="listTypeFilter" placeholder="类型筛选" clearable class="filter-select">
            <el-option v-for="t in scheduleTypes" :key="t.value" :label="t.label" :value="t.value" />
          </el-select>
          <el-select v-model="listStatusFilter" placeholder="状态筛选" clearable class="filter-select">
            <el-option label="待安排" value="SCHEDULED" />
            <el-option label="已完成" value="COMPLETED" />
          </el-select>
          <el-button @click="listSortAsc = !listSortAsc">
            {{ listSortAsc ? '时间↑' : '时间↓' }}
          </el-button>
        </div>
        <div class="list-body">
          <div v-for="s in filteredSchedules" :key="s.id"
            class="list-item"
            :class="{ 'is-important-item': s.important, 'is-completed-item': s.status === 'COMPLETED' }"
            @click="openScheduleDetail(s)"
          >
            <span class="item-time">{{ dayjs(s.startTime).format('MM-DD HH:mm') }}</span>
            <span class="item-title">
              <el-icon v-if="s.important" class="star-icon"><StarFilled /></el-icon>
              {{ s.title }}
            </span>
            <el-tag :color="getTypeColor(s.type)" effect="dark" size="small">{{ s.type }}</el-tag>
            <el-tag size="small" :type="s.status === 'COMPLETED' ? 'success' : 'info'">
              {{ s.status === 'COMPLETED' ? '已完成' : '待安排' }}
            </el-tag>
          </div>
          <el-empty v-if="filteredSchedules.length === 0" description="暂无日程" />
        </div>
      </div>
    </div>

    <el-dialog v-model="showCreateDialog" :title="editingScheduleId ? '编辑日程' : '新建日程'" width="560px" destroy-on-close @closed="resetForm">
      <el-form :model="scheduleForm" label-width="90px">
        <el-form-item label="标题" required>
          <el-input v-model="scheduleForm.title" placeholder="日程标题" />
        </el-form-item>
        <el-form-item label="时间">
          <el-date-picker
            v-model="scheduleTimeRange"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            style="width: 100%"
            value-format="YYYY-MM-DDTHH:mm:ss"
          />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="scheduleForm.type" style="width: 100%">
            <el-option v-for="t in scheduleTypes" :key="t.value" :label="t.label" :value="t.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="重要日程">
          <el-switch v-model="scheduleForm.important" active-text="是" inactive-text="否" />
        </el-form-item>
        <el-form-item label="地点">
          <el-input v-model="scheduleForm.location" placeholder="地点（可选）" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="scheduleForm.description" type="textarea" :rows="3" placeholder="详细描述（可选）" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreateDialog = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleCreateSchedule">
          {{ editingScheduleId ? '保存' : '创建' }}
        </el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showDetailDialog" title="日程详情" width="480px">
      <template v-if="selectedSchedule">
        <div class="detail-item">
          <label>标题</label>
          <span>{{ selectedSchedule.title }}</span>
        </div>
        <div class="detail-item">
          <label>时间</label>
          <span>{{ selectedSchedule.startTime }} ~ {{ selectedSchedule.endTime }}</span>
        </div>
        <div class="detail-item">
          <label>类型</label>
          <el-tag :color="getTypeColor(selectedSchedule.type)" effect="dark" size="small">
            {{ selectedSchedule.type }}
          </el-tag>
        </div>
        <div class="detail-item">
          <label>状态</label>
          <el-tag size="small" :type="selectedSchedule.status === 'COMPLETED' ? 'success' : 'info'">
            {{ selectedSchedule.status === 'COMPLETED' ? '已完成' : selectedSchedule.status }}
          </el-tag>
        </div>
        <div class="detail-item" v-if="selectedSchedule.location">
          <label>地点</label>
          <span>{{ selectedSchedule.location }}</span>
        </div>
        <div class="detail-item" v-if="selectedSchedule.description">
          <label>描述</label>
          <span>{{ selectedSchedule.description }}</span>
        </div>
      </template>
      <template #footer>
        <el-button
          v-if="selectedSchedule && selectedSchedule.status !== 'COMPLETED'"
          type="success"
          @click="handleCompleteSchedule"
        >标记完成</el-button>
        <el-button
          v-if="selectedSchedule && selectedSchedule.status === 'COMPLETED'"
          type="warning"
          @click="handleUncompleteSchedule"
        >取消完成</el-button>
        <el-button type="warning" @click="handleEditSchedule">编辑</el-button>
        <el-button type="danger" @click="handleDeleteSchedule">删除</el-button>
        <el-button @click="showDetailDialog = false">关闭</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showConflictDialog" title="检测到日程冲突" width="640px" destroy-on-close :close-on-click-modal="false">
      <div class="conflict-warning">
        <el-alert
          title="当前日程与已有日程存在时间重叠，请选择处理方案"
          type="warning"
          :closable="false"
          show-icon
        />
      </div>
      <div class="conflict-list" v-if="conflictResult.conflicts.length">
        <div v-for="c in conflictResult.conflicts" :key="c.scheduleId" class="conflict-item">
          <el-tag :color="getTypeColor(c.type)" effect="dark" size="small">{{ c.type }}</el-tag>
          <span class="conflict-title">{{ c.title }}</span>
          <span class="conflict-time">{{ dayjs(c.startTime).format('HH:mm') }} - {{ dayjs(c.endTime).format('HH:mm') }}</span>
          <el-tag size="small" :type="overlapTypeTag(c.overlapType)">{{ overlapTypeLabel(c.overlapType) }}</el-tag>
        </div>
      </div>
      <div class="resolution-section" v-if="conflictResult.suggestions.length">
        <h4 class="resolution-title">解决方案</h4>
        <div class="resolution-grid">
          <div
            v-for="s in conflictResult.suggestions"
            :key="s.strategy"
            class="resolution-card"
            :class="{ selected: selectedResolution === s.strategy }"
            @click="selectedResolution = s.strategy"
          >
            <div class="resolution-card-header">
              <span class="resolution-label">{{ s.label }}</span>
              <el-icon v-if="selectedResolution === s.strategy" color="#409EFF"><Check /></el-icon>
            </div>
            <p class="resolution-desc">{{ s.description }}</p>
            <p class="resolution-time" v-if="s.adjustedStartTime && s.adjustedEndTime">
              {{ dayjs(s.adjustedStartTime).format('MM-DD HH:mm') }} ~ {{ dayjs(s.adjustedEndTime).format('HH:mm') }}
              （{{ s.adjustedDurationMinutes }}分钟）
            </p>
          </div>
        </div>
      </div>
      <template #footer>
        <el-button @click="handleCancelConflict">取消创建</el-button>
        <el-button type="warning" @click="handleIgnoreConflict">忽略冲突仍然创建</el-button>
        <el-button type="primary" :disabled="!selectedResolution" @click="handleApplyResolution">
          选择方案
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useScheduleStore } from '@/stores/schedule'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Check } from '@element-plus/icons-vue'
import type { Schedule, ConflictCheckResult, ResolutionSuggestion } from '@/types'
import { scheduleAPI, nlpAPI, holidayAPI } from '@/api'
import dayjs from 'dayjs'
import lunisolar from 'lunisolar'

const scheduleStore = useScheduleStore()
const viewMode = ref<'month' | 'week' | 'day' | 'list'>('month')
const currentDate = ref(dayjs())
const nlpInput = ref('')
const nlpLoading = ref(false)

const showCreateDialog = ref(false)
const showDetailDialog = ref(false)
const showConflictDialog = ref(false)
const selectedSchedule = ref<Schedule | null>(null)
const editingScheduleId = ref<number | null>(null)
const saving = ref(false)

const conflictResult = ref<ConflictCheckResult>({ hasConflict: false, conflicts: [], suggestions: [] })
const selectedResolution = ref<string>('')
const pendingScheduleData = ref<{ startTime: string; endTime: string; [key: string]: unknown } | null>(null)
const pendingNlpSchedule = ref<{ title: string; description: string; startTime: string; endTime: string; type: string; location: string; participants: string; importance: number } | null>(null)

const holidayMap = ref<Record<string, string>>({})

const scheduleForm = ref({
  title: '', type: 'OTHER', important: false, location: '', description: ''
})
const scheduleTimeRange = ref<string[]>([])

const listSearch = ref('')
const listTypeFilter = ref('')
const listStatusFilter = ref('')
const listSortAsc = ref(true)

const filteredSchedules = computed(() => {
  let list = scheduleStore.schedules
  if (listSearch.value) {
    const q = listSearch.value.toLowerCase()
    list = list.filter(s => s.title.toLowerCase().includes(q))
  }
  if (listTypeFilter.value) {
    list = list.filter(s => s.type === listTypeFilter.value)
  }
  if (listStatusFilter.value) {
    list = list.filter(s => s.status === listStatusFilter.value)
  }
  return [...list].sort((a, b) => {
    const diff = dayjs(a.startTime).valueOf() - dayjs(b.startTime).valueOf()
    return listSortAsc.value ? diff : -diff
  })
})

const scheduleTypes = [
  { label: '会议', value: 'MEETING' },
  { label: '任务', value: 'TASK' },
  { label: '截止日期', value: 'DEADLINE' },
  { label: '个人', value: 'PERSONAL' },
  { label: '生日', value: 'BIRTHDAY' },
  { label: '旅行', value: 'TRAVEL' },
  { label: '其他', value: 'OTHER' }
]

const typeColors: Record<string, string> = {
  MEETING: '#409EFF', TASK: '#67C23A', DEADLINE: '#F56C6C',
  PERSONAL: '#E6A23C', BIRTHDAY: '#FF85C0', TRAVEL: '#36CFC9', OTHER: '#909399'
}

function getTypeColor(type: string) {
  return typeColors[type] || '#909399'
}

function getScheduleColor(s: Schedule) {
  if (s.status === 'COMPLETED') return '#67C23A'
  return getTypeColor(s.type)
}

function getHolidayName(date: string) {
  return holidayMap.value[date] || ''
}

function getLunarDate(dateStr: string) {
  try {
    const d = lunisolar(dateStr)
    const lunar = d.lunar
    if (!lunar) return ''
    const monthName = lunar.getMonthName()
    const dayName = lunar.getDayName()
    if (dayName === '初一') return monthName
    return dayName
  } catch {
    return ''
  }
}

async function fetchHolidays() {
  try {
    const year = currentDate.value.year()
    const res = await holidayAPI.list(year)
    const list = res.data.data as Array<{ date: string; name: string; type: string }>
    const map: Record<string, string> = {}
    list.forEach(h => {
      map[h.date] = h.name
    })
    holidayMap.value = map
  } catch {
    // holidays are optional, silent failure
  }
}

const weekdays = ['日', '一', '二', '三', '四', '五', '六']

const weekDays = computed(() => {
  const d = currentDate.value
  const start = d.startOf('week')
  const today = dayjs().format('YYYY-MM-DD')
  const days = []
  for (let i = 0; i < 7; i++) {
    const day = start.add(i, 'day')
    const dateStr = day.format('YYYY-MM-DD')
    days.push({
      date: dateStr,
      dayName: weekdays[i],
      dayNum: day.date(),
      isToday: dateStr === today
    })
  }
  return days
})

const dateTitle = computed(() => {
  const d = currentDate.value
  switch (viewMode.value) {
    case 'month': return d.format('YYYY年 M月')
    case 'week': return `${d.startOf('week').format('M月D日')} - ${d.endOf('week').format('M月D日')}`
    case 'day': return d.format('YYYY年M月D日')
  }
})

const monthDays = computed(() => {
  const d = currentDate.value
  const start = d.startOf('month').startOf('week')
  const end = d.endOf('month').endOf('week')
  const today = dayjs().format('YYYY-MM-DD')
  const days = []
  let cursor = start
  while (cursor.isBefore(end) || cursor.isSame(end, 'day')) {
    const dateStr = cursor.format('YYYY-MM-DD')
    days.push({
      date: dateStr,
      dayNum: cursor.date(),
      otherMonth: cursor.month() !== d.month(),
      isToday: dateStr === today,
      isWeekend: cursor.day() === 0 || cursor.day() === 6,
      lunarDate: getLunarDate(dateStr)
    })
    cursor = cursor.add(1, 'day')
  }
  return days
})

function getDaySchedules(date: string, limit?: number) {
  const list = scheduleStore.schedules.filter(s => {
    return dayjs(s.startTime).format('YYYY-MM-DD') === date
  })
  return limit ? list.slice(0, limit) : list
}

function getScheduleBlockStyle(schedule: Schedule) {
  const startHour = dayjs(schedule.startTime).hour()
  const startMin = dayjs(schedule.startTime).minute()
  const endHour = dayjs(schedule.endTime).hour()
  const endMin = dayjs(schedule.endTime).minute()
  const topPx = (startHour * 60 + startMin) * (48 / 60)
  const durationMin = (endHour * 60 + endMin) - (startHour * 60 + startMin)
  const heightPx = Math.max(durationMin * (48 / 60), 20)
  return {
    top: topPx + 'px',
    height: heightPx + 'px',
    background: schedule.status === 'COMPLETED' ? '#67C23A' : getTypeColor(schedule.type)
  }
}

function navigatePrev() {
  const unit = viewMode.value === 'month' ? 'month' : viewMode.value === 'week' ? 'week' : 'day'
  currentDate.value = currentDate.value.subtract(1, unit)
}

function navigateNext() {
  const unit = viewMode.value === 'month' ? 'month' : viewMode.value === 'week' ? 'week' : 'day'
  currentDate.value = currentDate.value.add(1, unit)
}

function goToday() {
  currentDate.value = dayjs()
}

function selectDay(date: string) {
  viewMode.value = 'day'
  currentDate.value = dayjs(date)
}

async function loadSchedules() {
  let start: string, end: string
  const d = currentDate.value
  switch (viewMode.value) {
    case 'month':
      start = d.startOf('month').startOf('week').format('YYYY-MM-DDTHH:mm:ss')
      end = d.endOf('month').endOf('week').format('YYYY-MM-DDTHH:mm:ss')
      break
    case 'week':
      start = d.startOf('week').format('YYYY-MM-DDTHH:mm:ss')
      end = d.endOf('week').format('YYYY-MM-DDTHH:mm:ss')
      break
    case 'list':
      start = d.startOf('month').format('YYYY-MM-DDTHH:mm:ss')
      end = d.endOf('month').format('YYYY-MM-DDTHH:mm:ss')
      break
    default:
      start = d.startOf('day').format('YYYY-MM-DDTHH:mm:ss')
      end = d.endOf('day').format('YYYY-MM-DDTHH:mm:ss')
  }
  await scheduleStore.fetchSchedules(start, end)
}

async function handleNlpParse() {
  if (!nlpInput.value.trim()) return
  nlpLoading.value = true
  try {
    const res = await nlpAPI.parseAndCheck(nlpInput.value, 0)
    const data = res.data.data as { parsedSchedule: any; conflictCheck: ConflictCheckResult }
    const parsed = data.parsedSchedule
    const conflictCheck = data.conflictCheck

    const scheduleData = {
      title: parsed.title || '未命名日程',
      description: parsed.description || '',
      startTime: parsed.startTime,
      endTime: parsed.endTime,
      type: parsed.type || 'OTHER',
      location: parsed.location || '',
      participants: parsed.participants ? '["' + parsed.participants.join('","') + '"]' : '',
      importance: parsed.importance || 3,
      source: 'NLP',
      rawText: nlpInput.value
    }

    pendingNlpSchedule.value = scheduleData

    if (conflictCheck.hasConflict) {
      conflictResult.value = conflictCheck
      selectedResolution.value = ''
      showConflictDialog.value = true
      return
    }

    await doCreateSchedule(scheduleData)
    ElMessage.success(`已创建：${scheduleData.title}`)
    nlpInput.value = ''
    await loadSchedules()
  } catch {
    // handled by interceptor
  } finally {
    nlpLoading.value = false
  }
}

async function handleCreateSchedule() {
  if (!scheduleForm.value.title) {
    ElMessage.warning('请输入标题')
    return
  }
  if (scheduleTimeRange.value.length !== 2) {
    ElMessage.warning('请选择时间范围')
    return
  }
  saving.value = true
  try {
    const data = {
      ...scheduleForm.value,
      startTime: scheduleTimeRange.value[0],
      endTime: scheduleTimeRange.value[1]
    }
    pendingScheduleData.value = data

    const conflictRes = await scheduleAPI.checkConflict(
      scheduleTimeRange.value[0],
      scheduleTimeRange.value[1],
      editingScheduleId.value ?? undefined
    )
    const conflictCheck = conflictRes.data.data as ConflictCheckResult

    if (conflictCheck.hasConflict) {
      conflictResult.value = conflictCheck
      selectedResolution.value = ''
      showConflictDialog.value = true
      return
    }

    await executeCreateOrUpdate(data)
    await loadSchedules()
  } catch {
    // handled
  } finally {
    saving.value = false
  }
}

function getResolutionByStrategy(strategy: string): ResolutionSuggestion | undefined {
  return conflictResult.value.suggestions.find(s => s.strategy === strategy)
}

async function handleApplyResolution() {
  if (!selectedResolution.value) return
  const resolution = getResolutionByStrategy(selectedResolution.value)
  if (!resolution) return

  showConflictDialog.value = false

  if (pendingNlpSchedule.value) {
    const adjusted = {
      ...pendingNlpSchedule.value,
      startTime: resolution.adjustedStartTime,
      endTime: resolution.adjustedEndTime
    }
    await doCreateSchedule(adjusted)
    ElMessage.success(`已创建：${adjusted.title}`)
    nlpInput.value = ''
    pendingNlpSchedule.value = null
    await loadSchedules()
  } else if (pendingScheduleData.value) {
    const adjusted = {
      ...pendingScheduleData.value,
      startTime: resolution.adjustedStartTime,
      endTime: resolution.adjustedEndTime
    }
    await executeCreateOrUpdate(adjusted)
    await loadSchedules()
  }
}

async function handleIgnoreConflict() {
  showConflictDialog.value = false

  if (pendingNlpSchedule.value) {
    await doCreateSchedule(pendingNlpSchedule.value)
    ElMessage.success(`已创建：${pendingNlpSchedule.value.title}`)
    nlpInput.value = ''
    pendingNlpSchedule.value = null
    await loadSchedules()
  } else if (pendingScheduleData.value) {
    await executeCreateOrUpdate(pendingScheduleData.value)
    await loadSchedules()
  }
}

function handleCancelConflict() {
  showConflictDialog.value = false
  pendingNlpSchedule.value = null
  pendingScheduleData.value = null
}

async function doCreateSchedule(data: Record<string, unknown>) {
  saving.value = true
  try {
    await scheduleStore.createSchedule(data)
  } finally {
    saving.value = false
  }
}

async function executeCreateOrUpdate(data: Record<string, unknown>) {
  saving.value = true
  try {
    if (editingScheduleId.value) {
      await scheduleStore.updateSchedule(editingScheduleId.value, data)
      ElMessage.success('已更新')
    } else {
      await scheduleStore.createSchedule(data)
      ElMessage.success('创建成功')
    }
    showCreateDialog.value = false
    pendingScheduleData.value = null
    resetForm()
  } finally {
    saving.value = false
  }
}

function overlapTypeLabel(overlapType: string): string {
  const map: Record<string, string> = {
    CONTAINED: '被包含',
    CONTAINING: '完全重叠',
    PARTIAL: '部分重叠'
  }
  return map[overlapType] || overlapType
}

function overlapTypeTag(overlapType: string): 'warning' | 'danger' | '' {
  const map: Record<string, 'warning' | 'danger' | ''> = {
    CONTAINED: 'warning',
    CONTAINING: 'danger',
    PARTIAL: ''
  }
  return map[overlapType] || ''
}

function handleEditSchedule() {
  if (!selectedSchedule.value) return
  editingScheduleId.value = selectedSchedule.value.id
  scheduleForm.value = {
    title: selectedSchedule.value.title,
    type: selectedSchedule.value.type,
    important: selectedSchedule.value.important || false,
    location: selectedSchedule.value.location || '',
    description: selectedSchedule.value.description || ''
  }
  scheduleTimeRange.value = [
    dayjs(selectedSchedule.value.startTime).format('YYYY-MM-DDTHH:mm:ss'),
    dayjs(selectedSchedule.value.endTime).format('YYYY-MM-DDTHH:mm:ss')
  ]
  showDetailDialog.value = false
  showCreateDialog.value = true
}

async function handleCompleteSchedule() {
  if (!selectedSchedule.value) return
  saving.value = true
  try {
    await scheduleStore.completeSchedule(selectedSchedule.value.id)
    selectedSchedule.value.status = 'COMPLETED'
    ElMessage.success('已标记为完成')
    await loadSchedules()
  } catch {
    // handled
  } finally {
    saving.value = false
  }
}

async function handleUncompleteSchedule() {
  if (!selectedSchedule.value) return
  saving.value = true
  try {
    await scheduleStore.uncompleteSchedule(selectedSchedule.value.id)
    selectedSchedule.value.status = 'SCHEDULED'
    ElMessage.success('已取消完成状态')
    await loadSchedules()
  } catch {
    // handled
  } finally {
    saving.value = false
  }
}

function resetForm() {
  editingScheduleId.value = null
  scheduleForm.value = { title: '', type: 'OTHER', important: false, location: '', description: '' }
  scheduleTimeRange.value = []
}

function openScheduleDetail(schedule: Schedule) {
  selectedSchedule.value = schedule
  showDetailDialog.value = true
}

async function handleDeleteSchedule() {
  if (!selectedSchedule.value) return
  try {
    await ElMessageBox.confirm('确定删除此日程？', '确认删除', { type: 'warning' })
    await scheduleStore.deleteSchedule(selectedSchedule.value.id)
    ElMessage.success('已删除')
    showDetailDialog.value = false
    await loadSchedules()
  } catch {
    // cancelled
  }
}

watch([viewMode, currentDate], () => {
  loadSchedules()
})

onMounted(() => {
  loadSchedules()
  fetchHolidays()
})

watch(() => currentDate.value.year(), () => {
  fetchHolidays()
})
</script>

<style scoped lang="scss">
.calendar-page {
  padding: 16px;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.calendar-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 8px;
}

.date-title {
  font-size: 18px;
  font-weight: 600;
  color: $text-primary;
  min-width: 180px;
}

.nav-btns {
  margin-left: 6px;
}

.nlp-bar {
  margin-bottom: 12px;
}

.calendar-body {
  flex: 1;
  background: #fff;
  border-radius: $radius-md;
  overflow: auto;
}

.weekday-header {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  border-bottom: 1px solid $border-color;
}

.weekday-cell {
  text-align: center;
  padding: 8px 0;
  font-size: 12px;
  font-weight: 600;
  color: $text-secondary;
}

.month-grid {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
}

.day-cell {
  min-height: 90px;
  padding: 4px;
  border-right: 1px solid $border-color;
  border-bottom: 1px solid $border-color;
  cursor: pointer;
  transition: background 0.15s;

  &:nth-child(7n) {
    border-right: none;
  }

  &:hover {
    background: #f5f7fa;
  }

  &.other-month {
    opacity: 0.4;
  }

  &.is-today .day-num {
    background: $primary-color;
    color: #fff;
  }

  &.is-weekend {
    background: #fafafa;
  }
}

.day-num {
  display: inline-block;
  width: 28px;
  height: 28px;
  line-height: 28px;
  text-align: center;
  border-radius: 50%;
  font-size: 14px;
  font-weight: 600;
  margin-bottom: 3px;
}

.holiday-label {
  font-size: 11px;
  color: #f56c6c;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  display: block;
  line-height: 1.2;
}

.lunar-label {
  font-size: 11px;
  color: #67c23a;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  display: block;
  line-height: 1.2;
}

.day-schedules {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.schedule-dot {
  padding: 1px 3px;
  border-radius: 3px;
  font-size: 11px;
  color: #fff;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  cursor: pointer;

  .star-icon {
    font-size: 8px;
    margin-right: 1px;
    vertical-align: middle;
  }

  &.is-important {
    background: #FFB300 !important;
    border: 1px solid #FF8F00;
    box-shadow: 0 0 3px rgba(255, 179, 0, 0.5);
  }

  &.is-completed {
    opacity: 0.75;
    text-decoration: line-through;
  }
}

.more-schedules {
  font-size: 10px;
  color: $text-secondary;
  padding: 0 3px;
}

.time-view {
  height: 100%;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.time-view-header-row {
  display: flex;
  border-bottom: 1px solid $border-color;
  flex-shrink: 0;
}

.time-gutter {
  width: 56px;
  flex-shrink: 0;
}

.day-col-header {
  flex: 1;
  text-align: center;
  padding: 6px 0;
  border-left: 1px solid #f0f0f0;

  &.is-today {
    background: #ecf5ff;

    .day-num-text {
      background: $primary-color;
      color: #fff;
    }
  }
}

.day-name {
  display: block;
  font-size: 11px;
  color: $text-secondary;
}

.day-num-text {
  display: inline-block;
  width: 24px;
  height: 24px;
  line-height: 24px;
  border-radius: 50%;
  font-size: 12px;
  font-weight: 600;
  color: $text-primary;
}

.time-view-body {
  flex: 1;
  overflow-y: auto;
  position: relative;
}

.time-row {
  display: flex;
  border-bottom: 1px solid #f0f0f0;
  min-height: 48px;
}

.time-label {
  width: 56px;
  padding-top: 2px;
  font-size: 11px;
  color: $text-secondary;
  text-align: right;
  padding-right: 6px;
  flex-shrink: 0;
}

.time-slot {
  flex: 1;
  border-left: 1px solid #f0f0f0;
}

.schedule-overlay {
  position: absolute;
  top: 0;
  left: 56px;
  right: 0;
  bottom: 0;
  pointer-events: none;
}

.day-overlay-col {
  position: absolute;
  top: 0;
  bottom: 0;
  pointer-events: auto;
}

.schedule-block {
  position: absolute;
  left: 2px;
  right: 2px;
  border-radius: 4px;
  padding: 1px 4px;
  overflow: hidden;
  cursor: pointer;
  transition: opacity 0.15s;
  z-index: 2;

  &:hover {
    opacity: 0.85;
  }

  &.is-important-block {
    background: #FFB300 !important;
    border: 2px solid #FF8F00;
    box-shadow: 0 0 4px rgba(255, 179, 0, 0.6);
    z-index: 3;
  }

  &.is-completed-block {
    opacity: 0.75;
    text-decoration: line-through;
  }
}

.block-time {
  font-size: 9px;
  color: rgba(255, 255, 255, 0.85);
  display: block;
  line-height: 1.2;
}

.block-title {
  font-size: 10px;
  color: #fff;
  font-weight: 500;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  display: block;
  line-height: 1.3;

  .star-icon {
    font-size: 9px;
    margin-right: 1px;
    vertical-align: middle;
  }
}

.block-desc {
  font-size: 9px;
  color: rgba(255, 255, 255, 0.75);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  display: block;
}

.day-schedule-overlay {
  position: absolute;
  top: 0;
  left: 56px;
  right: 0;
  bottom: 0;
  z-index: 2;
  pointer-events: auto;
}

.day-block {
  left: 8px;
  right: 16px;
}

.detail-item {
  margin-bottom: 12px;

  label {
    display: block;
    font-size: 12px;
    color: $text-secondary;
    margin-bottom: 4px;
  }

  span {
    font-size: 13px;
    color: $text-primary;
  }
}

.list-view {
  padding: 12px;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.list-toolbar {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;
  align-items: center;

  .search-input {
    width: 220px;
  }

  .filter-select {
    width: 120px;
  }
}

.list-body {
  flex: 1;
  overflow-y: auto;
}

.list-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 12px;
  border-bottom: 1px solid $border-color;
  cursor: pointer;
  transition: background 0.15s;
  border-radius: 4px;

  &:hover {
    background: #f5f7fa;
  }

  &.is-important-item {
    background: #fffde7;
    border-left: 3px solid #FFB300;
  }

  &.is-completed-item {
    opacity: 0.7;
    .item-title {
      text-decoration: line-through;
    }
  }
}

.item-time {
  font-size: 12px;
  color: $text-secondary;
  min-width: 90px;
  flex-shrink: 0;
}

.item-title {
  flex: 1;
  font-size: 13px;
  color: $text-primary;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;

  .star-icon {
    font-size: 10px;
    color: #FFB300;
    margin-right: 2px;
    vertical-align: middle;
  }
}

.conflict-warning {
  margin-bottom: 16px;
}

.conflict-list {
  margin-bottom: 16px;
}

.conflict-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 12px;
  background: #fff7e6;
  border: 1px solid #ffd591;
  border-radius: 6px;
  margin-bottom: 8px;
}

.conflict-title {
  flex: 1;
  font-size: 13px;
  color: $text-primary;
  font-weight: 500;
}

.conflict-time {
  font-size: 12px;
  color: $text-secondary;
}

.resolution-section {
  margin-top: 4px;
}

.resolution-title {
  font-size: 14px;
  font-weight: 600;
  color: $text-primary;
  margin-bottom: 12px;
}

.resolution-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
}

.resolution-card {
  padding: 12px 14px;
  border: 2px solid $border-color;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;

  &:hover {
    border-color: #409EFF;
    background: #ecf5ff;
  }

  &.selected {
    border-color: #409EFF;
    background: #ecf5ff;
  }
}

.resolution-card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 6px;
}

.resolution-label {
  font-size: 13px;
  font-weight: 600;
  color: $text-primary;
}

.resolution-desc {
  font-size: 12px;
  color: $text-secondary;
  line-height: 1.5;
  margin-bottom: 4px;
}

.resolution-time {
  font-size: 11px;
  color: #409EFF;
  font-weight: 500;
}
</style>