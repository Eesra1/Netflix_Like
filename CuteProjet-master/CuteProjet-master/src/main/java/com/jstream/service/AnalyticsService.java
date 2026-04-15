package com.jstream.service;

import com.jstream.dao.DatabaseConnection;
import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class AnalyticsService {

    public Map<String, Integer> getFilmsPerCategory() throws SQLException {
        Map<String, Integer> map = new LinkedHashMap<>();
        ResultSet rs = DatabaseConnection.getInstance().createStatement().executeQuery(
                "SELECT c.name, COUNT(f.id) as total FROM categories c " +
                        "LEFT JOIN films f ON c.id = f.category_id GROUP BY c.name");
        while (rs.next())
            map.put(rs.getString("name"), rs.getInt("total"));
        return map;
    }

    public Map<String, Integer> getTop5Films() throws SQLException {
        Map<String, Integer> map = new LinkedHashMap<>();
        ResultSet rs = DatabaseConnection.getInstance().createStatement().executeQuery(
                "SELECT f.title, COUNT(w.id) as vues " +
                        "FROM films f LEFT JOIN watch_history w ON f.id = w.film_id " +
                        "GROUP BY f.title ORDER BY vues DESC LIMIT 5");
        while (rs.next())
            map.put(rs.getString("title"), rs.getInt("vues"));
        return map;
    }

    public Map<String, Integer> getTop5Episodes() throws SQLException {
        Map<String, Integer> map = new LinkedHashMap<>();
        ResultSet rs = DatabaseConnection.getInstance().createStatement().executeQuery(
                "SELECT e.title, COUNT(w.id) as vues " +
                        "FROM episodes e LEFT JOIN watch_history w ON e.id = w.episode_id " +
                        "GROUP BY e.title ORDER BY vues DESC LIMIT 5");
        while (rs.next())
            map.put(rs.getString("title"), rs.getInt("vues"));
        return map;
    }

    public Map<String, Integer> getTop5Series() throws SQLException {
        Map<String, Integer> map = new LinkedHashMap<>();
        ResultSet rs = DatabaseConnection.getInstance().createStatement().executeQuery(
                "SELECT s.title, COUNT(w.id) as vues " +
                        "FROM series s " +
                        "LEFT JOIN seasons sa ON sa.series_id = s.id " +
                        "LEFT JOIN episodes e ON e.season_id = sa.id " +
                        "LEFT JOIN watch_history w ON w.episode_id = e.id " +
                        "GROUP BY s.title ORDER BY vues DESC LIMIT 5");
        while (rs.next())
            map.put(rs.getString("title"), rs.getInt("vues"));
        return map;
    }

    public Map<String, Integer> getRegistrationsPerDay() throws SQLException {
        Map<String, Integer> map = new LinkedHashMap<>();
        ResultSet rs = DatabaseConnection.getInstance().createStatement().executeQuery(
                "SELECT created_at, COUNT(id) as total FROM users GROUP BY created_at ORDER BY created_at");
        while (rs.next())
            map.put(rs.getString("created_at"), rs.getInt("total"));
        return map;
    }
}