import axios from 'axios';

const api = axios.create({
    baseURL: 'http://localhost:8080',
    withCredentials: true,
});

export const getTrips = () => api.get('/api/trips/')
export const createTrip = (data) => api.post('/api/trips/', data);