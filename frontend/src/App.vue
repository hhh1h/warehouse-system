<template>
  <div id="app">
    <router-view/>
  </div>
</template>

<script>
export default {
  name: 'App'
}
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

/* AI 悬浮按钮全局层级 */
.ai-float-btn {
  z-index: 99999 !important;
}
</style>
