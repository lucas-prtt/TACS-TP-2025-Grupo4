import { useState, useEffect, useCallback } from 'react';
import axios from 'axios';

const API_URL = 'http://localhost:8080';
const TOKEN_KEY = 'authToken';
const USER_KEY = 'authUser';

export const useGetAuth = () => {
  const [loading, setLoading] = useState(true); // Cambiar a true inicialmente
  const [error, setError] = useState(null);
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [user, setUser] = useState(null);

  // Función para validar el token
  const validateToken = useCallback(async (token) => {
    try {
      // Usar un endpoint que sabemos que existe y está protegido
      const response = await axios({
        method: 'get',
        url: `${API_URL}/registrations`, // Usar endpoint de registraciones para validar
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });
      return response.status === 200;
    } catch (err) {
      // Si es error 403 o 401, el token no es válido
      if (err.response?.status === 401 || err.response?.status === 403) {
        return false;
      }
      // Para otros errores (500, etc), asumimos que el token es válido pero hay problemas del servidor
      return true;
    }
  }, []);

  // Limpiar datos de autenticación
  const clearAuth = useCallback(() => {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
    setUser(null);
    setIsAuthenticated(false);
    delete axios.defaults.headers.common['Authorization'];
  }, []);

  // Verificar autenticación al cargar
  useEffect(() => {
    const checkAuth = () => {
      const storedToken = localStorage.getItem(TOKEN_KEY);
      const storedUser = localStorage.getItem(USER_KEY);
      
      console.log('🔍 Verificando autenticación al cargar:');
      console.log('  - Token presente:', !!storedToken);
      console.log('  - Usuario presente:', !!storedUser);
      
      if (storedToken && storedUser) {
        try {
          const userData = JSON.parse(storedUser);
          console.log('  - Datos de usuario:', userData);
          
          setUser(userData);
          setIsAuthenticated(true);
          
          // Configurar axios con el token
          axios.defaults.headers.common['Authorization'] = `Bearer ${storedToken}`;
          
          console.log('✅ Autenticación restaurada');
        } catch (err) {
          console.error('❌ Error al parsear datos de usuario:', err);
          clearAuth();
        }
      } else {
        console.log('ℹ️ No hay datos de autenticación almacenados');
      }
      
      setLoading(false);
    };

    checkAuth();
  }, [clearAuth]);

  // Interceptor para manejar errores 401 (token expirado)
  useEffect(() => {
    const interceptor = axios.interceptors.response.use(
      (response) => response,
      (error) => {
        if (error.response?.status === 401) {
          // Token expirado o inválido
          clearAuth();
        }
        return Promise.reject(error);
      }
    );

    return () => {
      axios.interceptors.response.eject(interceptor);
    };
  }, [clearAuth]);

  const saveAuthData = (userData, token) => {
    localStorage.setItem(TOKEN_KEY, token);
    localStorage.setItem(USER_KEY, JSON.stringify(userData));
    setUser(userData);
    setIsAuthenticated(true);
    axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
  };

  const register = async (username, password) => {
    setLoading(true);
    setError(null);
    
    try {
      const response = await axios({
        method: 'post',
        url: `${API_URL}/auth/register`,
        data: {
          username: username,
          password: password
        },
        headers: {
          'Content-Type': 'application/json'
        }
      });
      
      setLoading(false);
      return response.data;
    } catch (err) {
      const errorMsg = err.response?.data?.error || 'Error al registrar usuario';
      setError(errorMsg);
      setLoading(false);
      throw err;
    }
  };

  const registerAdmin = async (username, password) => {
    setLoading(true);
    setError(null);
    
    try {
      const response = await axios({
        method: 'post',
        url: `${API_URL}/auth/register-admin`,
        data: {
          username: username,
          password: password
        },
        headers: {
          'Content-Type': 'application/json'
        }
      });
      
      setLoading(false);
      return response.data;
    } catch (err) {
      const errorMsg = err.response?.data?.error || 'Error al registrar administrador';
      setError(errorMsg);
      setLoading(false);
      throw err;
    }
  };

  const login = async (username, password) => {
    setLoading(true);
    setError(null);
    
    try {
      const response = await axios({
        method: 'post',
        url: `${API_URL}/auth/login`,
        data: {
          username: username,
          password: password
        },
        headers: {
          'Content-Type': 'application/json'
        }
      });
      
      const { token, ...userData } = response.data;
      const processedUserData = {
        ...userData,
        id: String(userData.id)
      };
      saveAuthData(processedUserData, token);
      setLoading(false);
      return processedUserData;
    } catch (err) {
      const errorMsg = err.response?.data?.error || 'Error al iniciar sesión';
      setError(errorMsg);
      setLoading(false);
      throw err;
    }
  };

  const logout = () => {
    clearAuth();
  };

  const getToken = () => {
    return localStorage.getItem(TOKEN_KEY);
  };

  const hasRole = (role) => {
    return user && user.roles && user.roles.includes(role);
  };

  const generateOneTimeCode = async () => {
    setLoading(true);
    setError(null);
    
    try {
      const token = getToken();
      const response = await axios({
        method: 'post',
        url: `${API_URL}/auth/oneTimeCode`,
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        }
      });
      
      setLoading(false);
      return response.data;
    } catch (err) {
      const errorMsg = err.response?.data?.error || 'Error al generar código';
      setError(errorMsg);
      setLoading(false);
      throw err;
    }
  };

  const verifyOneTimeCode = async (username, code) => {
    setLoading(true);
    setError(null);
    
    try {
      const response = await axios({
        method: 'get',
        url: `${API_URL}/auth/oneTimeCode`,
        params: { username, code },
        headers: {
          'Content-Type': 'application/json'
        }
      });
      
      const { token, ...userData } = response.data;
      const processedUserData = {
        ...userData,
        id: String(userData.id)
      };
      saveAuthData(processedUserData, token);
      setLoading(false);
      return processedUserData;
    } catch (err) {
      const errorMsg = err.response?.data?.error || 'Código inválido';
      setError(errorMsg);
      setLoading(false);
      throw err;
    }
  };

  return {
    loading,
    error,
    user,
    isAuthenticated,
    register,
    registerAdmin,
    login,
    logout,
    getToken,
    hasRole,
    generateOneTimeCode,
    verifyOneTimeCode
  };
};