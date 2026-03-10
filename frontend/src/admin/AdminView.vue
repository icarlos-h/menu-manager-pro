<script setup>
import { computed, nextTick, onBeforeUnmount, onMounted, ref, watch } from "vue";
import UnitsAdmin from "./UnitsAdmin.vue";
import CategoriesAdmin from "./CategoriesAdmin.vue";
import ProductAdmin from "./ProductAdmin.vue";
import UserAdmin from "./UserAdmin.vue";
import sidebarIcon from "../assets/Logo-MMP-B.png";
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
  { key: "dashboard", label: "Visão Geral" },
  { key: "categories", label: "Categorias" },
  { key: "products", label: "Produtos" },
  { key: "units", label: "Unidades" },
  { key: "users", label: "Usuarios" },
  { key: "settings", label: "Ajustes" },
];

const createActions = [
  { key: "unit", label: "Nova unidade", tab: "units" },
  { key: "category", label: "Nova categoria", tab: "categories" },
  { key: "product", label: "Novo produto", tab: "products" },
  { key: "user", label: "Novo usuário", tab: "users" },
];

const menuNavRef = ref(null);
const menuIndicatorStyle = ref({
  transform: "translateY(0px)",
  height: "0px",
  width: "0px",
  opacity: "0",
});
const menuItemRefs = new Map();

function setMenuItemRef(key, el) {
  if (el) menuItemRefs.set(key, el);
  else menuItemRefs.delete(key);
}

function updateMenuIndicator() {
  const navEl = menuNavRef.value;
  const activeEl = menuItemRefs.get(tab.value);

  if (!navEl || !activeEl) return;

  menuIndicatorStyle.value = {
    transform: `translateY(${activeEl.offsetTop}px)`,
    height: `${activeEl.offsetHeight}px`,
    width: `${activeEl.offsetWidth}px`,
    opacity: "1",
  };
}
const tabTitle = computed(() => {
  if (tab.value === "dashboard") return "Visão Geral";
  if (tab.value === "units") return "Unidades";
  if (tab.value === "categories") return "Categorias";
  if (tab.value === "products") return "Produtos";
  if (tab.value === "settings") return "Ajustes";
  return "Usuarios";
});

