import { createApp } from 'vue'
import App from './App.vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import router from './router'
import store from './store'

const app = createApp(App);

app.use(ElementPlus);
app.use(router);
app.use(store);
// 屏蔽警告信息
app.config.warnHandler = () => null;

app.mount('#app');
