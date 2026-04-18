package com.jstream.dao;

import com.jstream.model.Watchlist;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO Watchlist — gère l'ajout / suppression d'un film, d'une série
 * ou d'un épisode dans la liste personnelle d'un utilisateur.
 */
public class WatchlistDAO {

    // ── Films ────────────────────────────────────────────────────────────────

    public void addFilm(int userId, int filmId) throws SQLException {
        PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(
                "INSERT IGNORE INTO watchlist (user_id, film_id) VALUES (?, ?)");
        ps.setInt(1, userId);
        ps.setInt(2, filmId);
        ps.executeUpdate();
    }

    public void removeFilm(int userId, int filmId) throws SQLException {
        PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(
                "DELETE FROM watchlist WHERE user_id=? AND film_id=?");
        ps.setInt(1, userId);
        ps.setInt(2, filmId);
        ps.executeUpdate();
    }

    public boolean isFilmInList(int userId, int filmId) throws SQLException {
        PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(
                "SELECT 1 FROM watchlist WHERE user_id=? AND film_id=?");
        ps.setInt(1, userId);
        ps.setInt(2, filmId);
        return ps.executeQuery().next();
    }

    // ── Séries ───────────────────────────────────────────────────────────────

    public void addSeries(int userId, int seriesId) throws SQLException {
        PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(
                "INSERT IGNORE INTO watchlist (user_id, series_id) VALUES (?, ?)");
        ps.setInt(1, userId);
        ps.setInt(2, seriesId);
        ps.executeUpdate();
    }

    public void removeSeries(int userId, int seriesId) throws SQLException {
        PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(
                "DELETE FROM watchlist WHERE user_id=? AND series_id=?");
        ps.setInt(1, userId);
        ps.setInt(2, seriesId);
        ps.executeUpdate();
    }

    public boolean isSeriesInList(int userId, int seriesId) throws SQLException {
        PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(
                "SELECT 1 FROM watchlist WHERE user_id=? AND series_id=?");
        ps.setInt(1, userId);
        ps.setInt(2, seriesId);
        return ps.executeQuery().next();
    }

    // ── Épisodes ─────────────────────────────────────────────────────────────

    public void addEpisode(int userId, int episodeId) throws SQLException {
        PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(
                "INSERT IGNORE INTO watchlist (user_id, episode_id) VALUES (?, ?)");
        ps.setInt(1, userId);
        ps.setInt(2, episodeId);
        ps.executeUpdate();
    }

    public void removeEpisode(int userId, int episodeId) throws SQLException {
        PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(
                "DELETE FROM watchlist WHERE user_id=? AND episode_id=?");
        ps.setInt(1, userId);
        ps.setInt(2, episodeId);
        ps.executeUpdate();
    }

    public boolean isEpisodeInList(int userId, int episodeId) throws SQLException {
        PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(
                "SELECT 1 FROM watchlist WHERE user_id=? AND episode_id=?");
        ps.setInt(1, userId);
        ps.setInt(2, episodeId);
        return ps.executeQuery().next();
    }

    // ── Liste complète d'un utilisateur ──────────────────────────────────────

    public List<Watchlist> findByUser(int userId) throws SQLException {
        List<Watchlist> list = new ArrayList<>();
        PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(
                "SELECT id, user_id, film_id, series_id, episode_id FROM watchlist WHERE user_id=?");
        ps.setInt(1, userId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            list.add(new Watchlist(
                    rs.getInt("id"),
                    rs.getInt("user_id"),
                    rs.getInt("film_id"),
                    rs.getInt("series_id"),
                    rs.getInt("episode_id")
            ));
        }
        return list;
    }
}