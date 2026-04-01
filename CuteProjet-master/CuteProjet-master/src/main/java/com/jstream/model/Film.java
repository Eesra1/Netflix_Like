package com.jstream.model;

public class Film {
    private int id;
    private String title;
    private String synopsis;
    private String cast;
    private String releaseDate;
    private String videoUrl;
    private String coverUrl;
    private int categoryId;

    public Film() {}

    public Film(int id, String title, String synopsis, String cast, String releaseDate, String videoUrl, String coverUrl, int categoryId) {
        this.id = id;
        this.title = title;
        this.synopsis = synopsis;
        this.cast = cast;
        this.releaseDate = releaseDate;
        this.videoUrl = videoUrl;
        this.coverUrl = coverUrl;
        this.categoryId = categoryId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSynopsis() { return synopsis; }
    public void setSynopsis(String synopsis) { this.synopsis = synopsis; }

    public String getCast() { return cast; }
    public void setCast(String cast) { this.cast = cast; }

    public String getReleaseDate() { return releaseDate; }
    public void setReleaseDate(String releaseDate) { this.releaseDate = releaseDate; }

    public String getVideoUrl() { return videoUrl; }
    public void setVideoUrl(String videoUrl) { this.videoUrl = videoUrl; }

    public String getCoverUrl() { return coverUrl; }
    public void setCoverUrl(String coverUrl) { this.coverUrl = coverUrl; }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }
}