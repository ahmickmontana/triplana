import axios from 'axios';

const api = axios.create({
    baseURL: 'http://localhost:8080',
    withCredentials: true,
});

export const computeRoute = (data) => api.post('/api/routes/compute', data);