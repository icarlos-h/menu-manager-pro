// frontend/src/api.js
const API_BASE = "http://localhost:8080"; // se backend estiver em /, deixe vazio; ou "http://localhost:8080"

function getAuthHeaders() {
  const token = localStorage.getItem("auth_token");
  console.log("TOKEN:", token);
  return token ? { Authorization: `Bearer ${token}` } : {};
}

async function checkResponse(r) {
  if (!r.ok) {
    const text = await r.text();
    throw new Error(`${r.status} ${r.statusText} ${text}`);
  }
  return r.status === 204 ? null : r.json();
}

/* Public */
export async function getPublicUnits() {
  const r = await fetch(`${API_BASE}/public/units`);
  return checkResponse(r);
}
export async function getPublicMenu(unitId) {
  const r = await fetch(`${API_BASE}/public/units/${unitId}/menu`);
  return checkResponse(r);
}

/* Auth */
export async function login(email, password) {
  const r = await fetch(`${API_BASE}/auth/login`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ email, password })
  });
  const data = await checkResponse(r);
  if (data?.token) localStorage.setItem("auth_token", data.token);
  return data;
}
export function logout() {
  localStorage.removeItem("auth_token");
}
export function isLoggedIn() {
  return !!localStorage.getItem("auth_token");
}

/* Whoami */
export async function whoami() {
  const r = await fetch(`${API_BASE}/api/whoami`, { headers: getAuthHeaders() });
  return checkResponse(r);
}

/* ADMIN endpoints */
export async function adminListUnits() {
  const r = await fetch(`${API_BASE}/api/admin/units`, { headers: getAuthHeaders() });
  return checkResponse(r);
}
export async function adminCreateUnit(payload) {
  const r = await fetch(`${API_BASE}/api/admin/units`, {
    method: "POST",
    headers: Object.assign({ "Content-Type": "application/json" }, getAuthHeaders()),
    body: JSON.stringify(payload)
  });
  return checkResponse(r);
}

export async function adminListCategories() {
  const r = await fetch(`${API_BASE}/api/admin/categories`, { headers: getAuthHeaders() });
  return checkResponse(r);
}
export async function adminCreateCategory(payload) {
  const r = await fetch(`${API_BASE}/api/admin/categories`, {
    method: "POST",
    headers: Object.assign({ "Content-Type": "application/json" }, getAuthHeaders()),
    body: JSON.stringify(payload)
  });
  return checkResponse(r);
}

export async function adminListProducts() {
  const r = await fetch(`${API_BASE}/api/admin/products`, { headers: getAuthHeaders() });
  return checkResponse(r);
}
export async function adminCreateProduct(payload) {
  const token = localStorage.getItem("auth_token");

  const r = await fetch(`${API_BASE}/api/admin/products`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
    },
    body: JSON.stringify(payload),
  });

  console.log("STATUS:", r.status);
  const text = await r.text();
  console.log("BODY:", text);
  if (!r.ok) throw new Error(`${r.status} ${r.statusText} ${text}`);
  return text ? JSON.parse(text) : null;
  console.log("✅ POST OK");
}

/* Users (create unit user) */
export async function adminCreateUnitUser(payload) {
  const r = await fetch(`${API_BASE}/api/admin/unit`, {
    method: "POST",
    headers: Object.assign({ "Content-Type": "application/json" }, getAuthHeaders()),
    body: JSON.stringify(payload)
  });
  return checkResponse(r);
}

/* Unit (franchise) actions: list unit products, toggle active, update header */
export async function unitListProducts(unitId) {
  const r = await fetch(`${API_BASE}/api/unit/${unitId}/products`, { headers: getAuthHeaders() });
  return checkResponse(r);
}
export async function unitToggleProduct(unitId, productId, payload) {
  const r = await fetch(`${API_BASE}/api/unit/${unitId}/products/${productId}`, {
    method: "PUT",
    headers: Object.assign({ "Content-Type": "application/json" }, getAuthHeaders()),
    body: JSON.stringify(payload)
  });
  return checkResponse(r);
}
export async function unitUpdateHeader(unitId, payload) {
  const r = await fetch(`${API_BASE}/api/unit/${unitId}/header`, {
    method: "PUT",
    headers: Object.assign({ "Content-Type": "application/json" }, getAuthHeaders()),
    body: JSON.stringify(payload)
  });
  return checkResponse(r);
}
/* ADMIN USERS */
export async function adminUsersList() {
  const r = await fetch(`${API_BASE}/api/admin/users`, { headers: getAuthHeaders() });
  return checkResponse(r);
}

// ideal: usa o endpoint /api/admin/users/units (melhor que pegar /api/admin/units)
export async function adminUsersListUnits() {
  const r = await fetch(`${API_BASE}/api/admin/users/units`, { headers: getAuthHeaders() });
  return checkResponse(r);
}
export async function adminUploadProductImage(productId, file) {
  const fd = new FormData();
  fd.append("image", file);

  const r = await fetch(`${API_BASE}/api/admin/products/${productId}/image`, {
    method: "POST",
    headers: { ...getAuthHeaders() }, // NÃO colocar Content-Type aqui
    body: fd,
  });

  return checkResponse(r);
}
export { API_BASE };