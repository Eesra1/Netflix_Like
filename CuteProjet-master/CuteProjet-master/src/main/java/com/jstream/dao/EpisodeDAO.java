package com.jstream.dao;

import com.jstream.model.Episode;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EpisodeDAO {

    public void add(Episode e) throws SQLException {
        PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(
                "INSERT INTO episodes (title, episode_number, duration, summary, video_url, thumbnail_url, season_id) VALUES (?, ?, ?, ?, ?, ?, ?)");
        ps.setString(1, e.getTitle());
        ps.setInt(2, e.getEpisodeNumber());
        ps.setInt(3, e.getDuration());
        ps.setString(4, e.getSummary());
        ps.setString(5, e.getVideoUrl());
        ps.setString(6, e.getThumbnailUrl());
        ps.setInt(7, e.getSeasonId());
        ps.executeUpdate();
    }

    public void delete(int id) throws SQLException {
        PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(
                "DELETE FROM episodes WHERE id=?");
        ps.setInt(1, id);
        ps.executeUpdate();
    }

    public List<Episode> findBySeason(int seasonId) throws SQLException {
        List<Episode> list = new ArrayList<>();
        PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(
                "SELECT * FROM episodes WHERE season_id=?");
        ps.setInt(1, seasonId);
        ResultSet rs = ps.executeQuery();
        while (rs.next())
            list.add(new Episode(rs.getInt("id"), rs.getString("title"), rs.getInt("episode_number"),
                    rs.getInt("duration"), rs.getString("summary"), rs.getString("video_url"),
                    rs.getString("thumbnail_url"), rs.getInt("season_id")));
        return list;
    }
}