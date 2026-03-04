<script setup>
import { computed, onMounted, ref } from "vue";
import { useRoute, useRouter } from "vue-router";
import { getPublicMenu } from "./api";

const route = useRoute();
const router = useRouter();

const loading = ref(false);
const error = ref("");
const menu = ref(null);

const unitId = computed(() => Number(route.params.unitId));

const activeCategoryId = ref(null);

function formatMoney(v) {
  const n = Number(v);
  return new Intl.NumberFormat("pt-BR", { style: "currency", currency: "BRL" }).format(
    Number.isFinite(n) ? n : 0
  );
}

const categories = computed(() => menu.value?.categories || []);

async function load() {
  loading.value = true;
  error.value = "";
  try {
    menu.value = await getPublicMenu(unitId.value);

    // define a primeira como ativa
    const first = menu.value?.categories?.[0]?.categoryId ?? null;
    activeCategoryId.value = first;
  } catch (e) {
    error.value = e?.message || "Erro ao carregar cardápio";
  } finally {
    loading.value = false;
  }
}

function goBack() {
  router.push("/");
}

function scrollToCategory(categoryId) {
  activeCategoryId.value = categoryId;
  const el = document.getElementById(`cat-${categoryId}`);
  if (el) el.scrollIntoView({ behavior: "smooth", block: "start" });
}

onMounted(load);
</script>

<template>
  <div class="app-bg">
    <div class="container">
      <!-- Header -->
      <div class="menu-header">
        <div>
          <div class="menu-title">Cardápio</div>
          <div class="menu-subtitle">Unidade #{{ unitId }}</div>
        </div>

        <div class="unit-links">
          <button class="btn" @click="goBack">← Unidades</button>
          <button class="btn primary" @click="load">Atualizar</button>
        </div>
      </div>

      <!-- Loading / Error -->
      <div v-if="loading" class="card">
        <div class="menu-subtitle">Carregando cardápio...</div>
      </div>

      <div v-else-if="error" class="card">
        <div class="menu-subtitle">{{ error }}</div>
      </div>

      <!-- Content -->
      <div v-else-if="menu">
        <!-- Pills (scroll horizontal) -->
        <div class="pills">
          <button
            v-for="c in categories"
            :key="c.categoryId"
            class="pill"
            :class="{ active: activeCategoryId === c.categoryId }"
            @click="scrollToCategory(c.categoryId)"
            type="button"
          >
            {{ c.name }}
          </button>
        </div>

        <!-- Sections -->
        <section v-for="c in categories" :key="c.categoryId" :id="`cat-${c.categoryId}`">
          <div class="section-title">
            <span>{{ c.name }}</span>
          </div>

          <div v-if="!c.products || c.products.length === 0" class="card">
            <div class="menu-subtitle">Nenhum produto ativo nesta categoria.</div>
          </div>

          <div v-else class="grid">
            <article v-for="p in c.products" :key="p.productId" class="menu-card">
              <div class="imgwrap">
                <img v-if="p.photoUrl" :src="p.photoUrl" :alt="p.name" />
                <div v-else style="height:100%;display:flex;align-items:center;justify-content:center;font-size:42px;">
                  🍔
                </div>

                <div v-if="p.weightGrams" class="badge">{{ p.weightGrams }}g</div>
              </div>

              <div class="body">
                <div class="name">{{ p.name }}</div>

                <div class="desc" v-if="p.description">
                  {{ p.description }}
                  <a class="more" href="javascript:void(0)">Ver mais</a>
                </div>
                <div class="desc" v-else>
                  <span class="menu-subtitle">Sem descrição.</span>
                </div>

                <div class="price">{{ formatMoney(p.price) }}</div>
              </div>
            </article>
          </div>

          <div style="height: 14px;"></div>
        </section>
      </div>
    </div>
  </div>
</template>