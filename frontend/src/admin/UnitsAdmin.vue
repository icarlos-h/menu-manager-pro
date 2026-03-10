<!-- frontend/src/admin/UnitsAdmin.vue -->
<script setup>
import { ref, onMounted } from "vue";
import { adminListUnits, adminCreateUnit, adminDeleteUnit, getFriendlyDeleteError } from "../api";

defineProps({
  hideCreateButton: { type: Boolean, default: false },
});


const units = ref([]);
const error = ref("");
const pageError = ref("");
const success = ref("");
const modalError = ref("");

const creating = ref(false);
const editingId = ref(null);

const form = ref({
  name: "",
  phone: "",
  instagram: "",
  googleMapsUrl: "",
  ifoodUrl: "",
  keetaUrl: "",
  food99Url: "",
});

async function load() {
  pageError.value = "";
  try {
    units.value = await adminListUnits();
  } catch (e) {
    pageError.value = e?.message || String(e);
  }
}

function openCreate() {
  modalError.value = "";
  creating.value = true;
  editingId.value = null;
  Object.assign(form.value, {
    name: "",
    phone: "",
    instagram: "",
    googleMapsUrl: "",
    ifoodUrl: "",
    keetaUrl: "",
    food99Url: "",
  });
}

defineExpose({
  openCreateModal: openCreate,
});

function closeModal() {
  modalError.value = "";
  creating.value = false;
  editingId.value = null;
}

function menuUrl(id) {
  return `${window.location.origin}/u/${id}`;
}

async function submit() {
  modalError.value = "";
  error.value = "";
  success.value = "";

  try {
    if (!form.value.name?.trim()) throw new Error("Informe o nome da unidade.");
    if (!form.value.phone?.trim()) throw new Error("Informe o telefone.");
    if (!form.value.instagram?.trim()) throw new Error("Informe o Instagram.");

    await adminCreateUnit({
      name: form.value.name.trim(),
      phone: form.value.phone.trim(),
      instagram: form.value.instagram.trim(),
      googleMapsUrl: form.value.googleMapsUrl?.trim() || null,
      ifoodUrl: form.value.ifoodUrl?.trim() || null,
      keetaUrl: form.value.keetaUrl?.trim() || null,
      food99Url: form.value.food99Url?.trim() || null,
    });

    success.value = "Unidade criada ✅";
    closeModal();
    await load();
  } catch (e) {
    modalError.value = e?.message || String(e);
  }
}
async function removeUnit(unit) {
  if (!confirm(`Remover a unidade "${unit.name}" (ID ${unit.id})?`)) return;

  pageError.value = "";
  success.value = "";

  try {
    await adminDeleteUnit(unit.id);
    success.value = "Unidade removida ✅";
    await load();
  } catch (e) {
    const raw = e?.message || String(e);
    pageError.value = getFriendlyDeleteError("unidade", raw);

  }
}
onMounted(load);
</script>

<template>
  <div>
    <div class="page-head">
      <div>
        <h2></h2>
        <div class="subtle"></div>
      </div>

      <div class="head-actions">
        <button class="btn primary" @click="openCreate">+ Criar unidade</button>
      </div>
    </div>

    <div v-if="error" class="alert danger mt-16">{{ pageError }}</div>
    <div v-if="success" class="alert success mt-16">{{ success }}</div>

    <div class="grid-cards mt-16">
      <div v-for="u in units" :key="u.id" class="unit-card">

        <div class="unit-cover">
          <img src="../assets/capa-unidade.png" alt="capa da unidade">
        </div>

        <div class="unit-card-top">
          <div>
            <div class="unit-title">{{ u.name }}</div>
            <div class="subtle">Unidade #{{ u.id }}</div>
          </div>
          <div class="badge">#{{ u.id }}</div>
        </div>

        <div class="unit-meta">
          <div><span class="meta-label">Tel:</span> {{ u.phone }}</div>
          <div><span class="meta-label">Insta:</span> {{ u.instagram }}</div>
        </div>

        <div class="unit-actions">
          <a class="pill-link" :href="menuUrl(u.id)" target="_blank" rel="noreferrer">
            Ver cardápio
          </a>

          <button class="btn" disabled title="Editar a gente implementa no backend já já">
            Editar
          </button>

          <button class="btn" @click="removeUnit(u)">Excluir</button>
        </div>

      </div>
    </div>

    <!-- Modal Criar -->
    <div v-if="creating" class="modal-backdrop" @click.self="closeModal">
      <div class="modal">
        <div class="modal-head">
          <div>
            <div class="modal-title">Criar unidade</div>
            <div class="subtle">Telefone e Instagram são obrigatórios.</div>
          </div>
          <button class="btn ghost" @click="closeModal">✕</button>
        </div>

        <div class="modal-body">
          <div class="form-grid">
            <input v-model="form.name" placeholder="Nome da unidade" />
            <input v-model="form.phone" placeholder="Telefone" />
            <input v-model="form.instagram" placeholder="@instagram" />
            <input v-model="form.googleMapsUrl" placeholder="Google Maps URL (opcional)" />
            <input v-model="form.ifoodUrl" placeholder="iFood URL (opcional)" />
            <input v-model="form.keetaUrl" placeholder="Keeta URL (opcional)" />
            <input v-model="form.food99Url" placeholder="99Food URL (opcional)" />
          </div>
        </div>

        <div class="modal-foot">
          <div v-if="modalError" class="alert danger" style="flex:1; margin-right: 8px;">
            {{ modalError }}
          </div>
          <button class="btn" @click="closeModal">Cancelar</button>
          <button class="btn primary" @click="submit">Salvar</button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
  .unit-cover {
    width: 100%;
    height: 90px;
    /* capa fina */
    padding-bottom: 15px;
    overflow: hidden;
    border-radius: 8px 8px 0 0;
  }

  .unit-cover img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    /* corta mantendo proporção */
    display: block;
  }
</style>