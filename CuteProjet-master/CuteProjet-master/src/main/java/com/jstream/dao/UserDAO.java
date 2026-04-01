package com.jstream.dao;

import com.jstream.model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public void add(User u) throws SQLException {
        PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(
                "INSERT INTO users (name, email, password_hash, role) VALUES (?, ?, ?, ?)");
        ps.setString(1, u.getName());
        ps.setString(2, u.getEmail());
        ps.setString(3, u.getPasswordHash());
        ps.setString(4, u.getRole());
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
        if (rs.next())
            return new User(rs.getInt("id"), rs.getString("name"),
                    rs.getString("email"), rs.getString("password_hash"), rs.getString("role"));
        return null;
    }

    public List<User> findAll() throws SQLException {
        List<User> list = new ArrayList<>();
        ResultSet rs = DatabaseConnection.getInstance().createStatement()
                .executeQuery("SELECT * FROM users");
        while (rs.next())
            list.add(new User(rs.getInt("id"), rs.getString("name"),
                    rs.getString("email"), rs.getString("password_hash"), rs.getString("role")));
        return list;
    }
}