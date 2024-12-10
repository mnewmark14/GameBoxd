import React, { useContext, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { AuthContext } from '../context/AuthContext';
import './RegisterPage.css';

const RegisterPage = () => {
  const { register } = useContext(AuthContext);
  const [userData, setUserData] = useState({
    username: '',
    email: '',
    password: '',
  });
  const navigate = useNavigate();
  const [error, setError] = useState('');

  // Allow user to register account
  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await register(userData);
      navigate('/');
    } catch (error) {
      setError(error.message || 'Registration failed');
    }
  };

  return (
    <div className="register-container">
      <h1 className="register-title">Register</h1>
      <div className="register-card">
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label htmlFor="username">Username</label>
            <input
              type="text"
              id="username"
              placeholder="Your username"
              value={userData.username}
              onChange={(e) =>
                setUserData({ ...userData, username: e.target.value })
              }
              required
            />
          </div>
          <div className="form-group">
            <label htmlFor="email">Email</label>
            <input
              type="email"
              id="email"
              placeholder="Your email"
              value={userData.email}
              onChange={(e) =>
                setUserData({ ...userData, email: e.target.value })
              }
              required
            />
          </div>
          <div className="form-group">
            <label htmlFor="password">Password</label>
            <input
              type="password"
              id="password"
              placeholder="Your password"
              value={userData.password}
              onChange={(e) =>
                setUserData({ ...userData, password: e.target.value })
              }
              required
            />
          </div>
          {error && <div className="error-message">{error}</div>}
          <button type="submit" className="submit-button">
            Register
          </button>
        </form>
      </div>
    </div>
  );
};

export default RegisterPage;
