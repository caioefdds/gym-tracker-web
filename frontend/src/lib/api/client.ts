import axios, { AxiosError } from 'axios';
import { useAuth } from '@/stores/auth';
import type { ApiError } from '@/types/api';

export const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL ?? '',
  timeout: 15000,
});

api.interceptors.request.use((config) => {
  const token = useAuth.getState().token;
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

api.interceptors.response.use(
  (res) => res,
  (error: AxiosError<ApiError>) => {
    if (error.response?.status === 401 && !isAuthCredentialRequest(error)) {
      useAuth.getState().logout();
    }
    return Promise.reject(error);
  },
);

function isAuthCredentialRequest(error: AxiosError): boolean {
  const url = error.config?.url ?? '';
  return /\/api\/auth\/(login|register|forgot-password|reset-password)(?:\?|$)/.test(url);
}

export function extractApiError(err: unknown): string {
  if (axios.isAxiosError(err)) {
    const data = err.response?.data as (ApiError & { detail?: string; title?: string }) | undefined;
    const fromBody = data?.message ?? data?.detail ?? data?.title;
    if (fromBody && fromBody !== 'Unauthorized') return fromBody;
    if (err.response?.status === 401) return 'E-mail ou senha incorretos';
    return err.message;
  }
  return 'Erro inesperado';
}
