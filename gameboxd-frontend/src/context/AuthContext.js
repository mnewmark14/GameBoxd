// src/context/AuthContext.js

import React, { createContext, useState, useEffect } from 'react';
import PropTypes from 'prop-types';
import api from '../services/api';

export const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);

  const login = async (credentials) => {
    try {
      const response = await api.post('/auth/login', credentials);
      localStorage.setItem('authToken', response.data.token);
      setUser(response.data.user);
      // Set the default authorization header for future requests
      api.defaults.headers.common['Authorization'] = `Bearer ${response.data.token}`;
    } catch (error) {
      throw error.response ? error.response.data : error;
    }
  };

  const register = async (userData) => {
    try {
      const response = await api.post('/users/register', userData);
      localStorage.setItem('authToken', response.data.token);
      setUser(response.data.user);
      // Set the default authorization header for future requests
      api.defaults.headers.common['Authorization'] = `Bearer ${response.data.token}`;
    } catch (error) {
      throw error.response ? error.response.data : error;
    }
  };

  const logout = () => {
    localStorage.removeItem('authToken');
    setUser(null);
    delete api.defaults.headers.common['Authorization'];
  };

  // Persist user on refresh
  useEffect(() => {
    const token = localStorage.getItem('authToken');
    if (token) {
      // Set the default authorization header
      api.defaults.headers.common['Authorization'] = `Bearer ${token}`;
      api
        .get('/users/me')
        .then((response) => setUser(response.data))
        .catch(() => {
          localStorage.removeItem('authToken');
          setUser(null);
          delete api.defaults.headers.common['Authorization'];
        });
    }
  }, []);

  return (
    <AuthContext.Provider value={{ user, login, register, logout }}>
      {children}
    </AuthContext.Provider>
  );
};

AuthProvider.propTypes = {
  children: PropTypes.node.isRequired,
};
