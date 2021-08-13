<template>
  <a-layout>
    <a-layout-content
        :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
    >
      <div class="about">
        <h1>电子书管理</h1>
      </div>
      <a-table
          :columns="columns"
          :row-key="record => record.id"
          :data-source="ebooks"
          :pagination="pagination"
          :loading="loading"
          @change="handleTableChange"
      >
        <template #cover="{ text: cover }">
          <img v-if="cover" :src="cover" alt="avatar" style="width: 50px" />
        </template>
        <template v-slot:action="{text, record}">
          <a-space size="small">
            <a-button type="primary">编辑</a-button>
            <a-button type="danger">删除</a-button>
          </a-space>
        </template>
      </a-table>
    </a-layout-content>
  </a-layout>
</template>

<script lang="ts">
import {defineComponent, onMounted, ref} from 'vue';
import axios from "axios";

export default defineComponent({
  name: 'AdminEbook',
  setup() {

    const ebooks = ref();
    const pagination = ref({
      current: 1,
      pageSize: 2,
      total: 0
    })
    const loading = ref(false)

    const columns = [
      {
        title: '封面',
        dataIndex: 'cover',
        slots: { customRender: 'cover' },
      },
      {
        title: '名称',
        dataIndex: 'name',
      },
      {
        title: '分类1',
        dataIndex: 'category1Id',
        key: 'category1Id',
      },
      {
        title: '分类2',
        dataIndex: 'category2Id',
        key: 'category2Id',
      },
      {
        title: '描述',
        dataIndex: 'description',
      },
      {
        title: '文章数',
        dataIndex: 'docCount',
      },
      {
        title: '阅读数',
        dataIndex: 'viewCount',
      },
      {
        title: '点赞数',
        dataIndex: 'voteCount',
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
      axios.get("/ebook/list").then((response) => {
        loading.value = false;
        const data = response.data;
        ebooks.value = data.content;

        // 重置分页按钮
        pagination.value.current = params.page;
      })
    }

    // 点击表格页码时触发
    const handleTableChange = (pagination: any) => {
      handleQuery({
        page: pagination.current,
        size: pagination.pageSize
      })
    }

    onMounted(() => {
      handleQuery({});
    });

    return {
      columns,
      ebooks,
      pagination,
      loading,
      handleTableChange,
    };
  },
});
</script>
