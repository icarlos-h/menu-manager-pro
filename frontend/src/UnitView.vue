<script setup>
import { computed, ref } from "vue";
import { logout } from "./api";
import { useRouter } from "vue-router";

const router = useRouter();

const tabs = [
  { key: "header", label: "Cabeçalho da Unidade" },
  { key: "categories", label: "Categorias" },
  { key: "products", label: "Produtos" },
];

const active = ref("header");
const activeLabel = computed(() => tabs.find(t => t.key === active.value)?.label ?? "");

function doLogout() {
  logout();
  router.push("/login");
}
</script>

<template>
  <div>
    <div class="page-title">
      <div>
        <h1>Painel da Unidade</h1>
        <div class="subtle">Ative/inative itens e personalize o cardápio da sua unidade.</div>
      </div>

      <div class="row-actions">
        <button class="btn" @click="$router.push('/')">Ver público</button>
        <button class="btn danger" @click="doLogout">Sair</button>
      </div>
    </div>

    <div class="tabs mt-16">
      <button
        v-for="t in tabs"
        :key="t.key"
        class="tab"
        :class="{ active: t.key === active }"
        type="button"
        @click="active = t.key"
      >
        {{ t.label }}
      </button>
    </div>

    <div class="card mt-16">
      <div class="card-title-inline">
        <div>
          <div class="section-title">{{ activeLabel }}</div>
          <div class="subtle">Em construção — próximo passo: conectar com a API.</div>
        </div>
      </div>

      <div class="placeholder">
        <div class="subtle">
          Aqui vamos montar os controles de <b>{{ activeLabel }}</b>.
        </div>
      </div>
    </div>
  </div>
</template>