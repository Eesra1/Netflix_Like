package com.jstream.model;

public class WatchHistory {
    private int id;
    private int userId;
    private int episodeId;
    private int filmId;
    private int progressSeconds;
    private boolean watched;

    public WatchHistory() {}

    public WatchHistory(int id, int userId, int episodeId, int filmId, int progressSeconds, boolean watched) {
        this.id = id;
        this.userId = userId;
        this.episodeId = episodeId;
        this.filmId = filmId;
        this.progressSeconds = progressSeconds;
        this.watched = watched;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getEpisodeId() { return episodeId; }
    public void setEpisodeId(int episodeId) { this.episodeId = episodeId; }

    public int getFilmId() { return filmId; }
    public void setFilmId(int filmId) { this.filmId = filmId; }

    public int getProgressSeconds() { return progressSeconds; }
    public void setProgressSeconds(int progressSeconds) { this.progressSeconds = progressSeconds; }

    public boolean isWatched() { return watched; }
    public void setWatched(boolean watched) { this.watched = watched; }
}