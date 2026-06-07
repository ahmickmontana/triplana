import axios from 'axios';

const api = axios.create({
    baseURL: 'http://localhost:8080',
    withCredentials: true,
});

export const register = (data) => api.post('/api/auth/register', data);
export const login = (data) => api.post('/api/auth/login', data);
export const logout = () => api.post('/api/auth/logout');
export const verifyEmail = (token) => api.get(`/api/auth/verify?token=${token}`);
export const resendVerification = (data) => api.post('/api/auth/resend-verification', data);
export const forgotPassword = (data) => api.post('/api/auth/forgot-password', data);
export const resetPassword = (data) => api.post('/api/auth/reset-password', data);
export const getMe = () => api.get('/api/auth/me');