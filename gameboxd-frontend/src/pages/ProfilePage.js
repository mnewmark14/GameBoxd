import React, { useEffect, useState, useContext } from 'react';
import { fetchUserProfile, fetchUserStatistics, fetchReviewedAndRatedGames, fetchUserCustomLists } from '../services/UserService';
import { AuthContext } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';

import './ProfilePage.css';

const ProfilePage = () => {
  const { logout } = useContext(AuthContext);
  const navigate = useNavigate();
  const [user, setUser] = useState(null);
  const [statistics, setStatistics] = useState(null);
  const [reviewedGames, setReviewedGames] = useState([]);
  const [customLists, setCustomLists] = useState([]); // State for custom lists
  const [error, setError] = useState('');



  useEffect(() => {
    const getProfileData = async () => {
      try {
        // Fetch all required data
        const userData = await fetchUserProfile();
        const statsData = await fetchUserStatistics();
        const games = await fetchReviewedAndRatedGames();
        const lists = await fetchUserCustomLists(); // Fetch custom lists


        // Set data in state
        setUser(userData);
        setStatistics(statsData);
        setReviewedGames(games);
        setCustomLists(lists); // Set custom lists data
      } catch (error) {
        setError('Failed to load profile data.');
      }
    };

    getProfileData();
  }, []);

  // Allow user to log out
  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  // Allow user to return to home screen
  const handleBackToHome = () => {
    navigate('/');
  };

  if (error)
    return (
      <div className="error-container">
        <p className="error-message">{error}</p>
      </div>
    );

  if (!user || !statistics || reviewedGames.length === 0)
    return (
      <div className="loader-container">
        <p>Loading...</p>
      </div>
    );

  return (
    <div className="profile-container">
      <div className="profile-header">
        <img
          src={user.avatarUrl}
          alt={user.username}
          className="profile-avatar"
        />
        <div className="profile-details">
          <h2 className="profile-username">{user.username}</h2>
          <p className="profile-bio">{user.bio}</p>
          <p className="profile-stats">
            Followers: {user.followersCount} | Following: {user.followingCount}
          </p>
          <p className="profile-stats">
            Total Games Logged: {statistics.totalGamesLogged}
          </p>
        </div>
      </div>

      <div className="profile-buttons">
        <button className="nav-button" onClick={handleBackToHome}>
          Back to Home
        </button>
        <button className="logout-button" onClick={handleLogout}>
          Logout
        </button>
      </div>

      <hr className="divider" />

      <h3 className="reviewed-games-title">Your Reviewed and Rated Games</h3>
      {reviewedGames.length > 0 ? (
        <ul className="reviewed-games-list">
          {reviewedGames.map((game) => (
            <li key={game.id} className="reviewed-game-item">
              {game.title}
            </li>
          ))}
        </ul>
      ) : (
        <p className="no-reviews-message">
          You haven't reviewed or rated any games yet.
        </p>
      )}

      <hr className="divider" />

      <h3 className="reviewed-games-title">Rating Distribution</h3>
      {statistics.ratingDistribution && Object.keys(statistics.ratingDistribution).length > 0 ? (
        <ul className="rating-distribution-list">
          {Object.entries(statistics.ratingDistribution).map(([rating, count]) => (
            <li key={rating} className="rating-distribution-item">
              {rating} Stars: {count}
            </li>
          ))}
        </ul>
      ) : (
        <p className="no-reviews-message">
          No ratings given yet.
        </p>
      )}

    <hr className="divider" />

    <h3 className="custom-lists-title">Your Custom Lists</h3>
    {customLists.length > 0 ? (
      <div className="custom-lists-container">
        {customLists.map((list) => (
          <div key={list.id} className="custom-list">
            <h4 className="custom-list-name">{list.name}</h4>
            {list.games && list.games.length > 0 ? (
              <ul className="custom-list-games">
                {list.games.map((game) => (
                  <li key={game.id} className="custom-list-game-item">
                    {game.title}
                  </li>
                ))}
              </ul>
            ) : (
              <p className="no-games-message">No games in this list.</p>
            )}
          </div>
        ))}
      </div>
    ) : (
      <p className="no-custom-lists-message">You haven't created any custom lists yet.</p>
    )}

    </div>
  );
};

export default ProfilePage;
