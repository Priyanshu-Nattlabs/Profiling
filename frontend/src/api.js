import axios from 'axios';

// Get API URL from environment variable or use default
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

// Create axios instance with base configuration
const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// API function to submit profile
export const submitProfile = async (profileData) => {
  try {
    const response = await api.post('/api/profiles', profileData);
    return { success: true, data: response.data };
  } catch (error) {
    return {
      success: false,
      error: error.response?.data?.message || error.message || 'Failed to submit profile',
    };
  }
};

export default api;