const tabDescription = computed(() => {
  if (tab.value === "dashboard") return "Resumo operacional e atalhos para criação rápida.";
  if (tab.value === "units") return "Gerencie as unidades da franquia e mantenha os dados de contato atualizados.";
  if (tab.value === "categories") return "Estruture o cardápio com categorias claras e fáceis de navegar.";
  if (tab.value === "products") return "Cadastre produtos com fotos, preços e organização por categoria.";
  if (tab.value === "settings") return "Personalize preferências e parâmetros gerais do sistema.";
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

onMounted(() => {
  loadWhoami();
  nextTick(updateMenuIndicator);
  window.addEventListener("resize", updateMenuIndicator);
});

watch(tab, () => {
  nextTick(updateMenuIndicator);
});

onBeforeUnmount(() => {
  window.removeEventListener("resize", updateMenuIndicator);
});
</script>

<template>
  <div class="admin-shell">
    <aside class="admin-sidebar">
      <div class="sidebar-top">
        <div class="brand-wrap">
          <img :src="sidebarIcon" alt="Menu Manager Pro" class="brand-icon" />
        </div>

        <nav ref="menuNavRef" class="sidebar-nav">
          <div class="sidebar-indicator" :style="menuIndicatorStyle" aria-hidden="true"></div>
          <button
            v-for="section in sections"
            :key="section.key"
            :ref="(el) => setMenuItemRef(section.key, el)"
            class="sidebar-link"
            :class="{ active: tab === section.key }"
            @click="changeSection(section.key)"
          >
            <span>{{ section.label }}</span>
          </button>
        </nav>
      </div>

      <button class="logout-btn" type="button">Sair</button>
    </aside>

    <main class="admin-main">
      <section class="hero-panel">
        <div>
          <h1>{{ tabTitle }}</h1>
          <p>{{ tabDescription }}</p>
        </div>

        <div class="status-pill">
          <span class="dot"></span>
          <span>{{ loggedUser.email || "Conta autenticada" }}</span>
        </div>
      </section>

      <template v-if="tab === 'dashboard'">
        <section class="quick-cards">
          <button class="quick-card" @click="changeSection('categories')">
            <div class="card-strip"></div>
            <div class="card-row">
              <strong>Categorias</strong>
              <span>acessar ></span>
            </div>
          </button>

          <button class="quick-card" @click="changeSection('products')">
            <div class="card-strip"></div>
            <div class="card-row">
              <strong>Produtos</strong>
              <span>acessar ></span>
            </div>
          </button>

          <button class="quick-card" @click="changeSection('units')">
            <div class="card-strip"></div>
            <div class="card-row">
              <strong>Unidades</strong>
              <span>acessar ></span>
            </div>
          </button>
        </section>

        <section class="warnings-panel">
          <h2>Avisos</h2>
          <p>
            <strong>Criação rápida:</strong> Use o botão
            <span class="plus-inline">+</span>
            no canto inferior da tela para criar produto, categoria, unidade ou usuário.
          </p>

          <p>
            <strong>Fluxo Recomendado:</strong>
            1) Crie unidade, 2) cadastre categorias, 3) adicione produtos e 4) habilite usuários.
          </p>
        </section>

        <div class="floating-wrap">
          <button class="floating-plus" @click="openCreateMenu">+</button>
          <div v-if="showCreateMenu" class="floating-menu">
            <button
              v-for="action in createActions"
              :key="action.key"
              class="floating-item"
              @click="handleQuickCreate(action)"
            >
              {{ action.label }}
            </button>
          </div>
        </div>
      </template>

      <section v-else class="admin-content-panel">
        <UnitsAdmin v-if="tab === 'units'" ref="unitsAdminRef" :hide-create-button="true" />
        <CategoriesAdmin v-if="tab === 'categories'" ref="categoriesAdminRef" :hide-create-button="true" />
        <ProductAdmin v-if="tab === 'products'" ref="productsAdminRef" :hide-create-button="true" />
        <UserAdmin v-if="tab === 'users'" ref="usersAdminRef" :hide-create-button="true" />

        <article v-if="tab === 'settings'" class="settings-placeholder">
          <h3>Ajustes</h3>
          <p class="subtle">Área de configurações em preparação.</p>
        </article>
      </section>
    </main>
  </div>
</template>

<style scoped>
.admin-shell {
  display: grid;
  grid-template-columns: 285px minmax(0, 1fr);
  gap: 14px;
  min-height: 100vh;
}

.admin-sidebar {
  background: #1e232d;
  border-radius: 0 0 24px 0;
  padding: 20px 16px 18px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  min-height: 100vh;
}

.sidebar-top {
  display: grid;
  gap: 18px;
}

.brand-wrap {
  padding: 24px 10px 16px;
  display: flex;
  justify-content: center;
}

.brand-icon {
  width: 100px;
  display: block;
}


.sidebar-nav {
  position: relative;
  display: grid;
  gap: 12px;
}

.sidebar-indicator {
  position: absolute;
  left: 0;
  top: 0;
  border-radius: 999px;
  background: #ececf4;
  box-shadow: 0 8px 18px rgba(12, 18, 30, 0.3), inset 0 0 0 1px #dfe2ea;
  transition: transform 260ms cubic-bezier(0.22, 1, 0.36, 1), height 260ms cubic-bezier(0.22, 1, 0.36, 1), width 260ms cubic-bezier(0.22, 1, 0.36, 1), opacity 180ms ease;
  pointer-events: none;
  z-index: 0;
}

