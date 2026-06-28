import axios from 'axios';

const api = axios.create({
    baseURL: 'http://localhost:8080',
    withCredentials: true,
});

export const getTrips = () => api.get('/api/trips/')
export const createTrip = (data) => api.post('/api/trips/', data);
export const uploadCoverImage = (tripId, formData) => 
    api.post(`/api/trips/${tripId}/cover-image`, formData, {
        headers: { 'Content-Type': 'multipart/form-data' }
    });
export const getTrip = (tripId) => api.get(`/api/trips/${tripId}`);