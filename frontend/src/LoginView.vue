<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import { login } from "./api";

const router = useRouter();
const email = ref("");
const password = ref("");
const loading = ref(false);
const error = ref("");

async function submit() {
  error.value = "";
  loading.value = true;

  try {
    const result = await login(email.value.trim(), password.value);

    // Ajuste automático: se backend não mandar role, a gente manda pro /admin e você testa
    if (result?.role === "ADMIN") router.push("/admin");
    else if (result?.role === "UNIT") router.push("/unit");
    else router.push("/admin");
  } catch (e) {
    error.value = e?.message || "Falha no login";
  } finally {
    loading.value = false;
  }
}
</script>

<template>
  <div class="login-wrap">
    <div class="card login-card">
      <h1>Login</h1>
      <p class="subtle">Entre para gerenciar seu cardápio.</p>

      <form @submit.prevent="submit" class="login-form">
        <input v-model="email" type="email" placeholder="E-mail" required />
        <input v-model="password" type="password" placeholder="Senha" required />

        <button class="btn primary" type="submit" :disabled="loading">
          {{ loading ? "Entrando..." : "Entrar" }}
        </button>

        <div v-if="error" class="error-box">{{ error }}</div>
      </form>
    </div>
  </div>
</template>