.sidebar-link {
  position: relative;
  z-index: 1;
  border: 0;
  background: transparent;
  color: #f1f4ff;
  text-align: left;
  padding: 12px 28px;
  border-radius: 999px;
  font-size: 16px;
  cursor: pointer;
  transition: color 220ms ease, font-weight 220ms ease;
}

.sidebar-link span {
  position: relative;
  z-index: 2;
}

.sidebar-link.active {
  color: #1e232d;
  font-weight: 700;
}

.logout-btn {
  border: 0;
  border-radius: 999px;
  background: #fff;
  color: #1a2060;
  font-size: 18px;
  font-weight: 700;
  height: 50px;
  cursor: pointer;
}

.admin-main {
  position: relative;
  display: grid;
  gap: 18px;
}

.hero-panel {
  background: #1e232d;
  color: #fff;
  border-radius: 0 0 56px 56px;
  height: 132px;
  padding: 22px 62px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  overflow: hidden;
}

.hero-panel h1 {
  margin: 0;
  font-size: 54px;
}

.hero-panel p {
  margin: 6px 0 0;
  font-size: 14px;
  color: #e8e8ef;
}

.status-pill {
  background: #dceee2;
  color: #418565;
  border-radius: 999px;
  padding: 8px 20px;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 10px;
  font-style: italic;
}

.dot {
  width: 13px;
  height: 13px;
  border-radius: 50%;
  background: #4d8f69;
}

.quick-cards {
  border: 1px solid #c4c6cd;
  border-radius: 44px;
  background: #ececed;
  padding: 14px 16px;
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
}

.quick-card {
  border: 1px solid #bdbec4;
  border-radius: 26px;
  background: #ececed;
  overflow: hidden;
  cursor: pointer;
}

.card-strip {
  height: 62px;
  background: #e1bd13;
}

.card-row {
  padding: 14px 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-row strong {
  font-size: 22px;
  color: #1e232d;
}

.card-row span {
  white-space: nowrap;
  font-size: 12px;
  color: #8f91ab;
  font-weight: 700;
}

.warnings-panel {
  min-height: 520px;
  border: 1px solid #c4c6cd;
  border-radius: 36px;
  background: #ececed;
  padding: 42px 62px;
}

.warnings-panel h2 {
  margin: 0;
  font-size: 40px;
  color: #1e232d;
}

.warnings-panel p {
  margin: 36px 0 0;
  font-size: 16px;
  line-height: 1.28;
}

.plus-inline {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 30px;
  height: 30px;
  border-radius: 50%;
  background: #5d9e7a;
  color: #fff;
  font-weight: 700;
  margin: 0 4px;
}

.admin-content-panel {
  border: 1px solid #c4c6cd;
  border-radius: 26px;
  background: #ececed;
  padding: 14px 16px;
}

.settings-placeholder {
  padding: 24px;
}

.floating-wrap {
  position: absolute;
  right: 24px;
  bottom: 24px;
}

.floating-plus {
  width: 68px;
  height: 68px;
  border-radius: 50%;
  border: 0;
  background: #5d9e7a;
  color: #fff;
  font-size: 52px;
  line-height: 1;
  cursor: pointer;
}

.floating-menu {
  position: absolute;
  right: 84px;
  bottom: 0;
  min-width: 180px;
  background: #fff;
  border: 1px solid #cfd1d8;
  border-radius: 12px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.12);
  overflow: hidden;
}

.floating-item {
  width: 100%;
  border: 0;
  padding: 10px 12px;
  text-align: left;
  background: #fff;
  cursor: pointer;
}

.floating-item:hover {
  background: #f3f4f8;
}

@media (max-width: 980px) {
  .admin-shell {
    grid-template-columns: 1fr;
  }

  .admin-sidebar {
    min-height: auto;
    border-radius: 0 0 18px 18px;
  }

  .quick-cards {
    grid-template-columns: 1fr;
  }

  .floating-wrap {
    right: 12px;
    bottom: 12px;
  }
}
</style>
