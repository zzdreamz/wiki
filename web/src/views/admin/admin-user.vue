<template>
  <a-layout>
    <a-layout-content
        :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
    >
      <a-input-search
          v-model:value="searchName"
          placeholder="输入名称"
          enter-button
          @search="onSearch"
          style="width: 200px"
      />&ensp;
      <a-button type="primary" @click="add">新增</a-button>
      <a-table
          :columns="columns"
          :row-key="record => record.id"
          :data-source="users"
          :pagination="pagination"
          :loading="loading"
          @change="handleTableChange"
      >
        <template #action="{text, record}">
          <a-space size="small">
            <a-button type="primary" @click="resetPassword(record)">重置密码</a-button>
            <a-button type="primary" @click="edit(record)">编辑</a-button>
            <a-popconfirm
                title="删除后不可恢复，确认删除？"
                ok-text="Yes"
                cancel-text="No"
                @confirm="handleDelete(record.id)"
            >
              <a-button type="danger">删除</a-button>
            </a-popconfirm>
          </a-space>
        </template>
      </a-table>
    </a-layout-content>
  </a-layout>
  <a-modal
      title="用户表单"
      v-model:visible="modalVisible"
      :confirm-loading="modalConfirmLoading"
      @ok="handleOk"
  >
    <a-form :model="user" :label-col="{span: 6}" :wrapper-col="{span: 12}">
      <a-form-item label="登录名">
        <a-input v-model:value="user.loginName" />
      </a-form-item>
      <a-form-item label="昵称">
        <a-input v-model:value="user.name" />
      </a-form-item>
      <a-form-item label="密码" v-show="!user.id">
        <a-input v-model:value="user.password" />
      </a-form-item>
    </a-form>
  </a-modal>
  <a-modal
      title="用户表单"
      v-model:visible="resetModalVisible"
      :confirm-loading="resetModalConfirmLoading"
      @ok="resetHandleOk"
  >
    <a-form :model="user" :label-col="{span: 6}" :wrapper-col="{span: 12}">
      <a-form-item label="新密码">
        <a-input v-model:value="user.password" />
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script lang="ts">
import {defineComponent, onMounted, ref} from 'vue';
import axios from "axios";
import {message} from "ant-design-vue";
import {Tool} from "@/util/tool";

declare let hexMd5: any;
declare let KEY: any;

export default defineComponent({
  name: 'AdminUser',
  setup() {

    const users = ref();
    const pagination = ref({
      current: 1,
      pageSize: 3,
      total: 0
    });
    const queryParam = ref();
    queryParam.value = {}

    const loading = ref(false)

    const columns = [
      {
        title: 'id',
        dataIndex: 'id',
      },
      {
        title: '登录名',
        dataIndex: 'loginName',
      },
      {
        title: '昵称',
        dataIndex: 'name',
      },
      {
        title: '密码',
        dataIndex: 'password',
      },
      {
        title: '操作',
        key: 'action',
        slots: { customRender: 'action' },
      },
    ];

    // 数据查询
    const handleQuery = (params: any) => {
      loading.value = true;
      axios.get("/user/list", {
            params: {
              pageNum: params.pageNum,
              pageSize: params.pageSize,
              loginName: queryParam.value.loginName
            }
          }).then((response) => {
        loading.value = false;
        const data = response.data;
        if (data.success) {
          users.value = data.content.list;

          // 重置分页按钮
          pagination.value.current = params.pageNum;
          pagination.value.total = data.content.total;
        } else {
          message.error(data.message);
        }
      })
    }

    // 点击表格页码时触发
    const handleTableChange = (pagination: any) => {
      handleQuery({
        pageNum: pagination.current,
        pageSize: pagination.pageSize
      })
    }

    // modal相关变量
    const modalVisible = ref<boolean>(false);
    const modalConfirmLoading = ref<boolean>(false);

    // 用户数据
    const user = ref()

    // 编辑
    const edit = (record: any) => {
      user.value = Tool.copy(record);
      modalVisible.value = true;
    };

    // 新增
    const add = () => {
      user.value = {};
      modalVisible.value = true;
    };

    // 删除
    const handleDelete = (id: any) => {
      axios.delete("/user/delete/" + id).then((response) => {
        const data = response.data;
        if (data.success) {
          handleQuery({
            pageNum: pagination.value.current,
            pageSize: pagination.value.pageSize
          });
        }
      })
    }

    // modal的ok事件
    const handleOk = () => {
      modalConfirmLoading.value = true;
      user.value.password = hexMd5(user.value.password+KEY);
      axios.post("/user/save", user.value).then((response) => {
        const data = response.data;
        if (data.success) {
          modalVisible.value = false;
          handleQuery({
            pageNum: pagination.value.current,
            pageSize: pagination.value.pageSize
          });
        } else {
          message.error(data.message);
        }
        modalConfirmLoading.value = false;
      })
    }

    const searchName = ref();
    const onSearch = (searchName: any) => {
      queryParam.value = {
        loginName: searchName
      }
      handleQuery({
        pageNum: 1,
        pageSize: pagination.value.pageSize
      })
    }

    // 重置密码
    const resetModalVisible = ref();
    resetModalVisible.value = false;
    const resetModalConfirmLoading = ref();
    resetModalConfirmLoading.value = false;
    const resetPassword = (record: any) => {
      user.value = Tool.copy(record);
      user.value.password = null;
      resetModalVisible.value = true;
    };
    const resetHandleOk = () => {
      resetModalConfirmLoading.value = true;
      user.value.password = hexMd5(user.value.password+KEY);
      delete user.value.name;
      delete user.value.loginName;
      axios.post("/user/reset-password", user.value).then((response) => {
        const data = response.data;
        if (data.success) {
          resetModalVisible.value = false;
          handleQuery({
            pageNum: pagination.value.current,
            pageSize: pagination.value.pageSize
          });
        } else {
          message.error(data.message);
        }
        resetModalConfirmLoading.value = false;
      })
    }

    onMounted(() => {
      handleQuery({
        pageNum: 1,
        pageSize: pagination.value.pageSize
      });
    });

    return {
      columns,
      users,
      pagination,
      loading,
      handleTableChange,

      modalVisible,
      modalConfirmLoading,
      edit,
      add,
      handleDelete,
      handleOk,

      user,

      searchName,
      onSearch,

      resetPassword,
      resetModalVisible,
      resetModalConfirmLoading,
      resetHandleOk,
    };
  },
});
</script>
