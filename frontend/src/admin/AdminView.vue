<script setup>
import { computed, nextTick, onMounted, ref } from "vue";
import UnitsAdmin from "./UnitsAdmin.vue";
import CategoriesAdmin from "./CategoriesAdmin.vue";
import ProductAdmin from "./ProductAdmin.vue";
import UserAdmin from "./UserAdmin.vue";
import logoImage from "../assets/Logo-MMP-B.png";
import { whoami } from "../api";

const tab = ref("dashboard");
const showCreateMenu = ref(false);

const loggedUser = ref({
  email: "",
  role: "",
});

const unitsAdminRef = ref(null);
const categoriesAdminRef = ref(null);
const productsAdminRef = ref(null);
const usersAdminRef = ref(null);

const sections = [
  { key: "dashboard", label: "Dashboard", icon: "" },
  { key: "units", label: "Unidades", icon: "" },
  { key: "categories", label: "Categorias", icon: "" },
  { key: "products", label: "Produtos", icon: "" },
  { key: "users", label: "Usuários", icon: "" },
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

async function loadWhoami() {
  try {
    const me = await whoami();
    loggedUser.value = {
      email: me?.email || "Conta autenticada",
      role: me?.role || "USER",
    };
  } catch {
    loggedUser.value = {
      email: "Conta autenticada",
      role: "USER",
    };
  }
}

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

onMounted(loadWhoami);
</script>

<template>
  <div class="admin-shell">

    <aside class="admin-sidebar">

      <div class="admin-top">
        <div class="admin-logo-wrap">
          <img :src="logoImage" alt="Menu Manager Pro" class="admin-logo" />
        </div>

        <nav class="sidebar-nav">

          <button v-for="section in sections" :key="section.key" class="sidebar-link"
            :class="{ active: tab === section.key }" @click="changeSection(section.key)">
            <span class="sidebar-icon">{{ section.icon }}</span>
            <span>{{ section.label }}</span>
          </button>
        </nav>
      </div>

      <div class="sidebar-bottom">
        <div class="create-block">
          <button class="create-btn" @click="openCreateMenu">+ Create New</button>
          <div v-if="showCreateMenu" class="create-menu">
            <button v-for="action in createActions" :key="action.key" class="create-menu-item"
              @click="handleQuickCreate(action)">
              {{ action.label }}
            </button>
          </div>
        </div>

        <div class="login-status">
          <div class="status-label">Status login</div>
          <div class="status-email">{{ loggedUser.email }}</div>
          <div class="status-role">Perfil: {{ loggedUser.role }}</div>
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
          <div class="card-content">
            <div class="card-text">
              <h3>Criação rápida</h3>
              <p class="subtle">
                Use o botão <strong>Create new</strong> no menu lateral para criar produto, categoria,
                unidade ou usuário.
              </p>
            </div>

            <img src="../assets/LOGO-MMP.png" alt="Menu Manager Pro" class="card-image">
          </div>
        </article>
        
        <article class="card">
          <div class="card-content">
            <div class="card-text">
              <h3>Fluxo recomendado</h3>
              <p class="subtle">1) Crie unidade, 2) cadastre categorias, 3) adicione produtos e 4) habilite usuários.</p>
            </div>

            <img src="../assets/LOGO-MMP.png" alt="Menu Manager Pro" class="card-image">
          </div>
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
  gap: 20px;
  min-height: calc(100vh - 120px);
}

.admin-sidebar {
  background: var(--brand);
  border-radius: 23px;
  padding: 18px 14px;
  color: #fff;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  position: sticky;
  top: 84px;
  height: calc(100vh - 106px);
}

.admin-top {
  display: grid;
  gap: 18px;
}

.admin-logo-wrap {
  background: rgba(255, 255, 255, .12);
  border-radius: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 14px 10px;
}

.admin-logo {
  width: 132px;
  max-width: 100%;
  height: auto;
  object-fit: contain;
}

.sidebar-nav {
  display: grid;
  gap: 8px;
}
.card-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.card-text {
  max-width: 70%;
}

.card-image {
  width: 80px;
  height: auto;
  object-fit: contain;
}
.sidebar-link {
  border: 0;
  color: #fff;
  background: transparent;
  border-radius: 12px;
  padding: 12px;
  text-align: left;
  font-weight: 700;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 10px;
}

.sidebar-link:hover {
  background: rgba(255, 255, 255, .12);
}

.sidebar-link.active {
  background: #fff;
  color: #0d49e6;
  font-weight: 800;
}

.sidebar-icon {
  width: 18px;
  display: inline-flex;
  justify-content: center;
}

.sidebar-bottom {
  display: grid;
  gap: 12px;
}

.create-block {
  position: relative;
}

.create-btn {
  width: 100%;
  border: 0;
  border-radius: 12px;
  background: #fff;
  color: #0d49e6;
  font-weight: 800;
  padding: 11px 12px;
  cursor: pointer;
}

.create-menu {
  margin-top: 8px;
  background: #ffffff;
  border-radius: 12px;
  padding: 8px;
  display: grid;
  gap: 6px;
}

.create-menu-item {
  border: 0;
  border-radius: 10px;
  background: #edf2ff;
  color: #12307c;
  font-weight: 700;
  text-align: left;
  padding: 10px;
  cursor: pointer;
}

.create-menu-item:hover {
  background: #dfe8ff;
}

.login-status {
  background: rgba(255, 255, 255, .14);
  border: 1px solid rgba(255, 255, 255, .2);
  border-radius: 14px;
  padding: 10px;
  display: grid;
  gap: 4px;
}

.status-label {
  font-size: 12px;
  font-weight: 700;
  opacity: .9;
}

.status-email {
  font-size: 13px;
  font-weight: 800;
  word-break: break-word;
}

.status-role {
  font-size: 12px;
  opacity: .9;
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
    min-height: auto;
  }

  .admin-sidebar {
    position: static;
    height: auto;
  }
}

@media (max-width: 640px) {
  .dashboard-grid {
    grid-template-columns: 1fr;
  }
}
</style>