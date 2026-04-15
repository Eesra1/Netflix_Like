package com.jstream.dao;

import com.jstream.model.WatchHistory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WatchHistoryDAO {

    public void saveFilm(int userId, int filmId) throws SQLException {
        PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(
                "INSERT INTO watch_history (user_id, film_id, progress_seconds, is_watched) VALUES (?, ?, 0, false)");
        ps.setInt(1, userId);
        ps.setInt(2, filmId);
        ps.executeUpdate();
    }

    public void saveEpisode(int userId, int episodeId) throws SQLException {
        PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(
                "INSERT INTO watch_history (user_id, episode_id, progress_seconds, is_watched) VALUES (?, ?, 0, false)");
        ps.setInt(1, userId);
        ps.setInt(2, episodeId);
        ps.executeUpdate();
    }

    public void updateProgress(int userId, int filmId, int episodeId, int seconds) throws SQLException {
        if (filmId > 0) {
            PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(
                    "UPDATE watch_history SET progress_seconds=? WHERE user_id=? AND film_id=?");
            ps.setInt(1, seconds);
            ps.setInt(2, userId);
            ps.setInt(3, filmId);
            ps.executeUpdate();
        } else {
            PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(
                    "UPDATE watch_history SET progress_seconds=? WHERE user_id=? AND episode_id=?");
            ps.setInt(1, seconds);
            ps.setInt(2, userId);
            ps.setInt(3, episodeId);
            ps.executeUpdate();
        }
    }

    public void markFilmWatched(int userId, int filmId) throws SQLException {
        PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(
                "UPDATE watch_history SET is_watched=true WHERE user_id=? AND film_id=?");
        ps.setInt(1, userId);
        ps.setInt(2, filmId);
        ps.executeUpdate();
    }

    public void markEpisodeWatched(int userId, int episodeId) throws SQLException {
        PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(
                "UPDATE watch_history SET is_watched=true WHERE user_id=? AND episode_id=?");
        ps.setInt(1, userId);
        ps.setInt(2, episodeId);
        ps.executeUpdate();
    }

    public List<WatchHistory> findByUser(int userId) throws SQLException {
        List<WatchHistory> list = new ArrayList<>();
        PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(
                "SELECT * FROM watch_history WHERE user_id=?");
        ps.setInt(1, userId);
        ResultSet rs = ps.executeQuery();
        while (rs.next())
            list.add(new WatchHistory(rs.getInt("id"), rs.getInt("user_id"),
                    rs.getInt("episode_id"), rs.getInt("film_id"),
                    rs.getInt("progress_seconds"), rs.getBoolean("is_watched")));
        return list;
    }

    public int getFilmProgress(int userId, int filmId) throws SQLException {
        PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(
                "SELECT progress_seconds FROM watch_history WHERE user_id=? AND film_id=?");
        ps.setInt(1, userId);
        ps.setInt(2, filmId);
        ResultSet rs = ps.executeQuery();
        if (rs.next())
            return rs.getInt("progress_seconds");
        return 0;
    }
}