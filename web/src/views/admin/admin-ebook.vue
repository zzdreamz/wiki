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
    </a-form>
  </a-modal>
</template>

<script lang="ts">
import {defineComponent, onMounted, ref} from 'vue';
import axios from "axios";
import {message} from "ant-design-vue";
import {Tool} from "@/util/tool";

export default defineComponent({
  name: 'AdminEbook',
  setup() {

    const ebooks = ref();
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
            params: {
              pageNum: params.pageNum,
              pageSize: params.pageSize,
              name: queryParam.value.name
            }
          }).then((response) => {
        loading.value = false;
        const data = response.data;
        if (data.success) {
          ebooks.value = data.content.list;

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

    // 电子书数据
    const ebook = ref()

    // 编辑
    const edit = (record: any) => {
      ebook.value = Tool.copy(record);
      modalVisible.value = true;
    };

    // 新增
    const add = () => {
      ebook.value = {};
      modalVisible.value = true;
    };

    // 删除
    const handleDelete = (id: any) => {
      axios.delete("/ebook/delete/" + id).then((response) => {
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
      console.log(ebook.value);
      axios.post("/ebook/save", ebook.value).then((response) => {
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
      ebooks,
      pagination,
      loading,
      handleTableChange,

      modalVisible,
      modalConfirmLoading,
      edit,
      add,
      handleDelete,
      handleOk,

      ebook,

      searchName,
      onSearch,
    };
  },
});
</script>
