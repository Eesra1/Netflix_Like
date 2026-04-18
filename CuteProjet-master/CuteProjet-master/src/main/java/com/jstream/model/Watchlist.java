package com.jstream.model;

public class Watchlist {
    private int id;
    private int userId;
    private int filmId;
    private int seriesId;
    private int episodeId;

    public Watchlist() {}

    public Watchlist(int id, int userId, int filmId, int seriesId, int episodeId) {
        this.id        = id;
        this.userId    = userId;
        this.filmId    = filmId;
        this.seriesId  = seriesId;
        this.episodeId = episodeId;
    }

    // Constructeur rétrocompatible (film uniquement)
    public Watchlist(int id, int userId, int filmId) {
        this(id, userId, filmId, 0, 0);
    }

    public int getId()              { return id; }
    public void setId(int id)       { this.id = id; }

    public int getUserId()          { return userId; }
    public void setUserId(int v)    { this.userId = v; }

    public int getFilmId()          { return filmId; }
    public void setFilmId(int v)    { this.filmId = v; }

    public int getSeriesId()        { return seriesId; }
    public void setSeriesId(int v)  { this.seriesId = v; }

    public int getEpisodeId()       { return episodeId; }
    public void setEpisodeId(int v) { this.episodeId = v; }
}