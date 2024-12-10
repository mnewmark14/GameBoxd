// src/services/GamesService.js

import api from './api';

export const fetchGames = async (page, searchQuery) => {
  try {
    const response = await fetch(
      `http://localhost:8080/api/v1/games?page=${page}&search=${encodeURIComponent(searchQuery)}`
    );

    if (!response.ok) {
      throw new Error(`Failed to fetch games: ${response.status} ${response.statusText}`);
    }

    const data = await response.json();
    return data;
  } catch (error) {
    console.error('Error fetching games:', error);
    throw error;
  }
};

export const fetchGameDetails = async (rawgGameId) => {
  try {
    const response = await api.get(`/games/rawg/${rawgGameId}`);
    return response.data;
  } catch (error) {
    console.error('Error fetching game details:', error);
    throw error.response ? error.response.data : error;
  }
};

export const submitRating = async (gameId, rating) => {
  try {
    const response = await api.post(`/games/${gameId}/ratings`, { rating });
    return response.data;
  } catch (error) {
    throw error.response ? error.response.data : error;
  }
};

export const updateRating = async (gameId, rating) => {
  try {
    const response = await api.put(`/games/${gameId}/ratings`, { rating });
    return response.data;
  } catch (error) {
    throw error.response ? error.response.data : error;
  }
};

export const submitReview = async (gameId, reviewText) => {
  try {
    const response = await api.post(`/games/${gameId}/reviews`, { reviewText });
    return response.data;
  } catch (error) {
    throw error.response ? error.response.data : error;
  }
};

export const updateReview = async (reviewId, reviewText) => {
  try {
    const response = await api.put(`/reviews/${reviewId}`, { reviewText });
    return response.data;
  } catch (error) {
    console.error('Error updating review:', error);
    throw error.response ? error.response.data : error;
  }
};

export const deleteReview = async (reviewId) => {
  try {
    await api.delete(`/reviews/${reviewId}`);
  } catch (error) {
    console.error('Error deleting review:', error);
    throw error.response ? error.response.data : error;
  }
};


export const fetchRatingsSummary = async (gameId) => {
  try {
    const response = await fetch(`http://localhost:8080/api/v1/games/${gameId}/ratings/summary`);
    if (!response.ok) {
      throw new Error(`Failed to fetch ratings summary: ${response.status}`);
    }
    return await response.json();
  } catch (error) {
    console.error('Error fetching ratings summary:', error);
    throw error;
  }
};

export const logGameStatus = async (gameId, status) => {
  try {
    const response = await api.post(`/games/${gameId}/log?status=${status}`);
    return response.data;
  } catch (error) {
    console.error("Error logging game status:", error);
    throw error.response ? error.response.data : error;
  }
};



export const fetchLoggedStatus = async (gameId) => {
  try {
    const response = await api.get(`/games/${gameId}/log`); // Call the endpoint
    return response.data; // Return the logged status
  } catch (error) {
    console.error("Error fetching logged status:", error);
    throw error.response ? error.response.data : error;
  }
};

