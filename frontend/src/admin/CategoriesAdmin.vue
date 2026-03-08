<script setup>
import { computed, ref, onMounted } from "vue";
import { adminListCategories, adminCreateCategory, adminDeleteCategory, getFriendlyDeleteError } from "../api";

defineProps({
  hideCreateButton: { type: Boolean, default: false },
});

const items = ref([]);
const name = ref("");
const error = ref("");
const success = ref("");
const creating = ref(false);

const activeCount = computed(() => items.value.filter((c) => c.active).length);

async function load() {
  error.value = "";
  items.value = await adminListCategories();
}

function openCreate() {
  creating.value = true;
  name.value = "";
}

defineExpose({
  openCreateModal: openCreate,
});

function closeCreate() {
  creating.value = false;
}

async function create() {
  error.value = "";
  success.value = "";

  try {
    const trimmedName = name.value?.trim();
    if (!trimmedName) {
      throw new Error("Informe o nome da categoria.");
    }
    await adminCreateCategory({ name: trimmedName });
    name.value = "";
    closeCreate();
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
    <div class="page-head">
      <div>
        <h2>Categorias</h2>
        <div class="subtle">Organize os produtos por categoria de forma rápida.</div>
      </div>

    </div>
    <div class="head-actions categories-stats">
      <button class="btn primary" @click="openCreate">+ Criar categoria</button>

    </div>
    <div v-if="error" class="alert danger mt-16">{{ error }}</div>
    <div v-if="success" class="alert success mt-16">{{ success }}</div>

    <div class="category-grid mt-16">
      <article v-for="c in items" :key="c.id" class="category-item-card">
        <div class="category-item-top">
          <strong>{{ c.name }}</strong>
          <span class="category-status" :class="c.active ? 'active' : 'inactive'">
            {{ c.active ? "Ativa" : "Inativa" }}
          </span>
        </div>
        <div class="subtle">ID {{ c.id }}</div>
        <div class="category-item-actions">
          <button class="btn danger" @click="removeCategory(c)">Remover</button>
        </div>
      </article>
    </div>

    <div v-if="creating" class="modal-backdrop" @click.self="closeCreate">
      <div class="modal">
        <div class="modal-head">
          <div>
            <div class="modal-title">Criar categoria</div>
            <div class="subtle">Digite o nome da nova categoria.</div>
          </div>
          <button class="btn ghost" @click="closeCreate">✕</button>
        </div>

        <form class="modal-body" @submit.prevent="create">
          <input v-model="name" placeholder="Nome da categoria" autofocus />
        </form>

        <div class="modal-foot">
          <button class="btn" @click="closeCreate">Cancelar</button>
          <button class="btn primary" @click="create">Criar</button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.categories-stats {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.badge {
  padding: 8px 12px;
  border-radius: 999px;
  border: 1px solid var(--border);
  background: rgba(255, 255, 255, .8);
  font-weight: 800;
}

.category-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
}

.category-item-card {
  border: 1px solid var(--border);
  border-radius: var(--radius-md);
  background: var(--paper);
  padding: 14px;
  box-shadow: var(--shadow-soft);
  display: grid;
  gap: 10px;
}

.category-item-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 8px;
}

.category-status {
  padding: 4px 10px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 800;
}

.category-status.active {
  background: rgba(24, 169, 87, .12);
  color: #116235;
}

.category-status.inactive {
  background: rgba(130, 130, 130, .14);
  color: #4b5563;
}

.category-item-actions {
  display: flex;
  justify-content: flex-end;
}

@media (max-width:900px) {
  .category-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width:640px) {
  .category-grid {
    grid-template-columns: 1fr;
  }
}
</style>