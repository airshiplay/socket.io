<template>
  <div class="about">
    <h1>This is an about page</h1>
    <img src="../assets/logo.png">
    <div>
      <p>
        If Ant-Design-Vue is successfully added to this project, you'll see an
        <code v-text="'<a-button>'"></code>
        <code v-text="'<a-pagination>'"></code>
        below
      </p>
      <a-button type="primary">Primary</a-button>
      <a-pagination size="small" :total="50" showSizeChanger showQuickJumper />
    </div>
  </div>
</template>
