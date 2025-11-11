import { useState } from 'react';
import axios from 'axios';

const API_URL = 'http://localhost:8080';
const TOKEN_KEY = 'authToken';

export const useAdmin = () => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  // Función auxiliar para obtener el token y crear los headers
  const getAuthHeaders = () => {
    const token = localStorage.getItem(TOKEN_KEY);
    return {
      'Content-Type': 'application/json',
      ...(token && { 'Authorization': `Bearer ${token}` })
    };
  };

  // Agregar una nueva categoría
  const addCategory = async (categoryTitle) => {
    setLoading(true);
    setError(null);
    
    try {
      const response = await axios({
        method: 'post',
        url: `${API_URL}/admin/categories`,
        data: { title: categoryTitle },
        headers: getAuthHeaders()
      });
      
      setLoading(false);
      return response.data;
    } catch (err) {
      const errorMsg = err.response?.data?.error || 'Error al agregar categoría';
      setError(errorMsg);
      setLoading(false);
      throw err;
    }
  };

  // Eliminar una categoría
  const deleteCategory = async (categoryTitle) => {
    setLoading(true);
    setError(null);
    
    try {
      const response = await axios({
        method: 'delete',
        url: `${API_URL}/admin/categories/${encodeURIComponent(categoryTitle)}`,
        headers: getAuthHeaders()
      });
      
      setLoading(false);
      return response.data;
    } catch (err) {
      const errorMsg = err.response?.data?.error || 'Error al eliminar categoría';
      setError(errorMsg);
      setLoading(false);
      throw err;
    }
  };

  // Obtener estadísticas del sistema
  const getStats = async () => {
    setLoading(true);
    setError(null);
    
    try {
      const response = await axios({
        method: 'get',
        url: `${API_URL}/admin/stats`,
        headers: getAuthHeaders()
      });
      
      setLoading(false);
      return response.data;
    } catch (err) {
      const errorMsg = err.response?.data?.error || 'Error al obtener estadísticas';
      setError(errorMsg);
      setLoading(false);
      throw err;
    }
  };

  return {
    loading,
    error,
    addCategory,
    deleteCategory,
    getStats
  };
};
