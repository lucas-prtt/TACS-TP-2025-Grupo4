import { useState, useCallback } from 'react';
import axios from 'axios';

const API_URL = `${window.location.protocol}//${window.location.hostname}:8080`;
const TOKEN_KEY = 'authToken';

export const useGetEvents = () => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [events, setEvents] = useState([]);
  const [event, setEvent] = useState(null);

  // Función auxiliar para obtener el token y crear los headers
  const getAuthHeaders = () => {
    const token = localStorage.getItem(TOKEN_KEY);
    return {
      'Content-Type': 'application/json',
      ...(token && { 'Authorization': `Bearer ${token}` })
    };
  };

  // Crear un nuevo evento
  const createEvent = async (eventData) => {
    setLoading(true);
    setError(null);
    
    try {
      const response = await axios({
        method: 'post',
        url: `${API_URL}/events`,
        data: eventData,
        headers: getAuthHeaders()
      });
      
      setLoading(false);
      return response.data;
    } catch (err) {
      const errorMsg = err.response?.data?.error || err.response?.data?.message || 'Error al crear el evento';
      setError(errorMsg);
      setLoading(false);
      throw err;
    }
  };

  // Obtener todos los eventos con filtros opcionales
  const getEvents = useCallback(async ({
    title,
    titleContains,
    maxDate,
    minDate,
    category,
    tags,
    maxPrice,
    minPrice,
    page,
    limit
  } = {}) => {
    setLoading(true);
    setError(null);
    
    try {
      const params = {};
      if (title) params.title = title;
      if (titleContains) params.titleContains = titleContains;
      if (maxDate) params.maxDate = maxDate;
      if (minDate) params.minDate = minDate;
      if (category) params.category = category;
      if (tags) params.tags = tags;
      if (maxPrice) params.maxPrice = maxPrice;
      if (minPrice) params.minPrice = minPrice;
      if (page) params.page = page;
      if (limit) params.limit = limit;

      const response = await axios({
        method: 'get',
        url: `${API_URL}/events`,
        params: params,
        headers: getAuthHeaders()
      });
      
      setEvents(response.data);
      setLoading(false);
      return response.data;
    } catch (err) {
      const errorMsg = err.response?.data?.error || 'Error al obtener eventos';
      setError(errorMsg);
      setLoading(false);
      throw err;
    }
  }, []);

  // Obtener un evento específico por ID
  const getEventById = useCallback(async (id) => {
    if (!id) {
      setError('ID de evento no proporcionado');
      return;
    }
    
    setLoading(true);
    setError(null);
    
    try {
      const response = await axios({
        method: 'get',
        url: `${API_URL}/events/${id}`,
        headers: getAuthHeaders()
      });
      
      setEvent(response.data);
      setLoading(false);
      return response.data;
    } catch (err) {
      const errorMsg = err.response?.data?.error || 'Error al obtener el evento';
      setError(errorMsg);
      setLoading(false);
      throw err;
    }
  }, []);

  // Obtener eventos organizados por el usuario autenticado
  const getOrganizedEvents = useCallback(async (page, limit) => {
    setLoading(true);
    setError(null);
    
    try {
      const params = {};
      if (page) params.page = page;
      if (limit) params.limit = limit;

      const response = await axios({
        method: 'get',
        url: `${API_URL}/events/organized-events`,
        params: params,
        headers: getAuthHeaders()
      });
      
      setEvents(response.data);
      setLoading(false);
      return response.data;
    } catch (err) {
      const errorMsg = err.response?.data?.error || 'Error al obtener eventos organizados';
      setError(errorMsg);
      setLoading(false);
      throw err;
    }
  }, []);

  // Registrar usuario en un evento
  const registerToEvent = async (eventId) => {
    setLoading(true);
    setError(null);
    
    try {
      const response = await axios({
        method: 'post',
        url: `${API_URL}/events/${eventId}/registrations`,
        headers: getAuthHeaders()
      });
      
      setLoading(false);
      return response.data;
    } catch (err) {
      const errorMsg = err.response?.data?.error || 'Error al registrarse en el evento';
      setError(errorMsg);
      setLoading(false);
      throw err;
    }
  };

  // Actualizar un evento (PATCH)
  const updateEvent = async (eventId, eventData) => {
    setLoading(true);
    setError(null);
    
    try {
      const response = await axios({
        method: 'patch',
        url: `${API_URL}/events/${eventId}`,
        data: eventData,
        headers: getAuthHeaders()
      });
      
      setLoading(false);
      return response.data;
    } catch (err) {
      const errorMsg = err.response?.data?.error || 'Error al actualizar el evento';
      setError(errorMsg);
      setLoading(false);
      throw err;
    }
  };

  // Obtener participantes de un evento
  const getEventParticipants = async (eventId, { page, limit, registrationState } = {}) => {
    setLoading(true);
    setError(null);
    
    try {
      const params = {};
      if (page) params.page = page;
      if (limit) params.limit = limit;
      if (registrationState) params.registrationState = registrationState;

      const response = await axios({
        method: 'get',
        url: `${API_URL}/events/${eventId}/registrations`,
        params: params,
        headers: getAuthHeaders()
      });
      
      setLoading(false);
      return response.data;
    } catch (err) {
      const errorMsg = err.response?.data?.error || 'Error al obtener participantes';
      setError(errorMsg);
      setLoading(false);
      throw err;
    }
  };

  // Obtener inscripciones del usuario autenticado
  const getUserRegistrations = useCallback(async ({ page, limit, registrationState } = {}) => {
    setLoading(true);
    setError(null);
    
    try {
      const params = {};
      if (page) params.page = page;
      if (limit) params.limit = limit;
      if (registrationState) params.registrationState = registrationState;

      const response = await axios({
        method: 'get',
        url: `${API_URL}/registrations`,
        params: params,
        headers: getAuthHeaders()
      });
      
      setLoading(false);
      return response.data;
    } catch (err) {
      const errorMsg = err.response?.data?.error || 'Error al obtener inscripciones';
      setError(errorMsg);
      setLoading(false);
      throw err;
    }
  }, []);

  // Cancelar inscripción del usuario autenticado
  const cancelRegistration = useCallback(async (registrationId) => {
    setLoading(true);
    setError(null);
    
    try {
      const response = await axios({
        method: 'patch',
        url: `${API_URL}/registrations/${registrationId}`,
        data: {
          state: 'CANCELED'
        },
        headers: getAuthHeaders()
      });
      
      setLoading(false);
      return response.data;
    } catch (err) {
      const errorMsg = err.response?.data?.error || 'Error al cancelar inscripción';
      setError(errorMsg);
      setLoading(false);
      throw err;
    }
  }, []);

  // Obtener categorías disponibles
  const getCategories = useCallback(async ({ page = 0, limit = 100, startsWith } = {}) => {
    setLoading(true);
    setError(null);
    
    try {
      const params = { page, limit };
      if (startsWith) params.startsWith = startsWith;

      const response = await axios({
        method: 'get',
        url: `${API_URL}/events/categories`,
        params: params,
        headers: getAuthHeaders()
      });

      setLoading(false);
      return response.data;
    } catch (err) {
      const errorMsg = err.response?.data?.error || 'Error al obtener categorías';
      setError(errorMsg);
      setLoading(false);
      throw err;
    }
  }, []);

  return {
    loading,
    error,
    events,
    event,
    getEvents,
    createEvent,
    getEventById,
    getOrganizedEvents,
    registerToEvent,
    updateEvent,
    getEventParticipants,
    getUserRegistrations,
    cancelRegistration,
    getCategories
  };
};