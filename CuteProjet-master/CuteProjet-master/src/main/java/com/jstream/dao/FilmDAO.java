package com.jstream.dao;

import com.jstream.model.Film;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FilmDAO {

    public void add(Film f) throws SQLException {
        PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(
                "INSERT INTO films (title, synopsis, cast, releaseDate, videoUrl, coverUrl, categoryId) VALUES (?, ?, ?, ?, ?, ?, ?)");
        ps.setString(1, f.getTitle());
        ps.setString(2, f.getSynopsis());
        ps.setString(3, f.getCast());
        ps.setString(4, f.getReleaseDate());
        ps.setString(5, f.getVideoUrl());
        ps.setString(6, f.getCoverUrl());
        ps.setInt(7, f.getCategoryId());
        ps.executeUpdate();
    }

    public void delete(int id) throws SQLException {
        PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(
                "DELETE FROM films WHERE id=?");
        ps.setInt(1, id);
        ps.executeUpdate();
    }

    public void update(Film f) throws SQLException {
        PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(
                "UPDATE films SET title=?, synopsis=?, cast=?, releaseDate=?, videoUrl=?, coverUrl=?, categoryId=? WHERE id=?");
        ps.setString(1, f.getTitle());
        ps.setString(2, f.getSynopsis());
        ps.setString(3, f.getCast());
        ps.setString(4, f.getReleaseDate());
        ps.setString(5, f.getVideoUrl());
        ps.setString(6, f.getCoverUrl());
        ps.setInt(7, f.getCategoryId());
        ps.setInt(8, f.getId());
        ps.executeUpdate();
    }

    public Film findById(int id) throws SQLException {
        PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(
                "SELECT * FROM films WHERE id=?");
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        if (rs.next())
            return new Film(rs.getInt("id"), rs.getString("title"), rs.getString("synopsis"),
                    rs.getString("cast"), rs.getString("release_date"), rs.getString("video_url"),
                    rs.getString("cover_url"), rs.getInt("category_id"));
        return null;
    }

    public List<Film> findAll() throws SQLException {
        List<Film> list = new ArrayList<>();
        ResultSet rs = DatabaseConnection.getInstance().createStatement()
                .executeQuery("SELECT * FROM films");
        while (rs.next())
            list.add(new Film(rs.getInt("id"), rs.getString("title"), rs.getString("synopsis"),
                    rs.getString("cast"), rs.getString("releaseDate"), rs.getString("videoUrl"),
                    rs.getString("coverUrl"), rs.getInt("categoryId")));
        return list;
    }

    public List<Film> findByCategory(int categoryId) throws SQLException {
        List<Film> list = new ArrayList<>();
        PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(
                "SELECT * FROM films WHERE category_id=?");
        ps.setInt(1, categoryId);
        ResultSet rs = ps.executeQuery();
        while (rs.next())
            list.add(new Film(rs.getInt("id"), rs.getString("title"), rs.getString("synopsis"),
                    rs.getString("cast"), rs.getString("release_date"), rs.getString("video_url"),
                    rs.getString("cover_url"), rs.getInt("category_id")));
        return list;
    }
}