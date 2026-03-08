<script setup>
import { computed, nextTick, ref } from "vue";
import UnitsAdmin from "./UnitsAdmin.vue";
import CategoriesAdmin from "./CategoriesAdmin.vue";
import ProductAdmin from "./ProductAdmin.vue";
import UserAdmin from "./UserAdmin.vue";
import logoImage from "../assets/logo-mmp.png";

const tab = ref("dashboard");
const showCreateMenu = ref(false);

const unitsAdminRef = ref(null);
const categoriesAdminRef = ref(null);
const productsAdminRef = ref(null);
const usersAdminRef = ref(null);

const sections = [
  { key: "dashboard", label: "Dashboard", icon: "🏠" },
  { key: "units", label: "Unidades", icon: "🏬" },
  { key: "categories", label: "Categorias", icon: "🗂️" },
  { key: "products", label: "Produtos", icon: "🍔" },
  { key: "users", label: "Usuários", icon: "👤" },
];

const createActions = [
  { key: "unit", label: "Nova unidade", tab: "units" },
  { key: "category", label: "Nova categoria", tab: "categories" },
  { key: "product", label: "Novo produto", tab: "products" },
  { key: "user", label: "Novo usuário", tab: "users" },
];

const tabTitle = computed(() => {
  if (tab.value === "dashboard") return "Visão geral";
  if (tab.value === "units") return "Unidades";
  if (tab.value === "categories") return "Categorias";
  if (tab.value === "products") return "Produtos";
  return "Usuários";
});

const tabDescription = computed(() => {
  if (tab.value === "dashboard") return "Resumo operacional e atalhos para criação rápida.";
  if (tab.value === "units") return "Gerencie as unidades da franquia e mantenha os dados de contato atualizados.";
  if (tab.value === "categories") return "Estruture o cardápio com categorias claras e fáceis de navegar.";
  if (tab.value === "products") return "Cadastre produtos com fotos, preços e organização por categoria.";
  return "Administre os acessos dos franqueados de forma segura.";
});

function changeSection(section) {
  showCreateMenu.value = false;
  tab.value = section;
}

function openCreateMenu() {
  showCreateMenu.value = !showCreateMenu.value;
}

async function handleQuickCreate(action) {
  tab.value = action.tab;
  showCreateMenu.value = false;

  await nextTick();

  if (action.tab === "units") unitsAdminRef.value?.openCreateModal?.();
  if (action.tab === "categories") categoriesAdminRef.value?.openCreateModal?.();
  if (action.tab === "products") productsAdminRef.value?.openCreateModal?.();
  if (action.tab === "users") usersAdminRef.value?.openCreateModal?.();
}

</script>

<template>
  <div class="admin-shell">

    <aside class="admin-sidebar">

      <div class="admin-logo">
        <img :src="logoImage" alt="Menu Manager Pro" />
        <div>
          <strong>Menu Manager</strong>
          <div class="subtle">Painel Pro</div>
        </div>
      </div>
      <nav class="sidebar-nav">
        <button v-for="section in sections" :key="section.key" class="sidebar-link"
          :class="{ active: tab === section.key }" @click="changeSection(section.key)">
          <span>{{ section.icon }}</span>
          {{ section.label }}
        </button>
      </nav>

      <div class="sidebar-create">
        <button class="btn primary quick-create-btn" @click="openCreateMenu">+ Create new</button>
        <div v-if="showCreateMenu" class="create-menu card">
          <button v-for="action in createActions" :key="action.key" class="create-menu-item"
            @click="handleQuickCreate(action)">
            {{ action.label }}
          </button>
        </div>
      </div>
    </aside>

    <main class="admin-main">
      <section class="admin-hero card">
        <div>
          <h1>{{ tabTitle }}</h1>
          <div class="subtle">{{ tabDescription }}</div>
        </div>
      </section>
      <section v-if="tab === 'dashboard'" class="admin-content-panel mt-16 dashboard-grid">
        <article class="card">
          <h3>Criação rápida</h3>
          <p class="subtle">Use o botão <strong>Create new</strong> no menu lateral para criar produto, categoria,
            unidade ou usuário.</p>
        </article>
        <article class="card">
          <h3>Fluxo recomendado</h3>
          <p class="subtle">1) Crie unidade, 2) cadastre categorias, 3) adicione produtos e 4) habilite usuários.</p>
        </article>
      </section>

      <section v-else class="admin-content-panel mt-16">
        <UnitsAdmin v-if="tab === 'units'" ref="unitsAdminRef" :hide-create-button="true" />
        <CategoriesAdmin v-if="tab === 'categories'" ref="categoriesAdminRef" :hide-create-button="true" />
        <ProductAdmin v-if="tab === 'products'" ref="productsAdminRef" :hide-create-button="true" />
        <UserAdmin v-if="tab === 'users'" ref="usersAdminRef" :hide-create-button="true" />
      </section>
    </main>
  </div>
</template>

<style scoped>
.admin-shell {
  display: grid;
  grid-template-columns: 260px minmax(0, 1fr);
  gap: 16px;
}

.admin-sidebar {
  background: linear-gradient(180deg, var(--brand), var(--brand-2));
  border-radius: 20px;
  padding: 16px;
  color: #fff;
  display: grid;
  gap: 16px;
  align-content: start;
  position: sticky;
  top: 76px;
  height: fit-content;
}

.admin-logo {
  display: flex;
  align-items: center;
  gap: 10px;
}

.admin-logo img {
  width: 42px;
  height: 42px;
  border-radius: 12px;
  object-fit: cover;
  background: #fff;
}

.admin-logo .subtle {
  color: rgba(255, 255, 255, .8);
}

.sidebar-nav {
  display: grid;
  gap: 8px;
}

.sidebar-link {
  border: 1px solid rgba(255, 255, 255, .22);
  color: #fff;
  background: rgba(255, 255, 255, .08);
  border-radius: 12px;
  padding: 10px 12px;
  text-align: left;
  font-weight: 700;
  cursor: pointer;
}

.sidebar-link span {
  margin-right: 8px;
}

.sidebar-link.active {
  background: #fff;
  color: var(--brand);
  border-color: transparent;
}

.sidebar-create {
  position: relative;
}

.quick-create-btn {
  width: 100%;
  justify-content: center;
}

.create-menu {
  margin-top: 10px;
  padding: 8px;
  display: grid;
  gap: 6px;
}

.create-menu-item {
  border: 1px solid var(--border);
  border-radius: 10px;
  background: #fff;
  padding: 9px 10px;
  text-align: left;
  cursor: pointer;
  font-weight: 700;
}

.admin-main {
  min-width: 0;
}

.dashboard-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

@media (max-width: 980px) {
  .admin-shell {
    grid-template-columns: 1fr;
  }

  .admin-sidebar {
    position: static;
  }
}

@media (max-width: 640px) {
  .dashboard-grid {
    grid-template-columns: 1fr;
  }
}
</style>
