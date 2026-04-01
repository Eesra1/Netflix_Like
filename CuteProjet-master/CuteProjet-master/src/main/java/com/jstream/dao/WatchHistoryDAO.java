package com.jstream.dao;

import com.jstream.model.WatchHistory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WatchHistoryDAO {

    public void save(WatchHistory w) throws SQLException {
        PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(
                "INSERT INTO watch_history (user_id, episode_id, progress_seconds, is_watched) VALUES (?, ?, ?, ?)");
        ps.setInt(1, w.getUserId());
        ps.setInt(2, w.getEpisodeId());
        ps.setInt(3, w.getProgressSeconds());
        ps.setBoolean(4, w.isWatched());
        ps.executeUpdate();
    }

    public void updateProgress(int userId, int episodeId, int seconds) throws SQLException {
        PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(
                "UPDATE watch_history SET progress_seconds=? WHERE user_id=? AND episode_id=?");
        ps.setInt(1, seconds);
        ps.setInt(2, userId);
        ps.setInt(3, episodeId);
        ps.executeUpdate();
    }

    public void markAsWatched(int userId, int episodeId) throws SQLException {
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
                    rs.getInt("episode_id"), rs.getInt("progress_seconds"), rs.getBoolean("is_watched")));
        return list;
    }
}