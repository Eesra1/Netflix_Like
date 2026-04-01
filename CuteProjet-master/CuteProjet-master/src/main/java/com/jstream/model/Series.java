package com.jstream.model;

public class Series {
    private int id;
    private String title;
    private String synopsis;
    private String cast;
    private int categoryId;
    private String coverUrl;

    public Series() {}

    public Series(int id, String title, String synopsis, String cast, int categoryId, String coverUrl) {
        this.id = id;
        this.title = title;
        this.synopsis = synopsis;
        this.cast = cast;
        this.categoryId = categoryId;
        this.coverUrl = coverUrl;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSynopsis() { return synopsis; }
    public void setSynopsis(String synopsis) { this.synopsis = synopsis; }

    public String getCast() { return cast; }
    public void setCast(String cast) { this.cast = cast; }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public String getCoverUrl() { return coverUrl; }
    public void setCoverUrl(String coverUrl) { this.coverUrl = coverUrl; }


}