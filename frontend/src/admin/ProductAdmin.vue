<!-- frontend/src/admin/ProductsAdmin.vue -->
<script setup>
import { computed, ref, onMounted } from "vue";
import {
  adminListProducts,
  adminCreateProduct,
  adminListCategories,
  adminUploadProductImage,
  adminDeleteProduct,
  resolveBackendUrl,
  getFriendlyDeleteError
} from "../api";
import defaultProductImage from "../assets/default.png"; // imagem padrão para produtos sem foto
defineProps({
  hideCreateButton: { type: Boolean, default: false },
});

const categories = ref([]);
const products = ref([]);
const error = ref("");
const success = ref("");
const creating = ref(false);

const form = ref({
  categoryId: null,
  name: "",
  description: "",
  basePrice: "",
  photoUrl: "",
  weightGrams: "",
});

const imageFile = ref(null);
const imageInputRef = ref(null);

const groupedProducts = computed(() => {
  const groups = new Map();

  categories.value.forEach((category) => {
    groups.set(category.id, {
      id: category.id,
      name: category.name,
      products: [],
    });
  });

  products.value.forEach((product) => {
    const categoryId = product.categoryId;
    if (!groups.has(categoryId)) {
      groups.set(categoryId, {
        id: categoryId,
        name: product.categoryName || "Sem categoria",
        products: [],
      });
    }
    groups.get(categoryId).products.push(product);
  });

  return Array.from(groups.values())
    .filter((group) => group.products.length > 0)
    .sort((a, b) => a.name.localeCompare(b.name));
});

async function load() {
  error.value = "";
  success.value = "";
  categories.value = await adminListCategories();
  products.value = await adminListProducts();
}

function openCreate() {
  creating.value = true;
}

defineExpose({
  openCreateModal: openCreate,
});

function closeCreate() {
  creating.value = false;
  clearSelectedImage();
}

function onFileChange(e) {
  const file = e.target.files?.[0] || null;
  imageFile.value = file;
}

function clearSelectedImage() {
  imageFile.value = null;
  if (imageInputRef.value) {
    imageInputRef.value.value = "";
  }
}
function productPhotoUrl(product) {
  if (!product?.photoUrl) return defaultProductImage;
  return resolveBackendUrl(product.photoUrl);
}

function formatPrice(value) {
  const number = Number(value || 0);
  return number.toLocaleString("pt-BR", { style: "currency", currency: "BRL" });
}
async function submit() {
  error.value = "";
  success.value = "";

  try {
    if (form.value.categoryId === null) throw new Error("Selecione uma categoria.");
    if (!form.value.name?.trim()) throw new Error("Informe o nome.");
    if (form.value.basePrice === "" || !Number.isFinite(Number(form.value.basePrice))) {
      throw new Error("Informe um preço válido.");
    }

    const payload = {
      categoryId: Number(form.value.categoryId),
      name: form.value.name.trim(),
      description: form.value.description?.trim() || null,
      basePrice: Number(form.value.basePrice),
      photoUrl: form.value.photoUrl?.trim() ? form.value.photoUrl.trim() : null,
      weightGrams: form.value.weightGrams ? Number(form.value.weightGrams) : null,
    };

    const created = await adminCreateProduct(payload);

    if (imageFile.value) {
      await adminUploadProductImage(created.id, imageFile.value);
    }

    Object.assign(form.value, {
      categoryId: null,
      name: "",
      description: "",
      basePrice: "",
      photoUrl: "",
      weightGrams: "",
    });
    closeCreate();

    await load();
    success.value = "Produto criado com sucesso ✅";
  } catch (e) {
    error.value = e?.message || String(e);
  }
}


async function removeProduct(product) {
  if (!confirm(`Remover produto "${product.name}" (ID ${product.id})?`)) return;

  error.value = "";
  success.value = "";

  try {
    await adminDeleteProduct(product.id);
    success.value = "Produto removido ✅";
    await load();
  } catch (e) {
    const raw = e?.message || String(e);
    error.value = getFriendlyDeleteError("produto", raw);

  }
}
onMounted(load);
function openPhotoUrl(photoUrl) {
  return resolveBackendUrl(photoUrl);
}
</script>

