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

const creating = ref(false);
const showPasswordModal = ref(false);
const tempPassword = ref("");
const tempEmail = ref("");
const copied = ref(false);

async function load() {
  error.value = "";
  units.value = await adminUsersListUnits();
  users.value = await adminUsersList();
}

function openCreate() {
  creating.value = true;
  email.value = "";
  unitId.value = "";
}

function closeCreate() {
  creating.value = false;
}
function closePasswordModal() {
  showPasswordModal.value = false;
  tempPassword.value = "";
  tempEmail.value = "";
  copied.value = false;
}

async function copyTempPassword() {
  if (!tempPassword.value) return;

  try {
    await navigator.clipboard.writeText(tempPassword.value);
    copied.value = true;
  } catch {
    copied.value = false;
    error.value = "Não foi possível copiar automaticamente. Copie a senha manualmente.";
  }
}

async function createUser() {
  error.value = "";
  success.value = "";

  try {
    const selectedUnitId = Number(unitId.value);
    const trimmedEmail = email.value?.trim();

    if (!Number.isInteger(selectedUnitId) || selectedUnitId <= 0) {
      throw new Error("Selecione uma unidade.");
    }
    if (!trimmedEmail) throw new Error("Informe o e-mail.");

    const created = await adminCreateUnitUser({
      unitId: selectedUnitId,
      email: trimmedEmail,
    });

    email.value = "";
    unitId.value = "";
    closeCreate();

    if (created?.temporaryPassword) {
      tempPassword.value = created.temporaryPassword;
      tempEmail.value = created.email || trimmedEmail;
      copied.value = false;
      showPasswordModal.value = true;
      success.value = "Usuário criado ✅";
    } else {
      success.value = "Usuário criado ✅";
    }

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
    <div class="page-head">
      <h2>Usuários (franqueado)</h2>
      <div class="head-actions">
        <button class="btn primary" @click="openCreate">+ Criar usuário</button>
      </div>
    </div>

    <div v-if="error" class="alert danger mt-16">{{ error }}</div>
    <div v-if="success" class="alert success mt-16">{{ success }}</div>

    <div class="card mt-16" style="margin-top:12px;">
      <table style="width:100%">
        <thead>
          <tr>
            <th>ID</th>
            <th>E-mail</th>
            <th>Papel</th>
            <th>Unidade</th>
            <th>Status</th>
            <th>Ações</th>
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

    <div v-if="creating" class="modal-backdrop" @click.self="closeCreate">
      <div class="modal">
        <div class="modal-head">
          <div>
            <div class="modal-title">Criar usuário de unidade</div>
            <div class="subtle">Selecione a unidade e informe o e-mail.</div>
          </div>
          <button class="btn ghost" @click="closeCreate">✕</button>
        </div>

        <form class="modal-body" @submit.prevent="createUser">
          <select v-model="unitId">
            <option value="">Escolha unidade</option>
            <option v-for="u in units" :value="u.id" :key="u.id">{{ u.name }}</option>
          </select>
          <input v-model="email" placeholder="email@franqueado" />
        </form>

        <div class="modal-foot">
          <button class="btn" @click="closeCreate">Cancelar</button>
          <button class="btn primary" @click="createUser">Criar usuário</button>
        </div>
      </div>
    </div>

    <div v-if="showPasswordModal" class="modal-backdrop" @click.self="closePasswordModal">
      <div class="modal">
        <div class="modal-head">
          <div>
            <div class="modal-title">Usuário criado com sucesso ✅</div>
            <div class="subtle">Guarde esta senha temporária antes de fechar.</div>
          </div>
          <button class="btn ghost" @click="closePasswordModal">✕</button>
        </div>

        <div class="modal-body">
          <div class="alert danger" style="margin-bottom: 10px;">
            ⚠️ Essa senha aparece apenas agora. Anote ou copie imediatamente.
          </div>

          <div class="subtle" style="margin-bottom: 6px;">E-mail</div>
          <div class="card" style="margin-bottom: 10px; font-weight: 600;">{{ tempEmail }}</div>

          <div class="subtle" style="margin-bottom: 6px;">Senha temporária</div>
          <div class="card"
            style="font-size: 1.25rem; letter-spacing: 0.08em; font-weight: 800; background: #fff8e1; border: 1px solid #f59e0b; color: #7c2d12;">
            {{ tempPassword }}
          </div>
        </div>

        <div class="modal-foot">
          <div v-if="copied" class="alert success" style="flex:1; margin-right: 8px;">
            Senha copiada para a área de transferência ✅
          </div>
          <button class="btn" @click="copyTempPassword">Copiar senha</button>
          <button class="btn primary" @click="closePasswordModal">Fechar</button>
        </div>
      </div>
    </div>
  </div>
</template>