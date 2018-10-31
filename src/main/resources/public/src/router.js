import Vue from 'vue'
import Router from 'vue-router'
import Dashboard from './views/Dashboard'
import DataRequest from './views/DataRequest'

Vue.use(Router)

export default new Router({
  mode: 'history',
  base: process.env.BASE_URL,
  routes: [
    {
      path: '/',
      name: 'dashboard',
      component: Dashboard
    },
    {
      path: '/request',
      name: 'request',
      component: DataRequest
    }
  ]
})