<template>
  <div class="ai-assistant-dialog">
    <!-- 消息列表 -->
    <div class="chat-messages" ref="messagesContainer">
      <div v-if="messages.length === 0" class="welcome-tip">
        <p>👋 您好，我是仓库管理系统的AI助手</p>
        <p>您可以问我：</p>
        <ul>
          <li>📦 "查询当前库存"</li>
          <li>📊 "生成库存报表"</li>
          <li>⚠️ "有哪些库存预警"</li>
          <li>🏭 "仓库列表"</li>
          <li>❓ "帮助"</li>
        </ul>
      </div>
      <div v-for="(msg, index) in messages" :key="index" :class="['message', msg.type]">
        <div class="message-avatar">
          {{ msg.type === 'user' ? '👤' : '🤖' }}
        </div>
        <div class="message-content">
          <div class="message-text" v-html="formatMessage(msg.content)"></div>
          <div class="message-time">{{ msg.time }}</div>
        </div>
      </div>
      <div v-if="loading" class="message bot">
        <div class="message-avatar">🤖</div>
        <div class="message-content">
          <div class="message-text loading">
            <span>思考中</span>
            <span class="dot">.</span>
            <span class="dot">.</span>
            <span class="dot">.</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 输入区域 -->
    <div class="chat-input">
      <el-input
        v-model="inputMessage"
        placeholder="输入您的问题，按回车发送..."
        @keyup.enter.native="sendMessage"
        :disabled="loading"
        size="small"
      >
        <i slot="prefix" class="el-input__icon el-icon-chat-line-round"></i>
      </el-input>
      <el-button type="primary" @click="sendMessage" :loading="loading" :disabled="!inputMessage.trim()" size="small">
        发送
      </el-button>
    </div>

    <!-- 快捷问题 -->
    <div class="quick-questions">
      <el-tag
        v-for="q in quickQuestions"
        :key="q"
        @click="askQuestion(q)"
        size="small"
      >
        {{ q }}
      </el-tag>
    </div>
  </div>
</template>

<script>
import request from '@/utils/request'

export default {
  name: 'AiAssistant',
  data() {
    return {
      inputMessage: '',
      messages: [],
      loading: false,
      quickQuestions: [
        '库存情况',
        '生成报表',
        '库存预警',
        '帮助'
      ]
    }
  },
  methods: {
    async sendMessage() {
      const message = this.inputMessage.trim()
      if (!message) return

      this.messages.push({
        type: 'user',
        content: message,
        time: this.formatTime(new Date())
      })
      this.inputMessage = ''
      this.loading = true
      this.scrollToBottom()

      try {
        const response = await request.post('/ai/chat', { message })
        this.messages.push({
          type: 'bot',
          content: response.data,
          time: this.formatTime(new Date())
        })
      } catch (error) {
        this.messages.push({
          type: 'bot',
          content: '网络错误，请稍后重试',
          time: this.formatTime(new Date())
        })
      } finally {
        this.loading = false
        this.scrollToBottom()
      }
    },

    askQuestion(question) {
      this.inputMessage = question
      this.sendMessage()
    },

    clearChat() {
      this.messages = []
    },

    scrollToBottom() {
      this.$nextTick(() => {
        const container = this.$refs.messagesContainer
        if (container) {
          container.scrollTop = container.scrollHeight
        }
      })
    },

    formatTime(date) {
      return date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
    },

    formatMessage(content) {
      return content
        .replace(/\n/g, '<br>')
        .replace(/• /g, '• ')
        .replace(/- /g, '- ')
        .replace(/(\d+)条/g, '<strong>$1</strong>条')
        .replace(/(种|件|个)/g, '<strong>$1</strong>')
    }
  }
}
</script>

<style scoped>
.ai-assistant-dialog {
  display: flex;
  flex-direction: column;
  height: 420px;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 12px;
  background: #f5f7fa;
  border-radius: 4px 4px 0 0;
}

.welcome-tip {
  text-align: center;
  color: #909399;
  padding: 20px;
  line-height: 2;
}

.welcome-tip ul {
  list-style: none;
  padding: 0;
  margin-top: 10px;
}

.welcome-tip li {
  padding: 3px 0;
}

.message {
  display: flex;
  margin-bottom: 12px;
  align-items: flex-start;
}

.message.user {
  flex-direction: row-reverse;
}

.message-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  flex-shrink: 0;
}

.message.user .message-avatar {
  background: #409EFF;
  color: white;
  margin-left: 8px;
}

.message.bot .message-avatar {
  background: #67C23A;
  color: white;
  margin-right: 8px;
}

.message-content {
  max-width: 75%;
}

.message-text {
  padding: 8px 12px;
  border-radius: 8px;
  line-height: 1.5;
  word-break: break-word;
  font-size: 13px;
}

.message.user .message-text {
  background: #409EFF;
  color: white;
  border-bottom-right-radius: 4px;
}

.message.bot .message-text {
  background: white;
  color: #303133;
  border-bottom-left-radius: 4px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.08);
}

.message-time {
  font-size: 11px;
  color: #909399;
  margin-top: 3px;
  padding: 0 6px;
}

.message.user .message-time {
  text-align: right;
}

.loading .dot {
  animation: blink 1.4s infinite both;
}

.loading .dot:nth-child(2) {
  animation-delay: 0.2s;
}

.loading .dot:nth-child(3) {
  animation-delay: 0.4s;
}

@keyframes blink {
  0%, 80%, 100% { opacity: 0; }
  40% { opacity: 1; }
}

.chat-input {
  display: flex;
  gap: 8px;
  padding: 8px 12px;
  background: white;
  border-top: 1px solid #ebeef5;
}

.quick-questions {
  padding: 6px 12px 10px;
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
  background: white;
  border-top: 1px solid #ebeef5;
}
</style>
