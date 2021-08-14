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
        <template #category="{ text: record }">
          <span>{{ getCategoryName(record.category1Id)}} / {{ getCategoryName(record.category2Id) }}</span>
        </template>
        <template #action="{text, record}">
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
      title="电子书表单"
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
      <a-form-item label="分类">
        <a-cascader
            v-model:value="categoryIds"
            :options="level1"
            :field-names="{label: 'name', value: 'id', children: 'children'}"
            placeholder="Please select"
        />
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
        title: '分类',
        key: 'category',
        slots: { customRender: 'category' },
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

    const categoryIds = ref();

    // modal相关变量
    const modalVisible = ref<boolean>(false);
    const modalConfirmLoading = ref<boolean>(false);

    // 电子书数据
    const ebook = ref()

    // 编辑
    const edit = (record: any) => {
      ebook.value = Tool.copy(record);
      categoryIds.value = [ebook.value.category1Id, ebook.value.category2Id]
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
      ebook.value.category1Id = categoryIds.value[0];
      ebook.value.category2Id = categoryIds.value[1];
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

    // 查询所有分类
    const level1 = ref();
    let categorys: any[] = [];

    const handleQueryCategory = () => {
      loading.value = true;

      axios.get("/category/all").then((response) => {
        loading.value = false;
        const data = response.data;
        if (data.success) {
          categorys = data.content;

          level1.value = [];
          level1.value = Tool.array2Tree(categorys,0);

          // 加载完所有分类后再去查询电子书，否则渲染会出错
          handleQuery({
            pageNum: 1,
            pageSize: pagination.value.pageSize
          });
        } else {
          message.error(data.message);
        }
      })
    }

    const getCategoryName = (categoryId: any) => {
      let name = "";
      categorys.forEach((item: any) => {
        if (item.id == categoryId) {
          name = item.name;
        }
      });
      return name;
    };

    onMounted(() => {
      handleQueryCategory();
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
      level1,
      categoryIds,

      getCategoryName,
    };
  },
});
</script>
