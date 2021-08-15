<template>
  <a-layout>
    <a-layout-content
        :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
    >
      <a-button type="primary" @click="add">新增</a-button>
      <a-table
          :columns="columns"
          :row-key="record => record.id"
          :data-source="level1"
          :loading="loading"
          :pagination="false"
      >
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
      title="文档表单"
      v-model:visible="modalVisible"
      :confirm-loading="modalConfirmLoading"
      @ok="handleOk"
  >
    <a-form :model="doc" :label-col="{span: 6}" :wrapper-col="{span: 12}">
      <a-form-item label="名称">
        <a-input v-model:value="doc.name" />
      </a-form-item>
      <a-form-item label="父文档">
        <a-select
            ref="select"
            v-model:value="doc.parent"
        >
          <a-select-option value="0">无</a-select-option>
          <a-select-option v-for="c in level1" :key="c.id" :value="c.id" :disabled="doc.id === c.id">{{ c.name }}</a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item label="顺序">
        <a-input v-model:value="doc.sort" />
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
  name: 'AdminDoc',
  setup() {

    const docs = ref();
    const level1 = ref();

    const loading = ref(false)

    const columns = [
      {
        title: '名称',
        dataIndex: 'name',
      },
      {
        title: '父文档',
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
    const handleQuery = () => {
      loading.value = true;

      axios.get("/doc/all").then((response) => {
        loading.value = false;
        const data = response.data;
        if (data.success) {
          docs.value = data.content;

          level1.value = [];
          level1.value = Tool.array2Tree(docs.value,0);
          console.log(level1);

        } else {
          message.error(data.message);
        }
      })
    }

    // modal相关变量
    const modalVisible = ref<boolean>(false);
    const modalConfirmLoading = ref<boolean>(false);

    // 文档数据
    const doc = ref()

    // 编辑
    const edit = (record: any) => {
      doc.value = Tool.copy(record);
      modalVisible.value = true;
    };

    // 新增
    const add = () => {
      doc.value = {};
      modalVisible.value = true;
    };

    // 删除
    const handleDelete = (id: any) => {
      axios.delete("/doc/delete/" + id).then((response) => {
        const data = response.data;
        if (data.success) {
          handleQuery();
        }
      })
    }

    // modal的ok事件
    const handleOk = () => {
      modalConfirmLoading.value = true;
      console.log(doc.value);
      axios.post("/doc/save", doc.value).then((response) => {
        const data = response.data;
        if (data.success) {
          modalVisible.value = false;
          handleQuery();
        } else {
          message.error(data.message);
        }
        modalConfirmLoading.value = false;
      })
    }

    onMounted(() => {
      handleQuery();
    });

    return {
      columns,
      level1,
      loading,

      modalVisible,
      modalConfirmLoading,
      edit,
      add,
      handleDelete,
      handleOk,

      doc,
    };
  },
});
</script>