package com.jstream.dao;

import com.jstream.model.Comment;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommentDAO {

    public void add(Comment c) throws SQLException {
        PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(
                "INSERT INTO comments (user_id, film_id, content) VALUES (?, ?, ?)");
        ps.setInt(1, c.getUserId());
        ps.setInt(2, c.getFilmId());
        ps.setString(3, c.getContent());
        ps.executeUpdate();
    }

    public void delete(int id) throws SQLException {
        PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(
                "DELETE FROM comments WHERE id=?");
        ps.setInt(1, id);
        ps.executeUpdate();
    }

    public List<Comment> findByFilm(int filmId) throws SQLException {
        List<Comment> list = new ArrayList<>();
        PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(
                "SELECT * FROM comments WHERE film_id=?");
        ps.setInt(1, filmId);
        ResultSet rs = ps.executeQuery();
        while (rs.next())
            list.add(new Comment(rs.getInt("id"), rs.getInt("user_id"),
                    rs.getInt("film_id"), rs.getString("content"), rs.getBoolean("is_flagged")));
        return list;
    }

    public List<Comment> getFlagged() throws SQLException {
        List<Comment> list = new ArrayList<>();
        ResultSet rs = DatabaseConnection.getInstance().createStatement()
                .executeQuery("SELECT * FROM comments WHERE is_flagged=true");
        while (rs.next())
            list.add(new Comment(rs.getInt("id"), rs.getInt("user_id"),
                    rs.getInt("film_id"), rs.getString("content"), rs.getBoolean("is_flagged")));
        return list;
    }
    public List<Comment> findBySeries(int seriesId) throws SQLException {
        List<Comment> list = new ArrayList<>();
        PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(
                "SELECT * FROM comments WHERE series_id=?");
        ps.setInt(1, seriesId);
        ResultSet rs = ps.executeQuery();
        while (rs.next())
            list.add(new Comment(rs.getInt("id"), rs.getInt("user_id"),
                    rs.getInt("film_id"), rs.getString("content"), rs.getBoolean("is_flagged")));
        return list;
    }
}