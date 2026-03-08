<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import { login } from "./api";
import loginSideImage from "./assets/capa.png";
import logoImage from "./assets/logo-mmp.png";

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
    <div class="login-shell">
      <section class="login-left">
        <div class="brand-chip">
          <img :src="logoImage" alt="Logo do Menu Manager Pro" />
        </div>
        <h1>Bem-vindo de volta 👋</h1>
        <p class="subtle">Entre para gerenciar seu cardápio com segurança e rapidez.</p>

        <form @submit.prevent="submit" class="login-form">
          <input v-model="email" type="email" placeholder="E-mail" required />
          <input v-model="password" type="password" placeholder="Senha" required />
          <button class="btn primary login-btn" type="submit" :disabled="loading">
            {{ loading ? "Entrando..." : "Entrar" }}
          </button>
          <div v-if="error" class="alert danger">{{ error }}</div>
        </form>
      </section>

      <aside class="login-right">
        <img :src="loginSideImage" alt="Imagem de destaque do login" />
      </aside>

    </div>
  </div>
</template>

<style scoped>
.login-wrap {
  min-height: calc(100vh - 90px);
  display: grid;
  place-items: center;
  padding: 24px;
}

.login-shell {
  width: min(1100px, 100%);
  background: var(--paper-solid);
  border-radius: 28px;
  border: 1px solid var(--border);
  box-shadow: var(--shadow);
  padding: 18px;
  display: grid;
  grid-template-columns: 1fr 0.95fr;
  gap: 18px;
}

.login-left {
  padding: 18px 22px;
  display: grid;
  align-content: center;
  gap: 14px;
}

.brand-chip img{
  width: 50%;
  margin-bottom: 40px;
  padding: 18px 22px;
  padding: 6px 12px;
}

.login-left h1 {
  font-size: clamp(1.8rem, 2.6vw, 2.8rem);
  line-height: 1.1;
  margin: 0;
}

.login-form {
  margin-top: 8px;
  display: grid;
  gap: 10px;
  max-width: 420px;
}

.login-btn {
  margin-top: 4px;
  padding-top: 12px;
  padding-bottom: 12px;
}

.login-right {
  border-radius: 22px;
  overflow: hidden;
  border: 1px solid var(--border);
  min-height: 500px;
  background: linear-gradient(135deg, rgba(11, 15, 94, 0.12), rgba(29, 43, 216, 0.08));
}

.login-right img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

@media (max-width: 900px) {
  .login-shell {
    grid-template-columns: 1fr;
  }

  .login-right {
    min-height: 260px;
    order: -1;
  }
}
</style>