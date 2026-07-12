import axios from 'axios';

const api = axios.create({
    baseURL: 'http://localhost:8080',
    withCredentials: true,
});

export const createActivity = (tripId, dayId, data) =>
    api.post(`/api/trips/${tripId}/days/${dayId}/activities`, data);

export const getActivities = (tripId, dayId) =>
    api.get(`/api/trips/${tripId}/days/${dayId}/activities`);