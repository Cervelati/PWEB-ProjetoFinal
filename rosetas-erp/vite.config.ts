import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

// O proxy evita CORS em desenvolvimento: o ERP chama /api e o Vite repassa ao backend.
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5174,
    proxy: {
      '/api': process.env.VITE_API_TARGET ?? 'http://localhost:8080',
    },
  },
});
