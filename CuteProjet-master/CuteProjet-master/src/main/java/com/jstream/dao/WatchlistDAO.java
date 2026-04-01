package com.jstream.dao;

import com.jstream.model.Watchlist;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WatchlistDAO {

    public void add(int userId, int filmId) throws SQLException {
        PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(
                "INSERT INTO watchlist (user_id, film_id) VALUES (?, ?)");
        ps.setInt(1, userId);
        ps.setInt(2, filmId);
        ps.executeUpdate();
    }

    public void remove(int userId, int filmId) throws SQLException {
        PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(
                "DELETE FROM watchlist WHERE user_id=? AND film_id=?");
        ps.setInt(1, userId);
        ps.setInt(2, filmId);
        ps.executeUpdate();
    }

    public List<Watchlist> findByUser(int userId) throws SQLException {
        List<Watchlist> list = new ArrayList<>();
        PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(
                "SELECT * FROM watchlist WHERE user_id=?");
        ps.setInt(1, userId);
        ResultSet rs = ps.executeQuery();
        while (rs.next())
            list.add(new Watchlist(rs.getInt("id"), rs.getInt("user_id"), rs.getInt("film_id")));
        return list;
    }
}