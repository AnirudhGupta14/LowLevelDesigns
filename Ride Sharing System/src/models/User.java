package models;

import java.util.UUID;

/**
 * Base class representing any registered user on the platform.
 * Both Rider and Driver extend this class.
 */
public abstract class User {
    private final String id;
    private final String name;
    private final String email;
    private final String phone;
    private double rating;
    private int totalRatings;

    public User(String name, String email, String phone) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.rating = 5.0;
        this.totalRatings = 0;
    }

    // ── Getters ──────────────────────────────────────────────────────────────
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public double getRating() {
        return rating;
    }

    /**
     * Updates the running average rating after each completed ride.
     */
    public void updateRating(double newRating) {
        this.rating = ((this.rating * this.totalRatings) + newRating)
                / (this.totalRatings + 1);
        this.totalRatings++;
    }

    @Override
    public String toString() {
        return String.format("%s[id=%s, name=%s, rating=%.2f]",
                getClass().getSimpleName(), id, name, rating);
    }
}
