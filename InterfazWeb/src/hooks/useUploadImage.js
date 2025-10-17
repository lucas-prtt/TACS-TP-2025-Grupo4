import { useState } from 'react';
import axios from 'axios';

const API_URL = 'http://localhost:8080';
const TOKEN_KEY = 'authToken';

export const useUploadImage = () => {
  const [uploading, setUploading] = useState(false);
  const [error, setError] = useState(null);

  const getAuthHeaders = () => {
    const token = localStorage.getItem(TOKEN_KEY);
    return {
      ...(token && { 'Authorization': `Bearer ${token}` })
    };
  };

  const uploadImage = async (file) => {
    setUploading(true);
    setError(null);
    
    const formData = new FormData();
    formData.append('image', file);
    
    try {
      const response = await axios({
        method: 'post',
        url: `${API_URL}/images`,
        data: formData,
        headers: {
          ...getAuthHeaders(),
          'Content-Type': 'multipart/form-data'
        }
      });
      
      setUploading(false);
      return response.data; // { imageId: "...", url: "/images/..." }
    } catch (err) {
      const errorMsg = err.response?.data?.error || 'Error al subir imagen';
      setError(errorMsg);
      setUploading(false);
      throw err;
    }
  };

  const getImageUrl = (imageId) => {
    return `${API_URL}/images/${imageId}`;
  };

  return { uploadImage, uploading, error, getImageUrl };
};