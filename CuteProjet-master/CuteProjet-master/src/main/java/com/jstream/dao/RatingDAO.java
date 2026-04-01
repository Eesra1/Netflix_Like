package com.jstream.dao;

import com.jstream.model.Rating;
import java.sql.*;

public class RatingDAO {

    public void add(Rating r) throws SQLException {
        PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(
                "INSERT INTO ratings (user_id, film_id, stars) VALUES (?, ?, ?)");
        ps.setInt(1, r.getUserId());
        ps.setInt(2, r.getFilmId());
        ps.setInt(3, r.getStars());
        ps.executeUpdate();
    }

    public double getAverage(int filmId) throws SQLException {
        PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(
                "SELECT AVG(stars) FROM ratings WHERE film_id=?");
        ps.setInt(1, filmId);
        ResultSet rs = ps.executeQuery();
        if (rs.next())
            return rs.getDouble(1);
        return 0;
    }
}