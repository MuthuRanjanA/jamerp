import axios from "axios";

// Determine default base URL based on build mode
const defaultBaseUrl = import.meta.env.PROD
  ? (import.meta.env.VITE_API_URL || import.meta.env.VITE_API_BASE_URL || "https://jamerp.onrender.com")
  : "http://localhost:8080";

// Support both VITE_API_BASE_URL and VITE_API_URL
const apiBaseUrl =
  import.meta.env.VITE_API_BASE_URL ||
  import.meta.env.VITE_API_URL ||
  defaultBaseUrl;

const api = axios.create({
  baseURL: apiBaseUrl,
  timeout: 30000, // 30s timeout to handle Render cold starts gracefully
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");

  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  return config;
});

api.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    const isLoginRequest = error.config?.url?.includes("/auth/login");

    // Only redirect if token is expired or unauthorized (401), NOT on 403 Forbidden
    if (
      !isLoginRequest &&
      error.response &&
      error.response.status === 401
    ) {
      const currentPath = window.location.pathname;
      if (!currentPath.includes("/login")) {
        console.error("Token expired or unauthorized (401), redirecting to login.");
        localStorage.removeItem("token");
        localStorage.removeItem("role");
        localStorage.removeItem("employeeId");
        localStorage.removeItem("employeeName");
        window.location.href = "/login";
      }
    }
    return Promise.reject(error);
  }
);

export default api;
