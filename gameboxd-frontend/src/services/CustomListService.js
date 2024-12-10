// src/services/CustomListService.js
import api from './api';

// Fetch all custom lists for the current user
export const fetchCustomLists = async () => {
  try {
    const response = await api.get('/custom-lists');
    return response.data;
  } catch (error) {
    console.error('Error fetching custom lists:', error);
    throw error.response ? error.response.data : error;
  }
};

// Create a new custom list
export const createCustomList = async (listData) => {
  try {
    const response = await api.post('/custom-lists', listData);
    return response.data;
  } catch (error) {
    console.error('Error creating custom list:', error);
    throw error.response ? error.response.data : error;
  }
};

export const addGameToList = async (listId, gameId) => {
  try {
    const response = await api.post(`/custom-lists/${listId}/games/${gameId}`);
    return response.data;
  } catch (error) {
    console.error('Error adding game to custom list:', error);
    throw error;
  }
};

// Remove a game from a custom list
export const removeGameFromList = async (listId, gameId) => {
  try {
    await api.delete(`/custom-lists/${listId}/games/${gameId}`);
  } catch (error) {
    console.error('Error removing game from list:', error);
    throw error.response ? error.response.data : error;
  }
};

// Delete a custom list
export const deleteCustomList = async (listId) => {
  try {
    await api.delete(`/custom-lists/${listId}`);
  } catch (error) {
    console.error('Error deleting custom list:', error);
    throw error.response ? error.response.data : error;
  }
};
