<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import { login } from "./api";
import loginSideImage from "./assets/capa.png";
import logoImage from "./assets/LOGO-MMP.png";

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
        <img class="brand-logo" :src="logoImage" alt="Menu Manager Pro" />

        <form @submit.prevent="submit" class="login-form">
          <input v-model="email" type="email" placeholder="Email" required />
          <input v-model="password" type="password" placeholder="Senha" required />

          <button type="button" class="text-link">Esqueceu a senha?</button>

          <button class="login-btn" type="submit" :disabled="loading">
            {{ loading ? "Entrando..." : "Entrar" }}
          </button>

          <div v-if="error" class="alert danger">{{ error }}</div>
        </form>

        <button type="button" class="create-account">Criar conta</button>
      </section>

      <aside class="login-right">
        <img :src="loginSideImage" alt="Garçom em restaurante" />
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
  width: min(940px, 100%);
  min-height: 700px;
  background: #fff;
  border: 1px solid #d3d5dd;
  border-radius: 40px;
  padding: 18px;
  display: grid;
  grid-template-columns: 1fr 420px;
  gap: 24px;
}

.login-left {
  padding: 34px 42px 26px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.brand-logo {
  width: 190px;
  margin-top: 56px;
  margin-bottom: 84px;
}

.login-form {
  width: 100%;
  max-width: 390px;
  display: grid;
  gap: 16px;
}

.login-form input {
  height: 58px;
  border: none;
  border-radius: 999px;
  padding: 0 28px;
  background: #ececf4;
  color: #1e232d;
  font-size: 16px;
}

.login-form input::placeholder {
  color: #8e91a8;
}

.login-form input:focus {
  box-shadow: inset 0 0 0 2px #1e232d22;
}

.text-link {
  border: 0;
  background: transparent;
  color: #2048d4;
  font-size: 14px;
  font-style: italic;
  cursor: pointer;
  margin-top: 2px;
}

.login-btn {
  margin-top: 2px;
  height: 58px;
  border-radius: 999px;
  border: 0;
  background: #1e232d;
  color: #fff;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
}

.login-btn:disabled {
  opacity: 0.7;
  cursor: default;
}

.alert {
  margin-top: 4px;
}

.create-account {
  margin-top: auto;
  margin-bottom: 8px;
  border: 0;
  background: transparent;
  color: #111;
  font-size: 17px;
  cursor: pointer;
}

.login-right {
  border-radius: 24px;
  overflow: hidden;
}

.login-right img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

@media (max-width: 980px) {
  .login-shell {
    min-height: auto;
    grid-template-columns: 1fr;
  }

  .login-right {
    min-height: 300px;
    order: -1;
  }

  .brand-logo {
    margin-top: 16px;
    margin-bottom: 38px;
  }

  .create-account {
    margin-top: 28px;
  }
}
</style>
