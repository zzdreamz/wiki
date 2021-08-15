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
        <a-input v-model:value="doc.name"/>
      </a-form-item>
      <a-form-item label="父文档">
        <a-tree-select
            v-model:value="doc.parent"
            style="width: 100%"
            :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
            :tree-data="treeData"
            placeholder="请选择父文档"
            tree-default-expand-all
            :replaceFields="{title: 'name', key: 'id', value: 'id'}"
        >

        </a-tree-select>
      </a-form-item>
      <a-form-item label="顺序">
        <a-input v-model:value="doc.sort"/>
      </a-form-item>
      <a-form-item label="内容">
        <div id="docContent"></div>
      </a-form-item>
    </a-form>
  </a-modal>

</template>

<script lang="ts">
import {defineComponent, onMounted, ref} from 'vue';
import axios from "axios";
import {message} from "ant-design-vue";
import {Tool} from "@/util/tool";
import {useRoute} from "vue-router";
import E from 'wangeditor'


export default defineComponent({
  name: 'AdminDoc',
  setup() {

    const route = useRoute();
    const docs = ref();
    const level1 = ref();
    const treeData = ref();

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
        slots: {customRender: 'action'},
      },
    ];

    // 数据查询
    const handleQuery = () => {
      loading.value = true;

      axios.get("/doc/list", {
        params: {
          ebookId: route.query.ebookId,
        }
      }).then((response) => {
        loading.value = false;
        const data = response.data;
        if (data.success) {
          docs.value = data.content;

          level1.value = [];
          level1.value = Tool.array2Tree(docs.value, 0);

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

    // 将某节点及其子孙节点设置为disabled
    const setDisable = (treeData: any, id: any) => {
      for (let i = 0; i < treeData.length; i++) {
        const node = treeData[i];
        if (node.id == id) {
          node.disabled = true;
          const children = node.children;
          if (Tool.isNotEmpty(children)) {
            for (let j = 0; j < children.length; j++) {
              setDisable(children, children[j].id);
            }
          }
        } else {
          const children = node.children;
          if (Tool.isNotEmpty(children)) {
            setDisable(children, id);
          }
        }
      }
    }

    let ids: any[] = [];
    // 将某节点及其子孙节点设置为disabled
    const getDeleteIds = (treeData: any, id: any) => {
      for (let i = 0; i < treeData.length; i++) {
        const node = treeData[i];
        if (node.id == id) {
          ids.push(id);
          const children = node.children;
          if (Tool.isNotEmpty(children)) {
            for (let j = 0; j < children.length; j++) {
              getDeleteIds(children, children[j].id);
            }
          }
        } else {
          const children = node.children;
          if (Tool.isNotEmpty(children)) {
            getDeleteIds(children, id);
          }
        }
      }
    }

    // 编辑
    const edit = (record: any) => {
      doc.value = Tool.copy(record);
      modalVisible.value = true;
      const editor = new E('#docContent');
      editor.create();

      // 不能选择当前节点极其子孙节点
      treeData.value = Tool.copy(level1.value);
      setDisable(treeData.value, record.id);

      // 为选择树添加一个无
      treeData.value.unshift({id: 0, name: '无'});
    };

    // 新增
    const add = () => {
      doc.value = {
        ebookId: route.query.ebookId,
      };
      modalVisible.value = true;
      const editor = new E('#docContent');
      editor.create();

      treeData.value = Tool.copy(level1.value);
      // 为选择树添加一个无
      if (Tool.isEmpty(treeData.value)) {
        treeData.value = [{id: 0, name: '无'}];
      } else {
        treeData.value.unshift({id: 0, name: '无'});
      }
    };

    // 删除
    const handleDelete = (id: any) => {
      getDeleteIds(level1.value, id);
      axios.delete("/doc/delete/" + ids.join(',')).then((response) => {
        const data = response.data;
        if (data.success) {
          ids = [];
          handleQuery();
        }
      })
    }

    // modal的ok事件
    const handleOk = () => {
      modalConfirmLoading.value = true;
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
      treeData,
    };
  },
});
</script>
