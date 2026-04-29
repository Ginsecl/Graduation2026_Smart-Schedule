<template>
  <div class="settings-page page-container">
    <h2 class="card-title">个人设置</h2>

    <el-card shadow="hover" style="max-width: 600px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="昵称">
          <el-input v-model="form.nickname" placeholder="设置昵称" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" disabled />
        </el-form-item>
        <el-form-item label="时区">
          <el-select v-model="form.timezone" style="width: 100%">
            <el-option label="Asia/Shanghai (UTC+8)" value="Asia/Shanghai" />
            <el-option label="Asia/Tokyo (UTC+9)" value="Asia/Tokyo" />
            <el-option label="America/New_York (UTC-5)" value="America/New_York" />
            <el-option label="Europe/London (UTC+0)" value="Europe/London" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="saving" @click="handleSave">保存设置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="hover" style="max-width: 600px; margin-top: 20px">
      <template #header><span>修改密码</span></template>
      <el-form :model="pwdForm" label-width="100px" :rules="pwdRules" ref="pwdFormRef">
        <el-form-item label="原密码" prop="oldPassword">
          <el-input v-model="pwdForm.oldPassword" type="password" show-password placeholder="请输入原密码" />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="pwdForm.newPassword" type="password" show-password placeholder="请输入新密码（至少6位）" />
        </el-form-item>
        <el-form-item label="确认新密码" prop="confirmPassword">
          <el-input v-model="pwdForm.confirmPassword" type="password" show-password placeholder="请再次输入新密码" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="changingPwd" @click="handleChangePassword">修改密码</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="hover" style="max-width: 600px; margin-top: 20px">
      <template #header><span>关于</span></template>
      <div class="about-info">
        <p><strong>SmartSchedule</strong> - 智能个人日程管理系统</p>
        <p>版本：1.0.0</p>
        <p>技术栈：Vue 3 + Element Plus + Spring Boot 3.2</p>
        <p>功能：日程管理 / NLP智能解析 / 统计分析 / 智能提醒</p>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { authAPI } from '@/api'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'

const userStore = useUserStore()
const saving = ref(false)
const changingPwd = ref(false)
const pwdFormRef = ref<FormInstance>()

const form = reactive({
  nickname: '',
  email: '',
  timezone: 'Asia/Shanghai'
})

const pwdForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const validateConfirmPassword = (_rule: unknown, value: string, callback: (error?: Error) => void) => {
  if (value !== pwdForm.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const pwdRules: FormRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

onMounted(async () => {
  try {
    const res = await authAPI.getProfile()
    const user = res.data.data
    form.nickname = user.nickname || ''
    form.email = user.email || ''
    form.timezone = user.timezone || 'Asia/Shanghai'
  } catch {
    form.nickname = userStore.user?.nickname || ''
    form.email = userStore.user?.email || ''
  }
})

async function handleSave() {
  saving.value = true
  try {
    await authAPI.updateProfile({ nickname: form.nickname, timezone: form.timezone })
    ElMessage.success('设置已保存')
  } catch {
    // handled
  } finally {
    saving.value = false
  }
}

async function handleChangePassword() {
  const valid = await pwdFormRef.value?.validate().catch(() => false)
  if (!valid) return
  changingPwd.value = true
  try {
    await authAPI.changePassword({
      oldPassword: pwdForm.oldPassword,
      newPassword: pwdForm.newPassword
    })
    ElMessage.success('密码修改成功，请妥善保管新密码')
    pwdForm.oldPassword = ''
    pwdForm.newPassword = ''
    pwdForm.confirmPassword = ''
  } catch {
    // handled
  } finally {
    changingPwd.value = false
  }
}
</script>

<style scoped lang="scss">
.about-info {
  p {
    margin-bottom: 8px;
    font-size: 14px;
    color: $text-regular;
  }
}
</style>