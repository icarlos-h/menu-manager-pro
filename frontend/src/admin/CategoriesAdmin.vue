<!-- frontend/src/admin/CategoriesAdmin.vue -->
<script setup>
import { ref, onMounted } from "vue";
import { adminListCategories, adminCreateCategory } from "../api";

const items = ref([]);
const name = ref("");
const sortOrder = ref(1);

async function load() {
  items.value = await adminListCategories();
}
async function create() {
  await adminCreateCategory({ name: name.value, sortOrder: Number(sortOrder.value) });
  name.value = ""; sortOrder.value = 1;
  await load();
}
onMounted(load);
</script>

<template>
  <div>
    <h2>Categorias</h2>
    <div class="card">
      <input v-model="name" placeholder="Nome" />
      <input v-model="sortOrder" type="number" style="width:80px" />
      <button @click="create">Criar</button>
    </div>

    <div class="card" style="margin-top:12px;">
      <ul>
        <li v-for="c in items" :key="c.categoryId">{{c.name}} (ord: {{c.sortOrder}})</li>
      </ul>
    </div>
  </div>
</template>