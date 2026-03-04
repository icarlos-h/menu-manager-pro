<script setup>
import { onMounted, ref } from "vue";
import { useRouter } from "vue-router";
import { getPublicUnits } from "./api";

const router = useRouter();
const units = ref([]);
const loading = ref(false);
const error = ref("");

async function load() {
  loading.value = true;
  error.value = "";
  try {
    units.value = await getPublicUnits();
  } catch (e) {
    error.value = e?.message || "Erro ao carregar unidades";
  } finally {
    loading.value = false;
  }
}

function openUnit(u) {
  router.push(`/u/${u.id}`);
}

onMounted(load);
</script>

<template>
  <div>
    <div class="page-head">
      <div>
        <h1>Escolha a unidade</h1>
        <div class="subtle">Selecione a unidade para ver o cardápio.</div>
      </div>
      <button class="btn" @click="load">Atualizar</button>
    </div>

    <div v-if="loading" class="card mt-16">
      <div class="subtle">Carregando unidades...</div>
    </div>

    <div v-if="error" class="card mt-16">
      <div class="subtle">{{ error }}</div>
    </div>

    <div v-if="!loading && !error" class="grid mt-16">
      <button
        v-for="u in units"
        :key="u.id"
        class="card unit-card"
        @click="openUnit(u)"
      >
        <div class="unit-title">{{ u.name }}</div>
        <div class="unit-meta">
          <span v-if="u.phone">📞 {{ u.phone }}</span>
          <span v-if="u.instagram">📷 {{ u.instagram }}</span>
        </div>
      </button>

      <div v-if="units.length === 0" class="card">
        <div class="subtle">Nenhuma unidade cadastrada.</div>
      </div>
    </div>
  </div>
</template>