<script setup>
import { ref, onMounted } from "vue";
import { adminListCategories, adminCreateCategory, adminDeleteCategory, getFriendlyDeleteError } from "../api";

const items = ref([]);
const name = ref("");
const error = ref("");
const success = ref("");

async function load() {
    error.value = "";
  items.value = await adminListCategories();
}
async function create() {
  await adminCreateCategory({ name: name.value, sortOrder: Number(sortOrder.value) });
  error.value = "";
  name.value = ""; sortOrder.value = 1;
  success.value = "";
  await load();

  try {
    if (!name.value?.trim()) throw new Error("Informe o nome da categoria.");

    await adminCreateCategory({ name: name.value.trim() });
    name.value = "";
    success.value = "Categoria criada ✅";
    await load();
  } catch (e) {
    error.value = e?.message || String(e);
  }
}

async function removeCategory(category) {
  if (!confirm(`Remover categoria "${category.name}" (ID ${category.id})?`)) return;

  error.value = "";
  success.value = "";

  try {
    await adminDeleteCategory(category.id);
    success.value = "Categoria removida ✅";
    await load();
  } catch (e) {
    const raw = e?.message || String(e);
    error.value = getFriendlyDeleteError("categoria", raw);

  }
}

onMounted(load);
</script>

<template>
  <div>
    <h2>Categorias</h2>
<div v-if="error" class="alert danger mt-16">{{ error }}</div>
    <div v-if="success" class="alert success mt-16">{{ success }}</div>

    <div class="card mt-16" style="display:flex;gap:8px;align-items:center;">
<input v-model="name" placeholder="Nome" />
<button class="btn primary" @click="create">Criar</button>
 </div><div class="card" style="margin-top:12px;">
<table style="width:100%">

<thead>
          <tr>
            <th>ID</th><th>Nome</th><th>Status</th><th>Ações</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="c in items" :key="c.id">
            <td>{{ c.id }}</td>
            <td>{{ c.name }}</td>
            <td>{{ c.active ? "Ativa" : "Inativa" }}</td>
            <td>
              <button class="btn" @click="removeCategory(c)">Excluir</button>
            </td>
          </tr>
        </tbody>
      </table>


</div></div>
</template>