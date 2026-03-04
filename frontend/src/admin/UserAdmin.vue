<!-- frontend/src/admin/UserAdmin.vue -->
<script setup>
import { ref, onMounted } from "vue";
import { adminListUnits, adminCreateUnitUser } from "../api";

const units = ref([]);
const email = ref("");
const unitId = ref("");

async function load() {
  units.value = await adminListUnits();
}
async function createUser() {
  await adminCreateUnitUser({ unitId: Number(unitId.value), email: email.value });
  email.value = ""; unitId.value = "";
  // backend cria senha temporária e retorna? se retornar, mostrar modal — aqui só limpa
}
onMounted(load);
</script>

<template>
  <div>
    <h2>Usuários (franqueado)</h2>
    <div class="card">
      <select v-model="unitId">
        <option value="">Escolha unidade</option>
        <option v-for="u in units" :value="u.id" :key="u.id">{{u.name}}</option>
      </select>
      <input v-model="email" placeholder="email@franqueado" />
      <button @click="createUser">Criar usuário de unidade</button>
    </div>
  </div>
</template>