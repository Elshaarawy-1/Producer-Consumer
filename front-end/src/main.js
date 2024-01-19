import { createApp } from 'vue';
import App from './App.vue';
import vuetify from './plugins/vuetify';
import VueNativeSock from "vue-native-websocket-vue3";
import { loadFonts } from './plugins/webfontloader';

loadFonts();

const app = createApp(App);

app.use(vuetify);
app.use(VueNativeSock, 'ws://localhost:8081/ws', {
  reconnection: true,
  reconnectionAttempts: 5,
  reconnectionDelay: 3000,
});

app.mount('#app');
