package com.jstream.dao;

import com.jstream.model.Series;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SeriesDAO {

    public void add(Series s) throws SQLException {
        PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(
                "INSERT INTO series (title, synopsis, cast, category_id, cover_url) VALUES (?, ?, ?, ?, ?)");
        ps.setString(1, s.getTitle());
        ps.setString(2, s.getSynopsis());
        ps.setString(3, s.getCast());
        ps.setInt(4, s.getCategoryId());
        ps.setString(5, s.getCoverUrl());
        ps.executeUpdate();
    }

    public void delete(int id) throws SQLException {
        PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(
                "DELETE FROM series WHERE id=?");
        ps.setInt(1, id);
        ps.executeUpdate();
    }

    public void update(Series s) throws SQLException {
        PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(
                "UPDATE series SET title=?, synopsis=?, cast=?, category_id=?, cover_url=? WHERE id=?");
        ps.setString(1, s.getTitle());
        ps.setString(2, s.getSynopsis());
        ps.setString(3, s.getCast());
        ps.setInt(4, s.getCategoryId());
        ps.setString(5, s.getCoverUrl());
        ps.setInt(6, s.getId());
        ps.executeUpdate();
    }

    public List<Series> findAll() throws SQLException {
        List<Series> list = new ArrayList<>();
        ResultSet rs = DatabaseConnection.getInstance().createStatement()
                .executeQuery("SELECT * FROM series");
        while (rs.next())
            list.add(new Series(rs.getInt("id"), rs.getString("title"), rs.getString("synopsis"),
                    rs.getString("cast"), rs.getInt("category_id"), rs.getString("cover_url")));
        return list;
    }
}