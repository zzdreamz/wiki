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
          :data-source="categorys"
          :pagination="pagination"
          :loading="loading"
          @change="handleTableChange"
      >
        <template #cover="{ text: cover }">
          <img v-if="cover" :src="cover" alt="avatar" style="width: 50px" />
        </template>
        <template v-slot:action="{text, record}">
          <a-space size="small">
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
      title="分类表单"
      v-model:visible="modalVisible"
      :confirm-loading="modalConfirmLoading"
      @ok="handleOk"
  >
    <a-form :model="category" :label-col="{span: 6}" :wrapper-col="{span: 12}">
      <a-form-item label="名称">
        <a-input v-model:value="category.name" />
      </a-form-item>
      <a-form-item label="父分类">
        <a-input v-model:value="category.parent" />
      </a-form-item>
      <a-form-item label="顺序">
        <a-input v-model:value="category.sort" />
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script lang="ts">
import {defineComponent, onMounted, ref} from 'vue';
import axios from "axios";
import {message} from "ant-design-vue";
import {Tool} from "@/util/tool";

export default defineComponent({
  name: 'AdminCategory',
  setup() {

    const categorys = ref();
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
        title: '名称',
        dataIndex: 'name',
      },
      {
        title: '父分类',
        dataIndex: 'parent',
        key: 'parent',
      },
      {
        title: '顺序',
        dataIndex: 'sort',
        key: 'sort',
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

      axios.get("/category/list", {
            params: {
              pageNum: params.pageNum,
              pageSize: params.pageSize,
              name: queryParam.value.name
            }
          }).then((response) => {
        loading.value = false;
        const data = response.data;
        if (data.success) {
          categorys.value = data.content.list;

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

    // 分类数据
    const category = ref()

    // 编辑
    const edit = (record: any) => {
      category.value = Tool.copy(record);
      modalVisible.value = true;
    };

    // 新增
    const add = () => {
      category.value = {};
      modalVisible.value = true;
    };

    // 删除
    const handleDelete = (id: any) => {
      axios.delete("/category/delete/" + id).then((response) => {
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
      console.log(category.value);
      axios.post("/category/save", category.value).then((response) => {
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
        name: searchName
      }
      handleQuery({
        pageNum: 1,
        pageSize: pagination.value.pageSize
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
      categorys,
      pagination,
      loading,
      handleTableChange,

      modalVisible,
      modalConfirmLoading,
      edit,
      add,
      handleDelete,
      handleOk,

      category,

      searchName,
      onSearch,
    };
  },
});
</script>
