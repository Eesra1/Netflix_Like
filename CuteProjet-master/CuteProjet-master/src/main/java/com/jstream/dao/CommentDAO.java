package com.jstream.dao;

import com.jstream.model.Comment;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommentDAO {

    private Connection getConnection() throws SQLException {
        return DatabaseConnection.getInstance();
    }

    private Comment mapRow(ResultSet rs) throws SQLException {
        Timestamp ts = rs.getTimestamp("created_at");
        return new Comment(
                rs.getInt("id"),
                rs.getInt("user_id"),
                rs.getInt("film_id"),
                rs.getInt("series_id"),
                rs.getInt("episode_id"),
                rs.getString("content"),
                rs.getBoolean("is_flagged"),
                ts != null ? ts.toLocalDateTime() : null
        );
    }

    private void executeInsert(String sql, Object... params) throws SQLException {
        Connection conn = getConnection();
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sql);
            for (int i = 0; i < params.length; i++) {
                if (params[i] instanceof Integer) {
                    ps.setInt(i + 1, (Integer) params[i]);
                } else if (params[i] instanceof String) {
                    ps.setString(i + 1, (String) params[i]);
                } else if (params[i] instanceof Boolean) {
                    ps.setBoolean(i + 1, (Boolean) params[i]);
                }
            }
            ps.executeUpdate();
        } catch (SQLException e) {
            try { if (!conn.getAutoCommit()) conn.rollback(); } catch (SQLException ignored) {}
            throw e;
        } finally {
            if (ps != null) { try { ps.close(); } catch (SQLException ignored) {} }
        }
        if (!conn.getAutoCommit()) conn.commit();
    }

    public void addFilmComment(int userId, int filmId, String content) throws SQLException {
        executeInsert(
                "INSERT INTO comments (user_id, film_id, content, is_flagged, created_at) VALUES (?, ?, ?, 0, CURRENT_TIMESTAMP)",
                userId, filmId, content
        );
    }

    public void addSeriesComment(int userId, int seriesId, String content) throws SQLException {
        executeInsert(
                "INSERT INTO comments (user_id, series_id, content, is_flagged, created_at) VALUES (?, ?, ?, 0, CURRENT_TIMESTAMP)",
                userId, seriesId, content
        );
    }

    public void addEpisodeComment(int userId, int episodeId, String content) throws SQLException {
        executeInsert(
                "INSERT INTO comments (user_id, episode_id, content, is_flagged, created_at) VALUES (?, ?, ?, 0, CURRENT_TIMESTAMP)",
                userId, episodeId, content
        );
    }

    public void add(Comment c) throws SQLException {
        if      (c.getFilmId()    > 0) addFilmComment(c.getUserId(), c.getFilmId(), c.getContent());
        else if (c.getSeriesId()  > 0) addSeriesComment(c.getUserId(), c.getSeriesId(), c.getContent());
        else if (c.getEpisodeId() > 0) addEpisodeComment(c.getUserId(), c.getEpisodeId(), c.getContent());
    }

    public void delete(int id) throws SQLException {
        Connection conn = getConnection();
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement("DELETE FROM comments WHERE id=?");
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            try { if (!conn.getAutoCommit()) conn.rollback(); } catch (SQLException ignored) {}
            throw e;
        } finally {
            if (ps != null) { try { ps.close(); } catch (SQLException ignored) {} }
        }
        if (!conn.getAutoCommit()) conn.commit();
    }

    public void setFlagged(int id, boolean flagged) throws SQLException {
        Connection conn = getConnection();
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement("UPDATE comments SET is_flagged=? WHERE id=?");
            ps.setBoolean(1, flagged);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            try { if (!conn.getAutoCommit()) conn.rollback(); } catch (SQLException ignored) {}
            throw e;
        } finally {
            if (ps != null) { try { ps.close(); } catch (SQLException ignored) {} }
        }
        if (!conn.getAutoCommit()) conn.commit();
    }

    public List<Comment> findByFilm(int filmId) throws SQLException { return findBy("film_id", filmId); }
    public List<Comment> findBySeries(int seriesId) throws SQLException { return findBy("series_id", seriesId); }
    public List<Comment> findByEpisode(int episodeId) throws SQLException { return findBy("episode_id", episodeId); }

    private List<Comment> findBy(String column, int id) throws SQLException {
        List<Comment> list = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = getConnection().prepareStatement(
                    "SELECT * FROM comments WHERE " + column + "=? ORDER BY created_at DESC");
            ps.setInt(1, id);
            rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } finally {
            if (rs != null) { try { rs.close(); } catch (SQLException ignored) {} }
            if (ps != null) { try { ps.close(); } catch (SQLException ignored) {} }
        }
        return list;
    }

    public List<Comment> findAll() throws SQLException {
        List<Comment> list = new ArrayList<>();
        Statement st = null;
        ResultSet rs = null;
        try {
            st = getConnection().createStatement();
            rs = st.executeQuery("SELECT * FROM comments ORDER BY created_at DESC");
            while (rs.next()) list.add(mapRow(rs));
        } finally {
            if (rs != null) { try { rs.close(); } catch (SQLException ignored) {} }
            if (st != null) { try { st.close(); } catch (SQLException ignored) {} }
        }
        return list;
    }

    public List<Comment> getFlagged() throws SQLException {
        List<Comment> list = new ArrayList<>();
        Statement st = null;
        ResultSet rs = null;
        try {
            st = getConnection().createStatement();
            rs = st.executeQuery(
                    "SELECT * FROM comments WHERE is_flagged=TRUE ORDER BY created_at DESC");
            while (rs.next()) list.add(mapRow(rs));
        } finally {
            if (rs != null) { try { rs.close(); } catch (SQLException ignored) {} }
            if (st != null) { try { st.close(); } catch (SQLException ignored) {} }
        }
        return list;
    }
}