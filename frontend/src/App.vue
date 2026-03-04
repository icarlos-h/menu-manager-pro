<!-- frontend/src/App.vue -->
<script setup>
import { computed } from "vue";
import { useRoute, useRouter } from "vue-router";
import { isLoggedIn, logout } from "./api";

const route = useRoute();
const router = useRouter();

const logged = computed(() => isLoggedIn());
const path = computed(() => route.path);

const isPublic = computed(() => path.value === "/" || path.value.startsWith("/u/"));

function goHome() {
  router.push("/");
}

function goAdmin() {
  // se não estiver logado, manda pro login
  if (!isLoggedIn()) return router.push("/login");
  router.push("/admin");
}

function goUnitPanel() {
  if (!isLoggedIn()) return router.push("/login");
  router.push("/unit");
}

function goLogin() {
  router.push("/login");
}

function doLogout() {
  logout();
  router.push("/login");
}
</script>

<template>
  <div class="app-bg app-shell">
    <!-- TOPBAR GLOBAL -->
    <header class="topbar">
      <div class="topbar-left" @click="goHome">
        <div class="brand-mark">M</div>
        <div>
          <div class="brand-title">Menu Manager Pro</div>
          <div class="brand-sub">Painel & Cardápio Público</div>
        </div>
      </div>

      <nav class="topbar-nav">
        <!-- Público -->
        <template v-if="isPublic">
          <button class="topbar-link" @click="goHome">Unidades</button>
          <button class="topbar-link" @click="goAdmin">Menu Admin</button>

          <template v-if="logged">
            <button class="topbar-link" @click="doLogout">Sair</button>
          </template>
          <template v-else>
            <button class="topbar-link" @click="goLogin">Entrar</button>
          </template>
        </template>

        <!-- Privado -->
        <template v-else>
          <button class="topbar-link" @click="goHome">Cardápio Público</button>

          <template v-if="logged">
            <button class="topbar-link" @click="goAdmin">Admin</button>
            <button class="topbar-link" @click="goUnitPanel">Unidade</button>
            <button class="topbar-link" @click="doLogout">Sair</button>
          </template>

          <template v-else>
            <button class="topbar-link" @click="goLogin">Entrar</button>
          </template>
        </template>
      </nav>
    </header>

    <!-- CONTEÚDO -->
    <main class="container">
      <router-view />
    </main>
  </div>
</template>