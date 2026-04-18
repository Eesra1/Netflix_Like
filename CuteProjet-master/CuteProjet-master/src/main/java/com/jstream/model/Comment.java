package com.jstream.model;

import java.time.LocalDateTime;

public class Comment {
    private int id;
    private int userId;
    private int filmId;
    private int seriesId;
    private int episodeId;
    private String content;
    private boolean flagged;
    private LocalDateTime createdAt;

    public Comment() {}

    public Comment(int id, int userId, int filmId, int seriesId, int episodeId,
                   String content, boolean flagged, LocalDateTime createdAt) {
        this.id        = id;
        this.userId    = userId;
        this.filmId    = filmId;
        this.seriesId  = seriesId;
        this.episodeId = episodeId;
        this.content   = content;
        this.flagged   = flagged;
        this.createdAt = createdAt;
    }

    // Constructeur rétrocompatible
    public Comment(int id, int userId, int filmId, String content, boolean flagged) {
        this(id, userId, filmId, 0, 0, content, flagged, null);
    }

    public int getId()               { return id; }
    public void setId(int id)        { this.id = id; }

    public int getUserId()           { return userId; }
    public void setUserId(int v)     { this.userId = v; }

    public int getFilmId()           { return filmId; }
    public void setFilmId(int v)     { this.filmId = v; }

    public int getSeriesId()         { return seriesId; }
    public void setSeriesId(int v)   { this.seriesId = v; }

    public int getEpisodeId()        { return episodeId; }
    public void setEpisodeId(int v)  { this.episodeId = v; }

    public String getContent()       { return content; }
    public void setContent(String v) { this.content = v; }

    public boolean isFlagged()       { return flagged; }
    public void setFlagged(boolean v){ this.flagged = v; }

    public LocalDateTime getCreatedAt()          { return createdAt; }
    public void setCreatedAt(LocalDateTime v)    { this.createdAt = v; }

    /** Retourne le type de contenu : "Film", "Série", "Épisode" ou "—" */
    public String getContentType() {
        if (filmId    > 0) return "Film";
        if (seriesId  > 0) return "Série";
        if (episodeId > 0) return "Épisode";
        return "—";
    }

    /** Retourne l'ID du contenu associé */
    public int getContentId() {
        if (filmId    > 0) return filmId;
        if (seriesId  > 0) return seriesId;
        if (episodeId > 0) return episodeId;
        return 0;
    }
}