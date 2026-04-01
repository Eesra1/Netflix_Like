package com.jstream.service;

import com.jstream.dao.DatabaseConnection;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class AnalyticsService {

    public Map<String, Integer> getFilmsPerCategory() throws SQLException {
        Map<String, Integer> map = new HashMap<>();
        ResultSet rs = DatabaseConnection.getInstance().createStatement().executeQuery(
                "SELECT c.name, COUNT(f.id) as total FROM categories c LEFT JOIN films f ON c.id = f.category_id GROUP BY c.name");
        while (rs.next())
            map.put(rs.getString("name"), rs.getInt("total"));
        return map;
    }

    public Map<String, Integer> getTop5Films() throws SQLException {
        Map<String, Integer> map = new HashMap<>();
        ResultSet rs = DatabaseConnection.getInstance().createStatement().executeQuery(
                "SELECT f.title, COUNT(w.id) as total FROM films f LEFT JOIN watch_history w ON f.id = w.episode_id GROUP BY f.title ORDER BY total DESC LIMIT 5");
        while (rs.next())
            map.put(rs.getString("title"), rs.getInt("total"));
        return map;
    }

    public Map<String, Integer> getRegistrationsPerDay() throws SQLException {
        Map<String, Integer> map = new HashMap<>();
        ResultSet rs = DatabaseConnection.getInstance().createStatement().executeQuery(
                "SELECT created_at, COUNT(id) as total FROM users GROUP BY created_at");
        while (rs.next())
            map.put(rs.getString("created_at"), rs.getInt("total"));
        return map;
    }
}