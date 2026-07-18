import axios from 'axios';

const api = axios.create({
    baseURL: 'http://localhost:8080',
    withCredentials: true,
});

export const createActivity = (tripId, dayId, data) =>
    api.post(`/api/trips/${tripId}/days/${dayId}/activities`, data);

export const getActivities = (tripId, dayId) =>
    api.get(`/api/trips/${tripId}/days/${dayId}/activities`);

export const updateActivity = (tripId, dayId, activityId, data) =>
    api.put(`/api/trips/${tripId}/days/${dayId}/activities/${activityId}`, data);

export const deleteActivity = (tripId, dayId, activityId) =>
    api.delete(`/api/trips/${tripId}/days/${dayId}/activities/${activityId}`);