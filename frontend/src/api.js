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

// API function to submit profile with template type
export const submitProfile = async (profileData, templateType = 'professional') => {
  try {
    const dataWithTemplate = {
      ...profileData,
      templateType: templateType
    };
    
    const response = await api.post('/api/profiles', dataWithTemplate);
    console.log('API response:', response);
    console.log('API response data:', response.data);
    
    // Backend returns: { message: "...", data: { profile: {...}, templateText: "..." } }
    // Extract the actual profile data from the nested structure
    const actualData = response.data.data || response.data;
    
    return { success: true, data: actualData };
  } catch (error) {
    console.error('API error:', error);
    return {
      success: false,
      error: error.response?.data?.message || error.message || 'Failed to submit profile',
    };
  }
};

export default api;

