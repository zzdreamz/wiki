<template>
  <a-layout>
    <a-layout-sider width="200" style="background: #fff">
      <a-menu
          style="width: 200px"
          mode="inline"
          :openKeys="state.openKeys"
          v-model:selectedKeys="state.selectedKeys"
          @openChange="onOpenChange"
      >
        <a-menu-item>
          <router-link to="/"><span>欢迎</span></router-link>
        </a-menu-item>
        <a-sub-menu v-for="parent in level1" :key="parent.id">
          <template #title>{{ parent.name }}</template>
          <a-menu-item v-for="child in parent.children" :key="child.id">{{ child.name }}</a-menu-item>
        </a-sub-menu>
      </a-menu>
    </a-layout-sider>
    <a-layout-content
        :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
    >
      <a-list item-layout="vertical" size="large" :grid="{ gutter: 20, column: 3 }" :data-source="ebooks">
        <template #renderItem="{ item }">
          <a-list-item key="item.id">
            <template #actions>
              <span v-for="{ type, text } in actions" :key="type">
                <component v-bind:is="type" style="margin-right: 8px" />
                {{ text }}
              </span>
            </template>
            <a-list-item-meta :description="item.description">
              <template #title>
                <a :href="item.href">{{ item.name }}</a>
              </template>
              <template #avatar><a-avatar :src="item.cover" /></template>
            </a-list-item-meta>
          </a-list-item>
        </template>
      </a-list>
    </a-layout-content>
  </a-layout>
</template>

<script lang="ts">
import {defineComponent, onMounted, ref} from 'vue';
import axios from "axios";
import {message} from "ant-design-vue";
import {Tool} from "@/util/tool";

const listData: Record<string, string>[] = [];



export default defineComponent({
  name: 'Home',
  setup() {
    const ebooks = ref();
    const level1 = ref();
    const state = ref();
    state.value ={
      rootSubmenuKeys: [],
      openKeys: [],
      selectedKeys: [],
    };

    const handlerQueryEbooks = () => {
      axios.get("/ebook/list", {
        params: {
          pageNum: 1,
          pageSize: 1000
        }
      }).then((response) => {
        const data = response.data;
        ebooks.value = data.content.list;
      })
    }

    const handleQueryCategory = () => {
      axios.get("/category/all").then((response) => {
        const data = response.data;
        if (data.success) {
          let categorys = data.content;

          level1.value = [];
          level1.value = Tool.array2Tree(categorys, 0);
          level1.value.forEach((item:any) => {
            state.value.rootSubmenuKeys.push(item.id);
          });
        } else {
          message.error(data.message);
        }
      })
    }

    const onOpenChange = (openKeys: string[]) => {
      const latestOpenKey = openKeys.find(key => state.value.openKeys.indexOf(key) === -1);
      if (state.value.rootSubmenuKeys.indexOf(latestOpenKey!) === -1) {
        state.value.openKeys = openKeys;
      } else {
        state.value.openKeys = latestOpenKey ? [latestOpenKey] : [];
      }
    };

    onMounted(() => {
      handleQueryCategory();
      handlerQueryEbooks();
    });


    // 图标
    const actions: Record<string, string>[] = [
      { type: 'StarOutlined', text: '156' },
      { type: 'LikeOutlined', text: '156' },
      { type: 'MessageOutlined', text: '2' },
    ];

    return {
      ebooks,
      listData,
      actions,
      state,
      onOpenChange,
      level1,
    }
  }
});
</script>

<style scoped>
  .ant-avatar {
    width: 50px;
    height: 50px;
    line-height: 50px;
    border-radius: 8%;
    margin: 5px 0;
  }
</style>
