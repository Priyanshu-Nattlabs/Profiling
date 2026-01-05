import React, { createContext, useContext, useState, useEffect } from 'react';
import api from '../api';

const AuthContext = createContext(null);

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(localStorage.getItem('token'));
  const [loading, setLoading] = useState(true);

  // Set token in axios headers and restore session on mount
  useEffect(() => {
    if (token) {
      api.defaults.headers.common['Authorization'] = `Bearer ${token}`;
      // Try to restore user session
      restoreSession();
    } else {
      delete api.defaults.headers.common['Authorization'];
      setLoading(false);
    }
  }, []);

  const restoreSession = async () => {
    const savedToken = localStorage.getItem('token');
    if (!savedToken) {
      setLoading(false);
      return;
    }

    try {
      api.defaults.headers.common['Authorization'] = `Bearer ${savedToken}`;
      const response = await api.get('/api/auth/me');
      if (response.data && response.data.data) {
        setUser(response.data.data);
        setToken(savedToken);
      }
    } catch (error) {
      // Token invalid or expired
      logout();
    } finally {
      setLoading(false);
    }
  };

  const login = async (newToken, userData) => {
    try {
      setToken(newToken);
      localStorage.setItem('token', newToken);
      api.defaults.headers.common['Authorization'] = `Bearer ${newToken}`;
      
      // If userData is provided, use it; otherwise fetch from API
      if (userData) {
        setUser(userData);
      } else {
        try {
          const response = await api.get('/api/auth/me');
          if (response.data && response.data.data) {
            setUser(response.data.data);
          }
        } catch (error) {
          console.error('Failed to fetch user data:', error);
          // If fetching user data fails, still set token but user will be null
          // This allows the token to be used for subsequent requests
        }
      }
    } catch (error) {
      console.error('Login error:', error);
      throw error;
    }
  };

  const logout = () => {
    setToken(null);
    setUser(null);
    localStorage.removeItem('token');
    delete api.defaults.headers.common['Authorization'];
  };

  const isAuthenticated = () => {
    return !!token && !!user;
  };

  return (
    <AuthContext.Provider value={{ user, token, login, logout, isAuthenticated, loading }}>
      {children}
    </AuthContext.Provider>
  );
};

