<!-- frontend/src/UnitDashboardView.vue -->
<script setup>
import { ref, onMounted } from "vue";
import { unitListProducts, unitToggleProduct, unitUpdateHeader } from "./api";
import { useRoute } from "vue-router";

const route = useRoute();
const unitId = route.params.unitId || 1;
const products = ref([]);
const header = ref({ phone: "", instagram: "", name: "" });

async function load() {
  products.value = await unitListProducts(unitId);
}
async function toggle(p) {
  const payload = { active: !p.active, priceOverride: p.priceOverride ?? null };
  await unitToggleProduct(unitId, p.productId, payload);
  await load();
}
async function saveHeader() {
  await unitUpdateHeader(unitId, header.value);
  // feedback
}
onMounted(load);
</script>

<template>
  <div>
    <h1>Unidade - Dashboard</h1>
    <div class="card">
      <h3>Header</h3>
      <input v-model="header.name" placeholder="Nome" />
      <input v-model="header.phone" placeholder="Telefone" />
      <input v-model="header.instagram" placeholder="Instagram" />
      <button @click="saveHeader">Salvar</button>
    </div>

    <div class="card" style="margin-top:12px;">
      <h3>Produtos da unidade</h3>
      <table style="width:100%">
        <thead><tr><th>Produto</th><th>Preço</th><th>Ativo</th><th>Ações</th></tr></thead>
        <tbody>
          <tr v-for="p in products" :key="p.productId">
            <td>{{p.name}}</td>
            <td>{{p.price}}</td>
            <td>{{p.active ? 'Sim' : 'Não'}}</td>
            <td>
              <button @click="toggle(p)">{{p.active ? 'Desativar' : 'Ativar'}}</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>