<template>
  <div class="ai-assistant">
    <el-card class="chat-card">
      <div slot="header" class="clearfix">
        <span>🤖 AI 智能助手</span>
        <el-button style="float: right; padding: 3px 10px" type="text" @click="clearChat">清空对话</el-button>
      </div>

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
          class="input-field"
        >
          <i slot="prefix" class="el-input__icon el-icon-chat-line-round"></i>
        </el-input>
        <el-button type="primary" @click="sendMessage" :loading="loading" :disabled="!inputMessage.trim()">
          发送
        </el-button>
      </div>
    </el-card>

    <!-- 快捷问题 -->
    <div class="quick-questions">
      <el-tag
        v-for="q in quickQuestions"
        :key="q"
        @click="askQuestion(q)"
        class="quick-tag"
      >
        {{ q }}
      </el-tag>
    </div>
  </div>
</template>

<script>
import axios from 'axios'

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

      // 添加用户消息
      this.messages.push({
        type: 'user',
        content: message,
        time: this.formatTime(new Date())
      })
      this.inputMessage = ''
      this.loading = true
      this.scrollToBottom()

      try {
        const response = await axios.post('/api/ai/chat', { message })
        if (response.data.code === 200) {
          this.messages.push({
            type: 'bot',
            content: response.data.data,
            time: this.formatTime(new Date())
          })
        } else {
          this.messages.push({
            type: 'bot',
            content: '抱歉，发生了错误：' + response.data.msg,
            time: this.formatTime(new Date())
          })
        }
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
      // 简单格式化：换行和列表
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
.ai-assistant {
  padding: 20px;
  height: calc(100vh - 140px);
  display: flex;
  flex-direction: column;
}

.chat-card {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.chat-card >>> .el-card__body {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 0;
  overflow: hidden;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  background: #f5f7fa;
}

.welcome-tip {
  text-align: center;
  color: #909399;
  padding: 40px 20px;
  line-height: 2;
}

.welcome-tip ul {
  list-style: none;
  padding: 0;
  margin-top: 20px;
}

.welcome-tip li {
  padding: 5px 0;
}

.message {
  display: flex;
  margin-bottom: 20px;
  align-items: flex-start;
}

.message.user {
  flex-direction: row-reverse;
}

.message-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  flex-shrink: 0;
}

.message.user .message-avatar {
  background: #409EFF;
  color: white;
  margin-left: 10px;
}

.message.bot .message-avatar {
  background: #67C23A;
  color: white;
  margin-right: 10px;
}

.message-content {
  max-width: 70%;
}

.message-text {
  padding: 12px 16px;
  border-radius: 12px;
  line-height: 1.6;
  word-break: break-word;
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
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.message-time {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
  padding: 0 8px;
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
  gap: 10px;
  padding: 15px;
  background: white;
  border-top: 1px solid #ebeef5;
}

.input-field {
  flex: 1;
}

.quick-questions {
  margin-top: 15px;
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.quick-tag {
  cursor: pointer;
  padding: 8px 16px;
}

.quick-tag:hover {
  opacity: 0.8;
}
</style>