<template>
  <div>
    <div class="page-head">
      <h2> </h2>
      <div class="head-actions">
        <button class="btn primary" @click="openCreate">+ Criar produto</button>
      </div>
    </div>

    <div v-if="error" class="alert danger mt-16">{{ error }}</div>
    <div v-if="success" class="alert success mt-16">{{ success }}</div>

    <div class="product-categories mt-16">
      <div v-for="category in groupedProducts" :key="category.id" class="category-card">
        <div class="category-head">
          <h3>{{ category.name }}</h3>
          <span class="subtle">{{ category.products.length }} produto(s)</span>
        </div>

        <div class="products-grid">
          <article v-for="p in category.products" :key="p.id" class="product-card">
            <img :src="productPhotoUrl(p)" :alt="p.name" class="product-photo" />
            <div class="product-content">
              <div class="product-title-row">
                <strong>{{ p.name }}</strong>
                <span class="price">{{ formatPrice(p.basePrice) }}</span>
              </div>

              <p v-if="p.description" class="subtle product-desc">{{ p.description }}</p>
              <p v-else class="subtle product-desc">Sem descrição.</p>

              <div class="product-meta">
                <span>ID {{ p.id }}</span>
                <span v-if="p.weightGrams">{{ p.weightGrams }}g</span>
              </div>

              <div class="product-actions">
                <button class="btn" @click="removeProduct(p)">Excluir</button>
              </div>
            </div>
          </article>

        </div>
      </div>

    </div>
    <div v-if="creating" class="modal-backdrop" @click.self="closeCreate">
      <div class="modal">
        <div class="modal-head">
          <div>
            <div class="modal-title">Criar produto</div>
            <div class="subtle">Preencha os dados e salve.</div>
          </div>
          <button class="btn ghost" @click="closeCreate">✕</button>
        </div>

        <form class="modal-body" @submit.prevent="submit">
          <select v-model.number="form.categoryId" required>
            <option :value="null" disabled>Escolha categoria</option>
            <option v-for="c in categories" :value="c.id" :key="c.id">{{ c.name }}</option>
          </select>

          <input v-model="form.name" placeholder="Nome" required />
          <input v-model="form.description" placeholder="Descrição (opcional)" />
          <input v-model="form.basePrice" type="number" step="0.01" placeholder="Preço" required />
          <input v-model="form.photoUrl" placeholder="URL da foto (opcional)" />
          <input v-model="form.weightGrams" type="number" step="1" placeholder="Gramatura (g)" />

          <div style="display:flex;align-items:center;gap:8px; margin-top: 8px;">
            <input ref="imageInputRef" type="file" accept="image/*" @change="onFileChange" />
            <button v-if="imageFile" type="button" class="btn" @click="clearSelectedImage">✕ Remover</button>
          </div>
          <div v-if="imageFile" class="subtle" style="margin-top:6px;">Arquivo selecionado: {{ imageFile.name }}</div>
        </form>

        <div class="modal-foot">
          <button class="btn" @click="closeCreate">Cancelar</button>
          <button class="btn primary" @click="submit">Criar produto</button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.product-categories {
  display: grid;
  gap: 16px;
}

.category-card {
  border: 1px solid var(--border);
  border-radius: var(--radius-lg);
  background: var(--paper);
  padding: 14px;
  box-shadow: var(--shadow-soft);
}

.category-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  padding-bottom: 10px;
  border-bottom: 1px dashed rgba(16, 24, 40, .12);
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
}

.product-card {
  border: 1px solid var(--border);
  border-radius: var(--radius-md);
  overflow: hidden;
  background: rgba(255, 255, 255, .9);
  display: flex;
  flex-direction: column;
}

.product-photo {
  width: 100%;
  height: 160px;
  object-fit: cover;
  background: #f2f4f7;
}

.product-content {
  padding: 10px;
  display: grid;
  gap: 8px;
}

.product-title-row {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 8px;
}

.price {
  color: var(--accent);
  font-weight: 900;
}

.product-desc {
  margin: 0;
  min-height: 38px;
}

.product-meta {
  display: flex;
  gap: 8px;
  color: var(--muted);
  font-size: 12px;
}

.product-actions {
  display: flex;
  justify-content: flex-end;
}

@media (max-width:980px) {
  .products-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width:640px) {
  .products-grid {
    grid-template-columns: 1fr;
  }
}
</style>