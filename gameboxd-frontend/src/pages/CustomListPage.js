import React, { useState, useEffect } from 'react';
import { useLocation } from 'react-router-dom';
import { fetchCustomLists, addGameToList } from '../services/CustomListService';
import './CustomListPage.css';

const CustomListPage = () => {
  const location = useLocation();
  const [customLists, setCustomLists] = useState([]);
  const [error, setError] = useState('');
  const gameId = new URLSearchParams(location.search).get('gameId'); // Get gameId from query string

  // Fetch custom lists on component mount
  useEffect(() => {
    const loadLists = async () => {
      try {
        const lists = await fetchCustomLists();
        setCustomLists(lists);
      } catch (err) {
        setError('Failed to fetch custom lists.');
      }
    };

    loadLists();
  }, []);

  // Handle adding a game to a list
  const handleAddGameToList = async (listId) => {
    try {
      await addGameToList(listId, gameId);
      alert('Game successfully added to the list!');
    } catch (err) {
      console.error('Error adding game to list:', err);
      setError('Failed to add game to the list.');
    }
  };

  // CustomList HTML
  return (
    <div className="custom-list-page">
      <h1>Select a List to Add Game</h1>
      {error && <p className="error-message">{error}</p>}

      {customLists.length === 0 ? (
        <p>No custom lists found.</p>
      ) : (
        <ul className="custom-list">
          {customLists.map((list) => (
            <li key={list.id}>
              {list.name}
              <button onClick={() => handleAddGameToList(list.id)} className="add-game-button">
                Add to List
              </button>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
};

export default CustomListPage;
