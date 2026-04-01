package com.jstream.dao;

import com.jstream.model.Category;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {

    public void add(Category c) throws SQLException {
        PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(
                "INSERT INTO categories (name) VALUES (?)");
        ps.setString(1, c.getName());
        ps.executeUpdate();
    }

    public void delete(int id) throws SQLException {
        PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(
                "DELETE FROM categories WHERE id=?");
        ps.setInt(1, id);
        ps.executeUpdate();
    }

    public List<Category> findAll() throws SQLException {
        List<Category> list = new ArrayList<>();
        ResultSet rs = DatabaseConnection.getInstance().createStatement()
                .executeQuery("SELECT * FROM categories");
        while (rs.next())
            list.add(new Category(rs.getInt("id"), rs.getString("name")));
        return list;
    }
}