package com.jstream.model;

public class Comment {
    private int id;
    private int userId;
    private int filmId;
    private String content;
    private boolean flagged;
    private int seriesId;


    public Comment() {}

    public Comment(int id, int userId, int filmId, String content, boolean flagged) {
        this.id = id;
        this.userId = userId;
        this.filmId = filmId;
        this.content = content;
        this.flagged = flagged;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getFilmId() { return filmId; }
    public void setFilmId(int filmId) { this.filmId = filmId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public boolean isFlagged() { return flagged; }
    public void setFlagged(boolean flagged) { this.flagged = flagged; }
    public int getSeriesId() { return seriesId; }
    public void setSeriesId(int seriesId) { this.seriesId = seriesId; }

}