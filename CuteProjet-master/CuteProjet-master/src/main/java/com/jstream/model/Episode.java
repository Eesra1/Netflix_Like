package com.jstream.model;

public class Episode {
    private int id;
    private String title;
    private int episodeNumber;
    private int duration;
    private String summary;
    private String videoUrl;
    private String thumbnailUrl;
    private int seasonId;

    public Episode() {}

    public Episode(int id, String title, int episodeNumber, int duration, String summary, String videoUrl, String thumbnailUrl, int seasonId) {
        this.id = id;
        this.title = title;
        this.episodeNumber = episodeNumber;
        this.duration = duration;
        this.summary = summary;
        this.videoUrl = videoUrl;
        this.thumbnailUrl = thumbnailUrl;
        this.seasonId = seasonId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public int getEpisodeNumber() { return episodeNumber; }
    public void setEpisodeNumber(int episodeNumber) { this.episodeNumber = episodeNumber; }

    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public String getVideoUrl() { return videoUrl; }
    public void setVideoUrl(String videoUrl) { this.videoUrl = videoUrl; }

    public String getThumbnailUrl() { return thumbnailUrl; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }

    public int getSeasonId() { return seasonId; }
    public void setSeasonId(int seasonId) { this.seasonId = seasonId; }
}