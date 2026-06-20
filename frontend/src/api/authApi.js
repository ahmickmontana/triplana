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
export const initiateChangeEmail = (data) => api.post('/api/auth/change-email/initiate', data);
export const submitChangeEmail = (data) => api.post('/api/auth/change-email/submit', data);
export const confirmChangeEmail = (token) => api.get('/api/auth/change-email/confirm', { params: { token } });
export const verifyResetToken = (token) => api.get('/api/auth/verify-reset-token', { params: { token } });
export const verifyChangeEmailToken = (token) => api.get('/api/auth/verify-change-email-token', { params: { token } });
export const resendChangeEmailConfirmation = (token) => api.post('/api/auth/resend-change-email-confirmation', null, { params: { token } });
export const verifyConfirmEmailToken = (token) => api.get('/api/auth/verify-change-email-token', { params: { token } });
export const getMe = () => api.get('/api/auth/me');