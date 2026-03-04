// frontend/src/router.js
import { createRouter, createWebHistory } from "vue-router";
import PublicUnitsView from "./PublicUnitsView.vue";
import PublicMenuView from "./PublicMenuView.vue";
import LoginView from "./LoginView.vue";
import AdminView from "./admin/AdminView.vue";
import UnitView from "./UnitView.vue";
import { isLoggedIn } from "./api";

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: "/", component: PublicUnitsView },
    { path: "/u/:unitId", component: PublicMenuView },

    { path: "/login", component: LoginView },

    { path: "/admin", component: AdminView, meta: { requiresAuth: true } },
    { path: "/unit", component: UnitView, meta: { requiresAuth: true } },
  ],
});

router.beforeEach((to) => {
  if (to.meta?.requiresAuth && !isLoggedIn()) return "/login";
});

export default router;