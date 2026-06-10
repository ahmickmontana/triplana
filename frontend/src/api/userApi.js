import axios from 'axios';

const api = axios.create({
    baseURL: 'http://localhost:8080',
    withCredentials: true,
});

export const updateProfile = (data) => api.put('/api/users/profile', data);