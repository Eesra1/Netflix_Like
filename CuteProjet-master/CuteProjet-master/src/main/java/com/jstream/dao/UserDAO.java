package com.jstream.dao;

import com.jstream.model.User;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    // ── Helpers ──────────────────────────────────────────────────────────────
    private LocalDateTime toLocalDateTime(Timestamp ts) {
        return ts != null ? ts.toLocalDateTime() : null;
    }

    private User mapRow(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("password_hash"),
                rs.getString("role"),
                toLocalDateTime(rs.getTimestamp("created_at")),
                toLocalDateTime(rs.getTimestamp("sub_start")),
                toLocalDateTime(rs.getTimestamp("sub_end")),
                rs.getString("sub_status")
        );
    }

    // ── CRUD ─────────────────────────────────────────────────────────────────
    public void add(User u) throws SQLException {
        LocalDateTime now = LocalDateTime.now();
        PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(
                "INSERT INTO users (name, email, password_hash, role, created_at, sub_start, sub_end, sub_status) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
        ps.setString(1, u.getName());
        ps.setString(2, u.getEmail());
        ps.setString(3, u.getPasswordHash());
        ps.setString(4, u.getRole());
        ps.setTimestamp(5, Timestamp.valueOf(now));
        ps.setTimestamp(6, Timestamp.valueOf(now));
        ps.setTimestamp(7, Timestamp.valueOf(now.plusDays(30)));
        ps.setString(8, "ACTIVE");
        ps.executeUpdate();
    }

    public void delete(int id) throws SQLException {
        PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(
                "DELETE FROM users WHERE id=?");
        ps.setInt(1, id);
        ps.executeUpdate();
    }

    public User findByEmail(String email) throws SQLException {
        PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(
                "SELECT * FROM users WHERE email=?");
        ps.setString(1, email);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) return mapRow(rs);
        return null;
    }

    public List<User> findAll() throws SQLException {
        List<User> list = new ArrayList<>();
        ResultSet rs = DatabaseConnection.getInstance().createStatement()
                .executeQuery("SELECT * FROM users ORDER BY created_at DESC");
        while (rs.next()) list.add(mapRow(rs));
        return list;
    }

    // ── Abonnements ──────────────────────────────────────────────────────────

    /** Renouveler l'abonnement d'un utilisateur pour 30 jours supplémentaires. */
    public void renewSubscription(int userId) throws SQLException {
        // Si l'abonnement est encore actif, on ajoute 30j à sub_end
        // Sinon, on repart de maintenant
        PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(
                "UPDATE users " +
                        "SET sub_start  = CASE WHEN sub_end >= NOW() THEN sub_start ELSE NOW() END, " +
                        "    sub_end    = CASE WHEN sub_end >= NOW() THEN DATE_ADD(sub_end, INTERVAL 30 DAY) ELSE DATE_ADD(NOW(), INTERVAL 30 DAY) END, " +
                        "    sub_status = 'ACTIVE' " +
                        "WHERE id = ?");
        ps.setInt(1, userId);
        ps.executeUpdate();
    }

    /** Synchroniser le statut EXPIRED pour tous les abonnements échus. */
    public void syncExpiredStatuses() throws SQLException {
        DatabaseConnection.getInstance().createStatement().executeUpdate(
                "UPDATE users SET sub_status = 'EXPIRED' " +
                        "WHERE sub_end < NOW() AND sub_status = 'ACTIVE'");
    }
}
 
