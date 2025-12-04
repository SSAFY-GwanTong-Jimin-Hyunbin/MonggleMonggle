import { createRouter, createWebHistory } from 'vue-router';

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      component: () => import('../layouts/MainLayout.vue'),
      children: [
        { path: '', redirect: { name: 'auth' } },
        {
          path: 'auth',
          name: 'auth',
          component: () => import('../views/AuthView.vue'),
          meta: { hideHeader: true }
        },
        {
          path: 'calendar',
          name: 'calendar',
          component: () => import('../views/CalendarView.vue')
        },
        {
          path: 'write',
          name: 'write',
          component: () => import('../views/WriteView.vue')
        },
        {
          path: 'mypage',
          name: 'mypage',
          component: () => import('../views/MyPageView.vue')
        },
        {
          path: 'loading',
          name: 'loading',
          component: () => import('../views/LoadingView.vue')
        },
        {
          path: 'analysis',
          name: 'analysis',
          component: () => import('../views/DreamAnalysisView.vue')
        },
        {
          path: 'visualization',
          name: 'visualization',
          component: () => import('../views/DreamVisualizationView.vue')
        },
        {
          path: 'monthly-analysis',
          name: 'monthly-analysis',
          component: () => import('../views/MonthlyAnalysisView.vue')
        },
        {
          path: 'gallery',
          name: 'gallery',
          component: () => import('../views/ImageGalleryView.vue')
        },
        { path: ':pathMatch(.*)*', redirect: { name: 'auth' } }
      ]
    }
  ],
  scrollBehavior: () => ({ top: 0 })
});

export default router;

