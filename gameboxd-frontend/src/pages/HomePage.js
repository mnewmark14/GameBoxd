import React, { useEffect, useState } from 'react';
import { fetchGames } from '../services/GamesService';
import InfiniteScroll from 'react-infinite-scroll-component';
import { useNavigate } from 'react-router-dom';
import './HomePage.css';

const HomePage = () => {
  const [games, setGames] = useState([]);
  const [page, setPage] = useState(1); // RAWG API pages start from 1
  const [hasMore, setHasMore] = useState(true);
  const [searchQuery, setSearchQuery] = useState('');
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);

  // Load RAWG API Games
  const loadGames = async () => {
    setLoading(true);
    try {
      const data = await fetchGames(page, searchQuery);
      console.log('Data received from API:', data);

      if (!data || data.length === 0) {
        setHasMore(false);
      } else {
        setGames((prevGames) => [...prevGames, ...data]);
        setPage((prevPage) => prevPage + 1);
      }
    } catch (error) {
      console.error('Error fetching games:', error);
    }
    setLoading(false);
  };

  useEffect(() => {
    setGames([]);
    setPage(1);
    setHasMore(true);
    loadGames();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [searchQuery]);

  // Handle visual appearance, enable infinite scroll
  return (
    <div className="homepage-container">
      {/* Search and Navigation Buttons */}
      <div className="search-profile">
        <input
          type="text"
          placeholder="Search games..."
          value={searchQuery}
          onChange={(e) => setSearchQuery(e.target.value)}
          className="search-input"
        />
        <div className="button-group">
          <button onClick={() => navigate('/profile')} className="profile-button">
            Profile
          </button>
        </div>
      </div>

      {/* Title */}
      <h1 className="game-catalog-title">Game Catalog</h1>

      {/* Infinite Scroll */}
      <InfiniteScroll
        dataLength={games.length}
        next={loadGames}
        hasMore={hasMore}
        loader={<div className="loader">Loading...</div>}
        endMessage={<p className="end-message">No more games to show</p>}
      >
        <div className="game-grid">
          {games.map((game) => (
            <div
              key={game.rawgId}
              className="game-card"
              onClick={() => navigate(`/games/${game.rawgId}`)}
            >
              <img
                src={game.coverImage || game.backgroundImage}
                alt={game.title || game.name}
                className="game-image"
              />
              <p className="game-title">{game.title || game.name}</p>
            </div>
          ))}
        </div>
      </InfiniteScroll>
    </div>
  );
};

export default HomePage;
