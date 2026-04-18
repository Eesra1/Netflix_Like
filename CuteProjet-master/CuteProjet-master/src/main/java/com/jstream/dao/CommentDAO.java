package com.jstream.dao;

import com.jstream.model.Comment;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommentDAO {

    // ── Helper mapping ────────────────────────────────────────────────────────
    private Comment mapRow(ResultSet rs) throws SQLException {
        Timestamp ts = rs.getTimestamp("created_at");
        return new Comment(
                rs.getInt("id"),
                rs.getInt("user_id"),
                rs.getInt("film_id"),
                rs.getInt("series_id"),
                rs.getInt("episode_id"),
                rs.getString("content"),
                rs.getBoolean("is_flagged"),
                ts != null ? ts.toLocalDateTime() : null
        );
    }

    // ── Ajout (film) ─────────────────────────────────────────────────────────
    public void addFilmComment(int userId, int filmId, String content) throws SQLException {
        PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(
                "INSERT INTO comments (user_id, film_id, content) VALUES (?, ?, ?)");
        ps.setInt(1, userId);
        ps.setInt(2, filmId);
        ps.setString(3, content);
        ps.executeUpdate();
    }

    // ── Ajout (série) ────────────────────────────────────────────────────────
    public void addSeriesComment(int userId, int seriesId, String content) throws SQLException {
        PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(
                "INSERT INTO comments (user_id, series_id, content) VALUES (?, ?, ?)");
        ps.setInt(1, userId);
        ps.setInt(2, seriesId);
        ps.setString(3, content);
        ps.executeUpdate();
    }

    // ── Ajout (épisode) ──────────────────────────────────────────────────────
    public void addEpisodeComment(int userId, int episodeId, String content) throws SQLException {
        PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(
                "INSERT INTO comments (user_id, episode_id, content) VALUES (?, ?, ?)");
        ps.setInt(1, userId);
        ps.setInt(2, episodeId);
        ps.setString(3, content);
        ps.executeUpdate();
    }

    // Méthode générique rétrocompatible (garde l'ancienne signature)
    public void add(Comment c) throws SQLException {
        if (c.getFilmId() > 0)    addFilmComment(c.getUserId(), c.getFilmId(), c.getContent());
        else if (c.getSeriesId() > 0)  addSeriesComment(c.getUserId(), c.getSeriesId(), c.getContent());
        else if (c.getEpisodeId() > 0) addEpisodeComment(c.getUserId(), c.getEpisodeId(), c.getContent());
    }

    // ── Suppression (admin) ──────────────────────────────────────────────────
    public void delete(int id) throws SQLException {
        PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(
                "DELETE FROM comments WHERE id=?");
        ps.setInt(1, id);
        ps.executeUpdate();
    }

    // ── Flag / Unflag ────────────────────────────────────────────────────────
    public void setFlagged(int id, boolean flagged) throws SQLException {
        PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(
                "UPDATE comments SET is_flagged=? WHERE id=?");
        ps.setBoolean(1, flagged);
        ps.setInt(2, id);
        ps.executeUpdate();
    }

    // ── Listes par contenu ───────────────────────────────────────────────────
    public List<Comment> findByFilm(int filmId) throws SQLException {
        return findBy("film_id", filmId);
    }

    public List<Comment> findBySeries(int seriesId) throws SQLException {
        return findBy("series_id", seriesId);
    }

    public List<Comment> findByEpisode(int episodeId) throws SQLException {
        return findBy("episode_id", episodeId);
    }

    private List<Comment> findBy(String column, int id) throws SQLException {
        List<Comment> list = new ArrayList<>();
        PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(
                "SELECT * FROM comments WHERE " + column + "=? ORDER BY created_at DESC");
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) list.add(mapRow(rs));
        return list;
    }

    // ── Tous les commentaires (admin) ────────────────────────────────────────
    public List<Comment> findAll() throws SQLException {
        List<Comment> list = new ArrayList<>();
        ResultSet rs = DatabaseConnection.getInstance().createStatement()
                .executeQuery("SELECT * FROM comments ORDER BY created_at DESC");
        while (rs.next()) list.add(mapRow(rs));
        return list;
    }

    // ── Commentaires signalés ────────────────────────────────────────────────
    public List<Comment> getFlagged() throws SQLException {
        List<Comment> list = new ArrayList<>();
        ResultSet rs = DatabaseConnection.getInstance().createStatement()
                .executeQuery("SELECT * FROM comments WHERE is_flagged=TRUE ORDER BY created_at DESC");
        while (rs.next()) list.add(mapRow(rs));
        return list;
    }
}