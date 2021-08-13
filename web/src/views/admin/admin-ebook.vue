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
            <a-button type="primary" @click="edit(record)">编辑</a-button>
            <a-button type="danger">删除</a-button>
          </a-space>
        </template>
      </a-table>
    </a-layout-content>
  </a-layout>
  <a-modal
      title="Title"
      v-model:visible="modalVisible"
      :confirm-loading="modalConfirmLoading"
      @ok="handleOk"
  >
    <a-form :model="ebook" :label-col="{span: 6}" :wrapper-col="{span: 12}">
      <a-form-item label="封面">
        <a-input v-model:value="ebook.cover" />
      </a-form-item>
      <a-form-item label="名称">
        <a-input v-model:value="ebook.name" />
      </a-form-item>
      <a-form-item label="分类一">
        <a-input v-model:value="ebook.category1Id" />
      </a-form-item>
      <a-form-item label="分类二">
        <a-input v-model:value="ebook.category2Id" />
      </a-form-item>
      <a-form-item label="描述">
        <a-input v-model:value="ebook.description" />
      </a-form-item>
<!--      <a-form-item label="Activity zone">-->
<!--        <a-select v-model:value="formState.region" placeholder="please select your zone">-->
<!--          <a-select-option value="shanghai">Zone one</a-select-option>-->
<!--          <a-select-option value="beijing">Zone two</a-select-option>-->
<!--        </a-select>-->
<!--      </a-form-item>-->
<!--      <a-form-item label="Activity time">-->
<!--        <a-date-picker-->
<!--            v-model:value="formState.date1"-->
<!--            show-time-->
<!--            type="date"-->
<!--            placeholder="Pick a date"-->
<!--            style="width: 100%"-->
<!--        />-->
<!--      </a-form-item>-->
<!--      <a-form-item label="Instant delivery">-->
<!--        <a-switch v-model:checked="formState.delivery" />-->
<!--      </a-form-item>-->
<!--      <a-form-item label="Activity type">-->
<!--        <a-checkbox-group v-model:value="formState.type">-->
<!--          <a-checkbox value="1" name="type">Online</a-checkbox>-->
<!--          <a-checkbox value="2" name="type">Promotion</a-checkbox>-->
<!--          <a-checkbox value="3" name="type">Offline</a-checkbox>-->
<!--        </a-checkbox-group>-->
<!--      </a-form-item>-->
<!--      <a-form-item label="Resources">-->
<!--        <a-radio-group v-model:value="formState.resource">-->
<!--          <a-radio value="1">Sponsor</a-radio>-->
<!--          <a-radio value="2">Venue</a-radio>-->
<!--        </a-radio-group>-->
<!--      </a-form-item>-->
<!--      <a-form-item label="Activity form">-->
<!--        <a-input v-model:value="formState.desc" type="textarea" />-->
<!--      </a-form-item>-->
<!--      <a-form-item :wrapper-col="{ span: 14, offset: 4 }">-->
<!--        <a-button type="primary" @click="onSubmit">Create</a-button>-->
<!--        <a-button style="margin-left: 10px">Cancel</a-button>-->
<!--      </a-form-item>-->
    </a-form>
  </a-modal>
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
      pageSize: 3,
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
      axios.get("/ebook/list", {
            params:
                {
                  pageNum: params.pageNum,
                  pageSize: params.pageSize
                }
          }).then((response) => {
        loading.value = false;
        const data = response.data;
        ebooks.value = data.content.list;

        // 重置分页按钮
        pagination.value.current = params.pageNum;
        pagination.value.total = data.content.total;
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

    // 电子书数据
    const ebook = ref()

    // 编辑
    const edit = (record: any) => {
      console.log(record);
      ebook.value = record;
      modalVisible.value = true;
    };

    // modal的ok事件
    const handleOk = () => {
      modalConfirmLoading.value = true;
      setTimeout(() => {
        modalVisible.value = false;
        modalConfirmLoading.value = false;
      }, 2000);
    }


    onMounted(() => {
      handleQuery({
        pageNum: 1,
        pageSize: pagination.value.pageSize
      });
    });

    return {
      columns,
      ebooks,
      pagination,
      loading,
      handleTableChange,

      modalVisible,
      modalConfirmLoading,
      edit,
      handleOk,

      ebook,
    };
  },
});
</script>
