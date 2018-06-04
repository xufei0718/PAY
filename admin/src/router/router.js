import Main from '@/views/Main.vue';

// 不作为Main组件的子页面展示的页面单独写，如下
//isCheck 表示 组件是否需要权限检查
export const loginRouter = {
    path: '/login',
    name: 'login',
    meta: {
        title: '登录'
    },
    component: resolve => { require(['@/views/login.vue'], resolve); }
};

export const page404 = {
    path: '/*',
    name: 'error-404',
    meta: {
        title: '404-页面不存在'
    },
    component: resolve => { require(['@/views/error-page/404.vue'], resolve); }
};

export const page403 = {
    path: '/403',
    meta: {
        title: '403-权限不足'
    },
    name: 'error-403',
    component: resolve => { require(['@//views/error-page/403.vue'], resolve); }
};

export const page500 = {
    path: '/500',
    meta: {
        title: '500-服务端错误'
    },
    name: 'error-500',
    component: resolve => { require(['@/views/error-page/500.vue'], resolve); }
};



export const locking = {
    path: '/locking',
    name: 'locking',
    component: resolve => { require(['@/views/main-components/lockscreen/components/locking-page.vue'], resolve); }
};

// 作为Main组件的子页面展示但是不在左侧菜单显示的路由写在otherRouter里
export const otherRouter = {
    path: '/',
    name: 'otherRouter',
    redirect: '/home',
    isCheck: 'no',
    component: Main,
    children: [
        { path: 'home', title: '控制中心', isCheck: 'no', name: 'home_index', component: resolve => { require(['@/views/home/home.vue'], resolve); } },
        { path: 'ownspace', title: '个人中心', isCheck: 'no', name: 'ownspace_index', component: resolve => { require(['@/views/own-space/own-space.vue'], resolve); } },
        { path: 'merCenter', title: '商户中心', isCheck: 'no', name: 'merCenter', component: resolve => { require(['@/views/own-space/merCenter.vue'], resolve); } },
    ]
};

// 作为Main组件的子页面展示并且在左侧菜单显示的路由写在appRouter里
export const appRouter = [
    {
        path: '/admin',
        icon: 'monitor',
        isCheck: 'yes',
        name: 'admin',
        title: '系统管理',
        component: Main,
        children: [
            { path: 'param', title: '参数管理', isCheck: 'yes', name: 'admin_param', component: resolve => { require(['@/views/admin/param/main.vue'], resolve); } },
            { path: 'user', title: '用户管理', isCheck: 'yes', name: 'admin_user', component: resolve => { require(['@/views/admin/user/main.vue'], resolve); } },
            { path: 'role', title: '角色管理', isCheck: 'yes', name: 'admin_role', component: resolve => { require(['@/views/admin/role/main.vue'], resolve); } },
            { path: 'tax', title: '分类管理', isCheck: 'yes', name: 'admin_tax', component: resolve => { require(['@/views/admin/taxonomy/main.vue'], resolve); } },
            { path: 'art', title: '文章管理', isCheck: 'yes', name: 'admin_art', component: resolve => { require(['@/views/admin/art/main.vue'], resolve); } },
            // { path: 'artList', title: '文章列表', name: 'admin_artList', component: resolve => { require(['@/views/admin/art/list.vue'], resolve); } },
        ]
    },
    {
        path: '/cclear',
        icon: 'calculator',
        isCheck: 'yes',
        name: 'collectionClear',
        title: '清分管理',
        component: Main,
        children: [
            { path: 'cla', title: '清分查询', isCheck: 'yes', name: 'collectionClear_cla', component: resolve => { require(['@/views/collection-clear/clearList_admin.vue'], resolve); } },
            // { path: 'clc', title: '清分审批', isCheck: 'yes', name: 'collectionClear_clc', component: resolve => { require(['@/views/collection-clear/clearList_check.vue'], resolve); } },
            { path: 'clm', title: '商户清分查询', isCheck: 'yes', name: 'collectionClear_clm', component: resolve => { require(['@/views/collection-clear/clearList_mer.vue'], resolve); } },
            { path: 'debit', title: '出账处理', isCheck: 'yes', name: 'collectionClear_debit', component: resolve => { require(['@/views/collection-clear/debit.vue'], resolve); } },
        ]
    },
    {
        path: '/mer',
        icon: 'monitor',
        isCheck: 'yes',
        name: 'mer',
        title: '商户管理',
        component: Main,
        children: [
            { path: 'merinfo', title: '商户信息管理', isCheck: 'yes', name: 'merinfo', component: resolve => { require(['@/views/mer/merinfo/main.vue'], resolve); } },
            { path: 'merquery', title: '商户信息查询', isCheck: 'yes', name: 'merquery', component: resolve => { require(['@/views/mer/merinfo/merquery.vue'], resolve); } },
            { path: 'custquery', title: '商户客户管理', isCheck: 'yes', name: 'custquery', component: resolve => { require(['@/views/mer/custquery/main.vue'], resolve); } },
            { path: 'feequery', title: '商户手续费查询', isCheck: 'yes', name: 'feequery', component: resolve => { require(['@/views/mer/fee/main.vue'], resolve); } },
        ]
    },
    {
        path: '/mer1',
        icon: 'monitor',
        isCheck: 'yes',
        name: 'mer1',
        title: '商户操作管理',
        component: Main,
        children: [
            { path: 'custquery', title: '商户客户管理', isCheck: 'yes', name: 'custquery', component: resolve => { require(['@/views/mer/custquery/main.vue'], resolve); } },
            { path: 'feequery', title: '商户手续费查询', isCheck: 'yes', name: 'feequery', component: resolve => { require(['@/views/mer/fee/main.vue'], resolve); } },
        ]
    },
    {
        path: '/mer2',
        icon: 'monitor',
        isCheck: 'yes',
        name: 'mer2',
        title: '商户用户管理',
        component: Main,
        children: [
            { path: 'meruser', title: '商户用户管理', isCheck: 'yes', name: 'meruser', component: resolve => { require(['@/views/mer/user/main.vue'], resolve); } },

        ]
    },
    {
        path: '/coll',
        icon: 'monitor',
        isCheck: 'no',
        name: 'coll',
        title: '代收交易管理',
        component: Main,
        children: [
            { path: 'entrust', title: '委托管理', isCheck: 'yes', name: 'coll_entrust', component: resolve => { require(['@/views/coll/entrust/main.vue'], resolve); } },
            { path: 'trade', title: '交易管理', isCheck: 'yes', name: 'coll_trade', component: resolve => { require(['@/views/coll/trade/main.vue'], resolve); } },
            { path: 'batch', title: '批量交易管理', isCheck: 'yes', name: 'coll_batch_trade', component: resolve => { require(['@/views/coll/batch/main.vue'], resolve); } }
            // { path: 'reconciliation', title: '对账管理', isCheck: 'yes', name: 'coll_reconciliation', component: resolve => { require(['@/views/coll/reconciliation/main.vue'], resolve); } }
        ]
    }
];

// 所有上面定义的路由都要写在下面的routers里
export const routers = [
    loginRouter,
    otherRouter,
    locking,
    ...appRouter,
    page500,
    page403,
    page404
];
