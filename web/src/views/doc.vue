<template>
  <a-row type="flex" justify="center">
    <span v-if="level1.length == 0">此电子书没有文档</span>
    <a-col :span="4">
      <a-tree
          v-if="level1!=null && level1.length > 0"
          class="draggable-tree"
          :tree-data="level1"
          :replaceFields="{title: 'name', key: 'id'}"
          :defaultExpandAll="true"
          @select="onSelect"
          :selected-keys="defaultSelected"
      />
    </a-col>
    <a-col :span="12">
      <div v-html="html" class="wangeditor"></div>
    </a-col>
  </a-row>
</template>

<script lang="ts">
import {defineComponent, onMounted, ref} from 'vue';
import axios from "axios";
import {message} from "ant-design-vue";
import {Tool} from "@/util/tool";
import {useRoute} from "vue-router";

export default defineComponent({
  name: 'Doc',
  setup() {
    const route = useRoute();
    const level1 = ref();
    level1.value = [];
    const html = ref();
    const defaultSelected = ref();
    defaultSelected.value = [];

    // 文档查询
    const handleQueryDocs = () => {
      axios.get("/doc/list", {
        params: {
          ebookId: route.query.ebookId,
        }
      }).then((response) => {
        const data = response.data;
        if (data.success) {
          let docs = data.content;

          level1.value = [];
          level1.value = Tool.array2Tree(docs, 0);
          defaultSelected.value = [level1.value[0].id];
          handleQueryContent(level1.value[0].id);

        } else {
          message.error(data.message);
        }
      })
    }

    // 文档内容查询
    const handleQueryContent = (id: any) => {
      axios.get("/doc/find-content/" + id).then((response) => {
        const data = response.data;
        if (data.success) {
          html.value = data.content;
        } else {
          message.error(data.message);
        }
      })
    }

    const onSelect = (selectKeys: any) => {
      defaultSelected.value = [selectKeys[0]];
      handleQueryContent(selectKeys[0]);
    }

    onMounted(() => {
      handleQueryDocs();
    });


    return {
      level1,
      html,
      defaultSelected,
      onSelect,
    }
  }
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
