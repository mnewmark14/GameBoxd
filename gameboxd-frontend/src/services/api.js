// src/services/api.js

import axios from 'axios';

const api = axios.create({
  baseURL: process.env.REACT_APP_API_BASE_URL || 'http://localhost:8080/api/v1',
});

// Optional: Interceptor to handle responses globally
api.interceptors.response.use(
  (response) => response,
  (error) => {
    // Handle error responses
    return Promise.reject(error);
  }
);

export default api;
