// src/App.js

import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import HomePage from './pages/HomePage';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import GameDetailsPage from './pages/GameDetailsPage';
import ProfilePage from './pages/ProfilePage';
import CustomListPage from './pages/CustomListPage'; // Import CustomListPage
import PrivateRoute from './components/privateRoute';
import { AuthProvider } from './context/AuthContext';
import { MantineProvider } from '@mantine/core';
import { Notifications } from '@mantine/notifications';

function App() {
  return (
    <AuthProvider>
      <MantineProvider withGlobalStyles withNormalizeCSS>
        <Notifications position="top-right" />
        <Router>
          <Routes>
            {/* Public Routes */}
            <Route path="/login" element={<LoginPage />} />
            <Route path="/register" element={<RegisterPage />} />

            {/* Protected Routes */}
            <Route
              path="/"
              element={
                <PrivateRoute>
                  <HomePage />
                </PrivateRoute>
              }
            />
            <Route
              path="/profile"
              element={
                <PrivateRoute>
                  <ProfilePage />
                </PrivateRoute>
              }
            />
            <Route path="/custom-lists/select" element={<CustomListPage />} />

            <Route path="/games/:rawgId" element={<GameDetailsPage />} />

            {/* Add additional routes as needed */}
          </Routes>
        </Router>
      </MantineProvider>
    </AuthProvider>
  );
}

export default App;
