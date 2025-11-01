const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';

interface RequestOptions {
  method?: 'GET' | 'POST' | 'PUT' | 'DELETE' | 'PATCH';
  headers?: Record<string, string>;
  body?: unknown;
  credentials?: 'include' | 'omit';
}

interface ApiResponse<T> {
  success: boolean;
  data?: T;
  error?: string;
  message?: string;
}

const getAuthToken = (): string | null => {
  return localStorage.getItem('auth_token');
};

const getHeaders = (customHeaders: Record<string, string> = {}): Record<string, string> => {
  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
    ...customHeaders,
  };

  const token = getAuthToken();
  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }

  return headers;
};

export const apiClient = {
  async request<T>(
    endpoint: string,
    options: RequestOptions = {}
  ): Promise<ApiResponse<T>> {
    const {
      method = 'GET',
      headers: customHeaders = {},
      body,
      credentials = 'include',
    } = options;

    const url = `${API_BASE_URL}${endpoint}`;
    const headers = getHeaders(customHeaders);

    const config: RequestInit = {
      method,
      headers,
      credentials,
    };

    if (body && (method === 'POST' || method === 'PUT' || method === 'PATCH')) {
      config.body = JSON.stringify(body);
    }

    try {
      const response = await fetch(url, config);

      if (!response.ok) {
        if (response.status === 401) {
          localStorage.removeItem('auth_token');
          window.location.href = '/giris.html';
        }

        const errorData = await response.json().catch(() => ({}));
        return {
          success: false,
          error: errorData.message || `Error: ${response.statusText}`,
        };
      }

      const data = await response.json();
      return {
        success: true,
        data: data as T,
      };
    } catch (error) {
      const errorMessage = error instanceof Error ? error.message : 'Unknown error occurred';
      return {
        success: false,
        error: errorMessage,
      };
    }
  },

  get<T>(endpoint: string, headers?: Record<string, string>) {
    return this.request<T>(endpoint, { method: 'GET', headers });
  },

  post<T>(endpoint: string, body: unknown, headers?: Record<string, string>) {
    return this.request<T>(endpoint, { method: 'POST', body, headers });
  },

  put<T>(endpoint: string, body: unknown, headers?: Record<string, string>) {
    return this.request<T>(endpoint, { method: 'PUT', body, headers });
  },

  delete<T>(endpoint: string, headers?: Record<string, string>) {
    return this.request<T>(endpoint, { method: 'DELETE', headers });
  },

  patch<T>(endpoint: string, body: unknown, headers?: Record<string, string>) {
    return this.request<T>(endpoint, { method: 'PATCH', body, headers });
  },
};

export const setAuthToken = (token: string): void => {
  localStorage.setItem('auth_token', token);
};

export const clearAuthToken = (): void => {
  localStorage.removeItem('auth_token');
};

export const isAuthenticated = (): boolean => {
  return !!getAuthToken();
};
