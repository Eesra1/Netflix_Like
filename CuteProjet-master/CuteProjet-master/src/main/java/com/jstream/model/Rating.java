package com.jstream.model;

public class Rating {
    private int id;
    private int userId;
    private int filmId;
    private int stars;

    public Rating() {}

    public Rating(int id, int userId, int filmId, int stars) {
        this.id = id;
        this.userId = userId;
        this.filmId = filmId;
        this.stars = stars;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getFilmId() { return filmId; }
    public void setFilmId(int filmId) { this.filmId = filmId; }

    public int getStars() { return stars; }
    public void setStars(int stars) { this.stars = stars; }
}