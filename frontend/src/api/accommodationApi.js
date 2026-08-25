import axios from 'axios';

const api = axios.create({
    baseURL: 'http://localhost:8080',
    withCredentials: true,
});

export const createAccommodation = (tripId, data) => api.post(`/api/trips/${tripId}/accommodations`, data);
export const getAccommodations = (tripId) => api.get(`/api/trips/${tripId}/accommodations`);
export const updateAccommodation = (tripId, accommodationId, data) => api.put(`/api/trips/${tripId}/accommodations/${accommodationId}`, data);
export const deleteAccommodation = (tripId, accommodationId) => api.delete(`/api/trips/${tripId}/accommodations/${accommodationId}`);