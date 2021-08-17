<template>
  <a-layout-header class="header">
    <a-row>
      <a-col :span="22">
        <a-menu
            theme="dark"
            mode="horizontal"
            :style="{ lineHeight: '64px' }"
        >
          <a-menu-item key="/">
            <router-link to="/">首页</router-link>
          </a-menu-item>
          <a-menu-item key="/admin/ebook">
            <router-link to="/admin/ebook">电子书管理</router-link>
          </a-menu-item>
          <a-menu-item key="/admin/category">
            <router-link to="/admin/category">分类管理</router-link>
          </a-menu-item>
          <a-menu-item key="/admin/user">
            <router-link to="/admin/user">用户管理</router-link>
          </a-menu-item>
          <a-menu-item key="/about">
            <router-link to="/about">关于我们</router-link>
          </a-menu-item>
        </a-menu>
      </a-col>
      <a-col :span="2" >
        <a-tag color="#3b5999" :model="user" v-show="user.id">
          <template #icon>
            <CrownOutlined />
          </template>
          欢迎：{{ user.name }}
        </a-tag>
        <a-button type="primary" ghost @click="loginModalShow" v-show="!user.id">登录</a-button>
      </a-col>
    </a-row>
  </a-layout-header>
  <a-modal
      title="登录表单"
      v-model:visible="loginModalVisible"
      :confirm-loading="loginModalConfirmLoading"
      @ok="loginHandleOk"
  >
    <a-form :model="loginUser" :label-col="{span: 6}" :wrapper-col="{span: 12}">
      <a-form-item label="登录名">
        <a-input v-model:value="loginUser.loginName" />
      </a-form-item>
      <a-form-item label="密码">
        <a-input v-model:value="loginUser.password" type="password" />
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script lang="ts">
import {defineComponent, ref} from 'vue';
import axios from "axios";
import {message} from "ant-design-vue";
import store from "@/store";

declare let hexMd5: any;
declare let KEY: any;

export default defineComponent({
  name: 'the-header',
  setup() {

    // 登录表单相关变量
    const loginModalVisible = ref();
    loginModalVisible.value = false;
    const loginModalConfirmLoading = ref();
    loginModalConfirmLoading.value = false;
    // 用于登录的信息
    const loginUser = ref();
    loginUser.value = {};
    // 登录后的用户信息
    const user = ref();
    user.value = store.state.user;

    const loginModalShow = () => {
      loginModalVisible.value = true;
    }

    const loginHandleOk = () => {
      loginModalConfirmLoading.value = true;
      loginUser.value.password = hexMd5(loginUser.value.password+KEY);
      axios.post("/user/login", loginUser.value).then((response) => {
        loginUser.value.password = null;
        const data = response.data;
        if (data.success) {
          loginModalVisible.value = false;
          user.value = data.content;
          store.commit('setUser', user.value);
        } else {
          message.error(data.message);
        }
        loginModalConfirmLoading.value = false;
      })
    }

    return {
      loginModalVisible,
      loginModalConfirmLoading,
      loginUser,
      user,

      loginModalShow,
      loginHandleOk,
    }
  }
});
</script>

<style>
 .login-menu {
   float: right !important;
   color: white;
 }
</style>