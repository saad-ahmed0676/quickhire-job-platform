package com.quickhire.service;

import com.quickhire.dao.ReviewDAO;
import com.quickhire.dao.UserDAO;
import com.quickhire.model.Review;
import java.util.List;

public class ReviewService {

    private final ReviewDAO reviewDAO = new ReviewDAO();
    private final UserDAO   userDAO   = new UserDAO();

    // UC09 — Submit a review
    public Review submitReview(int jobId, int reviewerId, int revieweeId,
                               int rating, String comment) throws Exception {
        if (rating < 1 || rating > 5)
            throw new IllegalArgumentException("Rating must be between 1 and 5.");

        // Guard: already reviewed this job?
        if (reviewDAO.hasReviewed(jobId, reviewerId))
            throw new Exception("You have already submitted a review for this job.");

        Review review = new Review();
        review.setJobId(jobId);
        review.setReviewerId(reviewerId);
        review.setRevieweeId(revieweeId);
        review.setRating(rating);
        review.setComment(comment);

        int id = reviewDAO.insertReview(review);
        review.setReviewId(id);

        // Recalculate and update reviewee's average rating
        updateAverageRating(revieweeId);
        return review;
    }

    // Recalculates average rating from all reviews for that user
    public void updateAverageRating(int userId) throws Exception {
        double avg = reviewDAO.calculateAverageRating(userId);
        userDAO.updateAverageRating(userId, avg);
    }

    public List<Review> getReviewsForUser(int userId) throws Exception {
        return reviewDAO.getReviewsByRevieweeId(userId);
    }
}