import api from './api';

export const fetchUserProfile = async () => {
  try {
    const response = await api.get('/users/me');
    return response.data;
  } catch (error) {
    throw error.response.data;
  }
};

export const fetchReviewedAndRatedGames = async () => {
  try {
    const response = await api.get('/users/me/reviewed-rated-games');
    return response.data;
  } catch (error) {
    throw error.response.data;
  }
};

export const fetchUserStatistics = async () => {
  try {
    const response = await api.get('/users/me/statistics');
    return response.data;
  } catch (error) {
    throw error.response.data;
  }
};

export const fetchUserCustomLists = async () => {
  try {
    const response = await api.get('/users/me/custom-lists');
    return response.data;
  } catch (error) {
    throw error.response.data;
  }
};
