<script setup>
import { ref } from "vue";
import UnitsAdmin from "./UnitsAdmin.vue";
import CategoriesAdmin from "./CategoriesAdmin.vue";
import ProductAdmin from "./ProductAdmin.vue";
import UserAdmin from "./UserAdmin.vue";

const tab = ref("units");
</script>

<template>
  <div class="container">
    <div class="page-head">
      <div>
        <h1>Painel Admin</h1>
        <div class="subtle">Gerencie unidades, categorias, produtos e usuários.</div>
      </div>

      <div class="admin-tabs">
        <button class="admin-tab" :class="{active: tab==='units'}" @click="tab='units'">Unidades</button>
        <button class="admin-tab" :class="{active: tab==='categories'}" @click="tab='categories'">Categorias</button>
        <button class="admin-tab" :class="{active: tab==='products'}" @click="tab='products'">Produtos</button>
        <button class="admin-tab" :class="{active: tab==='users'}" @click="tab='users'">Usuários</button>
      </div>
    </div>

    <div class="mt-16">
      <UnitsAdmin v-if="tab==='units'" />
      <CategoriesAdmin v-if="tab==='categories'" />
      <ProductAdmin v-if="tab==='products'" />
      <UserAdmin v-if="tab==='users'" />
    </div>
  </div>
</template>