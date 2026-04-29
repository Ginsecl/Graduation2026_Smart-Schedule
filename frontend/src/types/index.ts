export interface User {
  id: number
  username: string
  email: string
  nickname: string
  avatarUrl: string
  timezone: string
  preferences: string
  createdAt: string
}

export interface Schedule {
  id: number
  userId: number
  title: string
  description: string
  startTime: string
  endTime: string
  durationMinutes: number
  type: string
  status: string
  importance: number
  important: boolean
  location: string
  participants: string
  repeatRule: string
  source: string
  rawText: string
  extensions: string
  createdAt: string
  updatedAt: string
}

export interface ApiResponse<T> {
  code: number
  message: string
  data: T
  timestamp: number
}

export interface Todo {
  id: number
  userId: number
  title: string
  important: boolean
  completed: boolean
  createdAt: string
  updatedAt: string
}

export interface ConflictInfo {
  scheduleId: number
  title: string
  overlapType: string
  startTime: string
  endTime: string
  type: string
  importance: number
}

export interface ResolutionSuggestion {
  strategy: string
  label: string
  description: string
  adjustedStartTime: string
  adjustedEndTime: string
  adjustedDurationMinutes: number
}

export interface ConflictCheckResult {
  hasConflict: boolean
  conflicts: ConflictInfo[]
  suggestions: ResolutionSuggestion[]
}

export interface NlpParseConflictData {
  parsedSchedule: {
    title: string
    description: string
    startTime: string
    endTime: string
    type: string
    location: string
    participants: string[]
    importance: number
    confidence: number
    rawText: string
    intent: string
    needsConfirmation: boolean
  }
  conflictCheck: ConflictCheckResult
}