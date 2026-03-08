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
    const parsedId = Number(categoryId.value);

    if (!Number.isInteger(parsedId) || parsedId <= 0) {
      throw new Error("Informe um ID válido (número inteiro maior que zero).");
    }


    if (!name.value?.trim()) throw new Error("Informe o nome da categoria.");
    // Mantemos sortOrder explícito para evitar qualquer referência indefinida no runtime
    // e deixar o backend calcular automaticamente a ordem quando vier nulo.
    const sortOrder = null;
    await adminCreateCategory({ id: parsedId, name: name.value.trim(), sortOrder });
    categoryId.value = "";
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
    <div class="card mt-16" style="display:flex;gap:8px;align-items:center;flex-wrap:wrap;">
      <input v-model="categoryId" type="number" min="1" step="1" placeholder="ID" />
      <small style="width:80%;color: #6b7280;margin-left:10px;font-style: italic;">
        Atenção! Informe um ID que ainda não esteja cadastrado. Se o ID já existir, a categoria não poderá ser criada.
      </small>
      <div v-if="success" class="alert success mt-16">{{ success }}</div>
      <div class="card mt-16" style="display:flex;gap:8px;align-items:center;"></div>
      <input v-model="name" placeholder="Nome" />
      <button class="btn primary" @click="create">Criar</button>

    </div>

    <div class="card" style="margin-top:12px;">
      <table style="width:100%">

        <thead>
          <tr>
            <th>ID</th>
            <th>Nome</th>
            <th>Status</th>
            <th>Ações</th>
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


    </div>
  </div>
</template>