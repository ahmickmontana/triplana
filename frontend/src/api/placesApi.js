import axios from 'axios';

const api = axios.create({
    baseURL: 'http://localhost:8080',
    withCredentials: true,
});

export const getAutocompleteSuggestions = (input) => api.get(`/api/places/autocomplete`, { params: { input } });
export const getPlaceDetails = (placeId) => api.get(`/api/places/details`, { params: { placeId } });