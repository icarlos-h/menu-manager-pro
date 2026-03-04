<!-- frontend/src/admin/ProductsAdmin.vue -->
<script setup>
import { ref, onMounted } from "vue";
import {
  adminListProducts,
  adminCreateProduct,
  adminListCategories,
  adminUploadProductImage,
} from "../api";

const categories = ref([]);
const products = ref([]);
const error = ref("");
const success = ref("");

const form = ref({
  categoryId: null,
  name: "",
  description: "",
  basePrice: "",
  photoUrl: "",      // opcional: URL manual
  weightGrams: "",
});

const imageFile = ref(null); // arquivo selecionado

async function load() {
  error.value = "";
  success.value = "";
  categories.value = await adminListCategories();
  products.value = await adminListProducts();
}

function onFileChange(e) {
  const file = e.target.files?.[0] || null;
  imageFile.value = file;
}

async function submit() {
  error.value = "";
  success.value = "";

  try {
    if (form.value.categoryId === null) throw new Error("Selecione uma categoria.");
    if (!form.value.name?.trim()) throw new Error("Informe o nome.");
    if (form.value.basePrice === "" || !Number.isFinite(Number(form.value.basePrice)))
      throw new Error("Informe um preço válido.");

    // 1) cria produto (sem upload ainda)
    const payload = {
      categoryId: Number(form.value.categoryId),
      name: form.value.name.trim(),
      description: form.value.description?.trim() || null,
      basePrice: Number(form.value.basePrice),
      photoUrl: form.value.photoUrl?.trim() ? form.value.photoUrl.trim() : null,
      weightGrams: form.value.weightGrams ? Number(form.value.weightGrams) : null,
    };

    const created = await adminCreateProduct(payload); // precisa retornar productId

    // 2) se tem arquivo, faz upload
    if (imageFile.value) {
      await adminUploadProductImage(created.productId, imageFile.value);
    }

    // limpa form
    Object.assign(form.value, {
      categoryId: null,
      name: "",
      description: "",
      basePrice: "",
      photoUrl: "",
      weightGrams: "",
    });
    imageFile.value = null;

    await load();
    success.value = "Produto criado com sucesso ✅";
  } catch (e) {
    error.value = e?.message || String(e);
  }
}

onMounted(load);
</script>

<template>
  <div>
    <h2>Produtos</h2>

    <div v-if="error" class="card" style="border:1px solid #ffb4b4;">
      {{ error }}
    </div>
    <div v-if="success" class="card" style="border:1px solid #a6f3b0;">
      {{ success }}
    </div>

    <form class="card" @submit.prevent="submit">
      <select v-model.number="form.categoryId" required>
        <option :value="null" disabled>Escolha categoria</option>
        <option v-for="c in categories" :value="c.categoryId" :key="c.categoryId">
          {{ c.name }}
        </option>
      </select>

      <input v-model="form.name" placeholder="Nome" required />
      <input v-model="form.description" placeholder="Descrição (opcional)" />
      <input v-model="form.basePrice" type="number" step="0.01" placeholder="Preço" required />
      <input v-model="form.weightGrams" type="number" step="1" placeholder="Gramatura (g)" />

      <div class="subtle" style="margin-top:8px;">
        Foto:
      </div>
      <input type="file" accept="image/*" @change="onFileChange" />

      <button class="btn primary" type="submit">Criar Produto</button>
    </form>

    <div class="card" style="margin-top:12px;">
      <table style="width:100%">
        <thead>
          <tr>
            <th>ID</th><th>Nome</th><th>Categoria</th><th>Preço</th><th>Foto</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="p in products" :key="p.productId">
            <td>{{ p.productId }}</td>
            <td>{{ p.name }}</td>
            <td>{{ p.categoryName }}</td>
            <td>{{ p.price }}</td>
            <td>
              <a v-if="p.photoUrl" :href="p.photoUrl" target="_blank">abrir</a>
              <span v-else class="subtle">—</span>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>