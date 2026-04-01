package com.jstream.model;

public class Season {
    private int id;
    private int seasonNumber;
    private int seriesId;

    public Season() {}

    public Season(int id, int seasonNumber, int seriesId) {
        this.id = id;
        this.seasonNumber = seasonNumber;
        this.seriesId = seriesId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getSeasonNumber() { return seasonNumber; }
    public void setSeasonNumber(int seasonNumber) { this.seasonNumber = seasonNumber; }

    public int getSeriesId() { return seriesId; }
    public void setSeriesId(int seriesId) { this.seriesId = seriesId; }
}