package com.jstream.dao;

import com.jstream.model.Subscription;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubscriptionDAO {

    // ── Insertion ────────────────────────────────────────────────────────────

    public void add(Subscription s) throws SQLException {
        PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(
                "INSERT INTO subscriptions (user_id, plan, start_date, end_date, is_active) VALUES (?, ?, ?, ?, ?)");
        ps.setInt(1, s.getUserId());
        ps.setString(2, s.getPlan());
        ps.setString(3, s.getStartDate());
        ps.setString(4, s.getEndDate());
        ps.setBoolean(5, s.isActive());
        ps.executeUpdate();
    }

    // ── Modification ─────────────────────────────────────────────────────────

    public void update(Subscription s) throws SQLException {
        PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(
                "UPDATE subscriptions SET user_id=?, plan=?, start_date=?, end_date=?, is_active=? WHERE id=?");
        ps.setInt(1, s.getUserId());
        ps.setString(2, s.getPlan());
        ps.setString(3, s.getStartDate());
        ps.setString(4, s.getEndDate());
        ps.setBoolean(5, s.isActive());
        ps.setInt(6, s.getId());
        ps.executeUpdate();
    }

    // ── Suppression ──────────────────────────────────────────────────────────

    public void delete(int id) throws SQLException {
        PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(
                "DELETE FROM subscriptions WHERE id=?");
        ps.setInt(1, id);
        ps.executeUpdate();
    }

    // ── Comptages ────────────────────────────────────────────────────────────

    public int countActiveSubscriptions() throws SQLException {
        ResultSet rs = DatabaseConnection.getInstance().createStatement()
                .executeQuery("SELECT COUNT(*) FROM subscriptions WHERE is_active = true");
        if (rs.next()) return rs.getInt(1);
        return 0;
    }

    public int countTotal() throws SQLException {
        ResultSet rs = DatabaseConnection.getInstance().createStatement()
                .executeQuery("SELECT COUNT(*) FROM subscriptions");
        if (rs.next()) return rs.getInt(1);
        return 0;
    }

    public Map<String, Integer> countPerMonth() throws SQLException {
        Map<String, Integer> map = new HashMap<>();
        ResultSet rs = DatabaseConnection.getInstance().createStatement()
                .executeQuery(
                        "SELECT DATE_FORMAT(start_date, '%Y-%m') AS mois, COUNT(*) AS total " +
                                "FROM subscriptions GROUP BY mois ORDER BY mois");
        while (rs.next())
            map.put(rs.getString("mois"), rs.getInt("total"));
        return map;
    }

    // ── Listes ───────────────────────────────────────────────────────────────

    /** Utilisé par SubscriptionController (dashboard). */
    public List<Subscription> findAll(String filter) throws SQLException {
        return findAll(filter, null);
    }

    /** Utilisé par AbonnesController — filtre combiné search + plan exact. */
    public List<Subscription> findAll(String search, String plan) throws SQLException {
        List<Subscription> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
                "SELECT id, user_id, plan, start_date, end_date, is_active FROM subscriptions WHERE 1=1");

        if (search != null && !search.isEmpty())
            sql.append(" AND plan LIKE ?");
        if (plan != null && !plan.isEmpty())
            sql.append(" AND plan = ?");

        sql.append(" ORDER BY id DESC");

        PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(sql.toString());

        int idx = 1;
        if (search != null && !search.isEmpty())
            ps.setString(idx++, "%" + search + "%");
        if (plan != null && !plan.isEmpty())
            ps.setString(idx, plan);

        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            list.add(new Subscription(
                    rs.getInt("id"),
                    rs.getInt("user_id"),
                    rs.getString("plan"),
                    rs.getString("start_date"),
                    rs.getString("end_date"),
                    rs.getBoolean("is_active")
            ));
        }
        return list;
    }
}