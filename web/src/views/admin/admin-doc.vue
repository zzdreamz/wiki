<template>
  <a-layout>
    <a-layout-content
        :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
    >
      <a-row :gutter="96">
        <a-col :span="3"></a-col>
        <a-col :span="9">
          <a-button type="primary" @click="add">新增</a-button>
          <a-table
              :columns="columns"
              :row-key="record => record.id"
              :data-source="level1"
              :loading="loading"
              :pagination="false"
              size="small"
          >
            <template #name="{text, record}">
              {{record.sort}} {{record.name}}
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
        </a-col>
        <a-col :span="12">
          <a-form :model="doc" :label-col="{span: 6}" :wrapper-col="{span: 20}">
            <a-form-item label="">
              <a-space size="small">
                <a-button type="primary" @click="showDrawer">内容预览</a-button>
                <a-button type="primary" @click="handleSave">保存</a-button>
              </a-space>
            </a-form-item>
            <a-form-item label="">
              <a-input v-model:value="doc.name" placeholder="文档名称"/>
            </a-form-item>
            <a-form-item label="">
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
            <a-form-item label="">
              <a-input v-model:value="doc.sort" placeholder="顺序"/>
            </a-form-item>
            <a-form-item label="">
              <div id="docContent"></div>
            </a-form-item>
          </a-form>
        </a-col>
      </a-row>
    </a-layout-content>
    <a-drawer
        title="Basic Drawer"
        placement="right"
        :closable="false"
        v-model:visible="drawerVisible"
        :width="1440"
    >
      <div v-html="html" class="wangeditor"></div>
    </a-drawer>
  </a-layout>
<!--  <a-modal-->
<!--      title="文档表单"-->
<!--      v-model:visible="modalVisible"-->
<!--      :confirm-loading="modalConfirmLoading"-->
<!--      @ok="handleOk"-->
<!--  >-->

<!--  </a-modal>-->

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
    const drawerVisible = ref();
    drawerVisible.value = false;
    const treeData = ref();
    treeData.value = [];
    const html = ref();

    const loading = ref(false)

    const columns = [
      {
        title: '名称',
        dataIndex: 'name',
      },
      {
        title: '顺序 名称',
        slots: {customRender: 'name'},
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

          treeData.value = Tool.copy(level1.value);
          treeData.value.unshift({id: 0, name: '无'});

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
    doc.value = {};

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
      handleQueryContent(doc.value.id);

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
      editor.txt.html('');

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

    const handleSave = () => {
      doc.value.content = editor.txt.html();
      doc.value.ebookId = route.query.ebookId;
      delete doc.value.children;
      if (Tool.isNotEmpty(doc.value.children)) doc.value.children = doc.value.children.join(",");
      axios.post("/doc/save", doc.value).then((response) => {
        const data = response.data;
        if (data.success) {
          message.success(data.message);
          handleQuery();
        } else {
          message.error(data.message);
        }
      })
    }


    const handleQueryContent = (id: any) => {
      axios.get("/doc/find-content/" + id).then((response) => {
        const data = response.data;
        if (data.success) {
          editor.txt.html(data.content);
        } else {
          message.error(data.message);
        }
      })
    }

    const showDrawer = () => {
      html.value = editor.txt.html();
      drawerVisible.value = true;
    }

    let editor :any;
    onMounted(() => {
      handleQuery();
      editor = new E('#docContent')
      editor.config.zIndex=0;
      editor.create();
    });

    return {
      columns,
      level1,
      loading,
      drawerVisible,
      modalVisible,
      modalConfirmLoading,
      doc,
      treeData,
      html,
      edit,
      add,
      handleDelete,
      handleSave,
      showDrawer,
    };
  },
});
</script>

<style>
  /*wangeditor的样式*/
  /* table 样式 */
  .wangeditor table {
    border-top: 1px solid #ccc;
    border-left: 1px solid #ccc;
  }
  .wangeditor table td,
  .wangeditor table th {
    border-bottom: 1px solid #ccc;
    border-right: 1px solid #ccc;
    padding: 3px 5px;
  }
  .wangeditor table th {
    border-bottom: 2px solid #ccc;
    text-align: center;
  }

  /* blockquote 样式 */
  .wangeditor blockquote {
    display: block;
    border-left: 8px solid #d0e5f2;
    padding: 5px 10px;
    margin: 10px 0;
    line-height: 1.4;
    font-size: 100%;
    background-color: #f1f1f1;
  }

  /* code 样式 */
  .wangeditor code {
    display: inline-block;
    *display: inline;
    *zoom: 1;
    background-color: #f1f1f1;
    border-radius: 3px;
    padding: 3px 5px;
    margin: 0 3px;
  }
  .wangeditor pre code {
    display: block;
  }

  /* ul ol 样式 */
  .wangeditor ul, .wangeditor ol {
    margin: 10px 0 10px 20px;
  }

  .wangeditor blockquote p {
    margin: 20px 10px !important;
  }
</style>
