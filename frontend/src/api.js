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

export const fetchTemplates = async () => {
  try {
    const response = await api.get('/api/templates');
    return response.data;
  } catch (error) {
    console.error('Failed to fetch templates:', error);
    throw error;
  }
};

/**
 * API function to enhance profile using AI
 * @param {string} profileText - The profile text to enhance
 * @returns {Promise<{success: boolean, data?: string, error?: string}>}
 */
export const enhanceProfileWithAI = async (profileText) => {
  try {
    if (!profileText || profileText.trim().length === 0) {
      return {
        success: false,
        error: 'Profile text is required'
      };
    }

    const response = await api.post('/api/ai-enhance', {
      profile: profileText
    });

    // Backend returns: { message: "...", data: { enhancedProfile: "..." } }
    const enhancedProfile = response.data?.data?.enhancedProfile || response.data?.enhancedProfile;
    
    if (!enhancedProfile) {
      return {
        success: false,
        error: 'No enhanced profile returned from server'
      };
    }

    return {
      success: true,
      data: enhancedProfile
    };
  } catch (error) {
    console.error('API error:', error);
    return {
      success: false,
      error: error.response?.data?.message || error.message || 'Failed to enhance profile with AI'
    };
  }
};

/**
 * Chatbot API functions
 */

// Generate personalized questions based on user profile
export const generateQuestions = async (userProfile) => {
  try {
    const response = await api.post('/api/generate-questions', {
      userProfile
    });
    return {
      success: true,
      data: response.data?.data || response.data
    };
  } catch (error) {
    console.error('Error generating questions:', error);
    return {
      success: false,
      error: error.response?.data?.message || error.message || 'Failed to generate questions'
    };
  }
};

// Send chat message and get next question
export const sendChatMessage = async (userMessage, conversationState) => {
  try {
    const response = await api.post('/api/chat', {
      userMessage,
      conversationState
    });
    return {
      success: true,
      data: response.data?.data || response.data
    };
  } catch (error) {
    console.error('Error sending chat message:', error);
    return {
      success: false,
      error: error.response?.data?.message || error.message || 'Failed to send message'
    };
  }
};

// Evaluate user interests
export const evaluateInterests = async (userProfile, answers) => {
  try {
    const response = await api.post('/api/evaluate', {
      userProfile,
      answers
    });
    return {
      success: true,
      data: response.data?.data || response.data
    };
  } catch (error) {
    console.error('Error evaluating interests:', error);
    return {
      success: false,
      error: error.response?.data?.message || error.message || 'Failed to evaluate interests'
    };
  }
};

export default api;

