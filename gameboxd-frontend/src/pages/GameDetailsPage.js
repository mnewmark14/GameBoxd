import React, { useEffect, useState } from "react";
import {
  fetchGameDetails,
  submitRating,
  submitReview,
  fetchRatingsSummary,
  deleteReview,
  updateReview,
} from "../services/GamesService";
import { fetchUserProfile } from "../services/UserService"; // Import fetchUserProfile
import { useParams, useNavigate } from "react-router-dom";
import { logGameStatus, fetchLoggedStatus } from "../services/GamesService"; // Correctly import logGameStatus
import "./GameDetails.css";

const GameDetailsPage = () => {
  const { rawgId } = useParams();
  const navigate = useNavigate();

  const [game, setGame] = useState(null);
  const [reviews, setReviews] = useState([]);
  const [averageRating, setAverageRating] = useState(0);
  const [totalRatings, setTotalRatings] = useState(0);
  const [loading, setLoading] = useState(true);
  const [ratingValue, setRatingValue] = useState(null);
  const [reviewText, setReviewText] = useState("");
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState(null);
  const [editingReviewId, setEditingReviewId] = useState(null);
  const [updatedReviewText, setUpdatedReviewText] = useState("");
  const [currentUser, setCurrentUser] = useState(null); // State for current user
  const [gameStatus, setGameStatus] = useState(""); // Track game status


  // Fetch game details
  useEffect(() => {
    const getGameDetails = async () => {
      try {
        const data = await fetchGameDetails(rawgId);
        setGame(data);

        const ratingsSummary = await fetchRatingsSummary(data.id);
        setAverageRating(ratingsSummary.averageRating ?? 0);
        setTotalRatings(ratingsSummary.totalRatings ?? 0);

        setReviews(data.reviews ?? []);
        setRatingValue(data.userRating);
        setGameStatus(data.userStatus); // Initialize game status
      } catch (error) {
        console.error("Failed to load game details:", error);
        setError("Failed to load game details.");
      }
      setLoading(false);
    };

    getGameDetails();
  }, [rawgId]);

  // Fetch current user data
  useEffect(() => {
    const loadCurrentUser = async () => {
      try {
        const userData = await fetchUserProfile();
        setCurrentUser(userData);
        console.log("Current user fetched:", userData);
      } catch (error) {
        console.error("Failed to fetch current user:", error);
        setError("Failed to fetch user details. Please log in.");
      }
    };

    loadCurrentUser();
  }, []);

  //Fetch current game status
  useEffect(() => {
    const fetchStatus = async () => {
        try {
            const status = await fetchLoggedStatus(game.id); // Call endpoint with rawgId
            setGameStatus(status || "not-logged");
        } catch (error) {
            console.error("Failed to fetch game status:", error);
        }
    };

    fetchStatus();
  }, [game]);

  
  // Allow users to submit a review
  const handleSubmitReview = async () => {
    if (!reviewText.trim()) {
      setError("Review text cannot be empty.");
      return;
    }

    setSubmitting(true);
    setError(null);

    try {
      const newReview = await submitReview(game.id, reviewText);
      setReviews([newReview, ...reviews]);
      setReviewText("");
      alert("Review submitted successfully.");
    } catch (error) {
      console.error("Failed to submit review:", error);
      setError("Failed to submit review.");
    }
    setSubmitting(false);
  };

  // Allow users to submit a rating
  const handleSubmitRating = async () => {
    if (ratingValue === null) {
      setError("Please select a rating before submitting.");
      return;
    }

    if (!ratingValue || ratingValue < 1 || ratingValue > 5) {
      setError("Please select a valid rating between 1 and 5.");
      return;
    }

    setSubmitting(true);
    setError(null);

    try {
      await submitRating(game.id, ratingValue);
      const updatedSummary = await fetchRatingsSummary(game.id);
      setAverageRating(updatedSummary.averageRating ?? averageRating);
      setTotalRatings(updatedSummary.totalRatings ?? totalRatings);
      alert("Rating submitted successfully.");
    } catch (error) {
      console.error("Failed to submit rating:", error);
      setError("Failed to submit rating.");
    }
    setSubmitting(false);
  };

  // Allow users to edit a review
  const handleEditReview = (reviewId, currentText) => {
    setEditingReviewId(reviewId);
    setUpdatedReviewText(currentText);
  };

  const handleUpdateReview = async (reviewId) => {
    if (!updatedReviewText.trim()) {
      setError("Updated review text cannot be empty.");
      return;
    }

    try {
      await updateReview(reviewId, updatedReviewText);
      setReviews((prevReviews) =>
        prevReviews.map((review) =>
          review.id === reviewId ? { ...review, reviewText: updatedReviewText } : review
        )
      );
      setEditingReviewId(null);
      setUpdatedReviewText("");
    } catch (error) {
      console.error("Failed to update review:", error);
      setError("Failed to update review.");
    }
  };

  // Allow users to delete a review
  const handleDeleteReview = async (reviewId) => {
    try {
      await deleteReview(reviewId);
      setReviews((prevReviews) =>
        prevReviews.filter((review) => review.id !== reviewId)
      );
    } catch (error) {
      console.error("Failed to delete review:", error);
      setError("Failed to delete review.");
    }
  };

  // Allow users to add games to custom lists
  const handleAddToList = () => {
    navigate(`/custom-lists/select?gameId=${game.id}`);
  };

  // Allow users to update game played status
  const handleUpdateGameStatus = async (status) => {
    try {
      await logGameStatus(game.id, status); // Log status to the backend
      setGameStatus(status); // Update UI
      alert(`Game marked as "${status}".`);
    } catch (error) {
      console.error("Failed to update game status:", error);
      setError("Failed to update game status.");
    }
  };

  if (loading) {
    return <div className="loader">Loading...</div>;
  }

  if (!game || !currentUser) {
    return (
      <div className="error-container">
        <p className="error-message">{error || "Failed to load game details."}</p>
      </div>
    );
  }

  // Handle visual appearance
  return (
    <div className="game-details-container">
      {error && <p className="error-message">{error}</p>}

      <div className="navigation-buttons">
        <button onClick={() => navigate("/")} className="nav-button">
          Back to Home
        </button>
        <button onClick={() => navigate("/profile")} className="nav-button">
          Go to Profile
        </button>
      </div>

      <div className="game-details">
        <img src={game.coverImage} alt={game.title} className="game-image" />
        <h1 className="game-title">{game.title}</h1>
        <p className="game-description">{game.description}</p>
        <p className="game-release">
          <strong>Release Date:</strong> {game.releaseDate}
        </p>
        <p className="game-rating">
          <strong>Average Rating:</strong> {averageRating.toFixed(1)} ({totalRatings}{" "}
          {totalRatings === 1 ? "rating" : "ratings"})
        </p>

        <button onClick={handleAddToList} className="add-to-list-button">
          Add to List
        </button>

        <div className="status-section">
          <p>Set Game Status:</p>
          <select
            value={gameStatus}
            onChange={(e) => handleUpdateGameStatus(e.target.value)}
            className="status-dropdown"
          >
            <option value="">Select Status</option>
            <option value="played">Played</option>
            <option value="in-progress">In Progress</option>
            <option value="wishlisted">Wishlisted</option>
          </select>
          <p>Current Game Status: {gameStatus || "None selected"}</p> {/* Display the current status */}
        </div>

        <div className="rating-section">
          <p>Submit Your Rating:</p>
          <select
            value={ratingValue}
            onChange={(e) => setRatingValue(Number(e.target.value))}
            className="rating-dropdown"
          >
            <option value="">Select Rating</option>
            {[1, 2, 3, 4, 5].map((rating) => (
              <option key={rating} value={rating}>
                {rating} Star{rating > 1 ? "s" : ""}
              </option>
            ))}
          </select>
          <button
            onClick={handleSubmitRating}
            className="submit-button"
            disabled={submitting}
          >
            Submit Rating
          </button>
        </div>

        <div className="review-section">
          <p>Submit Your Review:</p>
          <textarea
            placeholder="Write your review here..."
            value={reviewText}
            onChange={(e) => setReviewText(e.target.value)}
            className="review-textarea"
          />
          <button
            onClick={handleSubmitReview}
            className="submit-button"
            disabled={!reviewText.trim() || submitting}
          >
            Submit Review
          </button>
        </div>

        <div className="user-reviews">
          <h2>User Reviews:</h2>
          {reviews.length === 0 ? (
            <p>No reviews yet. Be the first to review!</p>
          ) : (
            reviews.map((review) => (
              <div key={review.id} className="review-card">
                <p className="review-user">{review.user?.username || "Unknown User"}</p>
                {review.user?.id === currentUser?.id ? (
                  editingReviewId === review.id ? (
                    <>
                      <textarea
                        value={updatedReviewText}
                        onChange={(e) => setUpdatedReviewText(e.target.value)}
                        className="review-edit-textarea"
                      />
                      <button
                        onClick={() => handleUpdateReview(review.id)}
                        className="submit-button"
                      >
                        Save
                      </button>
                      <button
                        onClick={() => setEditingReviewId(null)}
                        className="cancel-button"
                      >
                        Cancel
                      </button>
                    </>
                  ) : (
                    <>
                      <p>{review.reviewText}</p>
                      <p className="review-date">
                        {new Date(review.createdAt).toLocaleString()}
                      </p>
                      <button
                        onClick={() =>
                          handleEditReview(review.id, review.reviewText)
                        }
                        className="edit-button"
                      >
                        Edit
                      </button>
                      <button
                        onClick={() => handleDeleteReview(review.id)}
                        className="delete-button"
                      >
                        Delete
                      </button>
                    </>
                  )
                ) : (
                  <>
                    <p>{review.reviewText}</p>
                    <p className="review-date">
                      {new Date(review.createdAt).toLocaleString()}
                    </p>
                  </>
                )}
              </div>
            ))
          )}
        </div>
      </div>
    </div>
  );
};

export default GameDetailsPage;
