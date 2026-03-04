<!-- frontend/src/admin/UserAdmin.vue -->
<script setup>
import { ref, onMounted } from "vue";
import {
  adminUsersList,
  adminUsersListUnits,
  adminCreateUnitUser,
  adminDeleteUser,
  getFriendlyDeleteError
} from "../api";

const units = ref([]);
const users = ref([]);
const email = ref("");
const unitId = ref("");
const error = ref("");
const success = ref("");

async function load() {
  error.value = "";
  units.value = await adminUsersListUnits();
  users.value = await adminUsersList();
}
async function createUser() {
error.value = "";
  email.value = ""; unitId.value = "";
  success.value = "";
  // backend cria senha temporária e retorna? se retornar, mostrar modal — aqui só limpa

  try {
    if (!unitId.value) throw new Error("Selecione uma unidade.");
    if (!email.value?.trim()) throw new Error("Informe o e-mail.");

    const created = await adminCreateUnitUser({
      unitId: Number(unitId.value),
      email: email.value.trim(),
    });

    email.value = "";
    unitId.value = "";

    success.value = created?.temporaryPassword
      ? `Usuário criado ✅ Senha temporária: ${created.temporaryPassword}`
      : "Usuário criado ✅";

    await load();
  } catch (e) {
    error.value = e?.message || String(e);
  }
}
async function removeUser(user) {
  if (!confirm(`Remover usuário "${user.email}" (ID ${user.id})?`)) return;

  error.value = "";
  success.value = "";

  try {
    await adminDeleteUser(user.id);
    success.value = "Usuário removido ✅";
    await load();
  } catch (e) {
    const raw = e?.message || String(e);
    error.value = getFriendlyDeleteError("usuário", raw);

  }
}
onMounted(load);
</script>

<template>
  <div>
    <h2>Usuários (franqueado)</h2>
    <div v-if="error" class="alert danger mt-16">{{ error }}</div>
    <div v-if="success" class="alert success mt-16">{{ success }}</div>

    <div class="card mt-16" style="display:flex;gap:8px;align-items:center;">
      <select v-model="unitId">
        <option value="">Escolha unidade</option>
        <option v-for="u in units" :value="u.id" :key="u.id">{{ u.name }}</option>
      </select>
      <input v-model="email" placeholder="email@franqueado" />
      <button class="btn primary" @click="createUser">Criar usuário de unidade</button>
    </div>

    <div class="card" style="margin-top:12px;">
      <table style="width:100%">
        <thead>
          <tr>
            <th>ID</th><th>E-mail</th><th>Papel</th><th>Unidade</th><th>Status</th><th>Ações</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="u in users" :key="u.id">
            <td>{{ u.id }}</td>
            <td>{{ u.email }}</td>
            <td>{{ u.role }}</td>
            <td>{{ u.unitName || "—" }}</td>
            <td>{{ u.active ? "Ativo" : "Inativo" }}</td>
            <td>
              <button class="btn" @click="removeUser(u)">Excluir</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>