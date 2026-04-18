package com.jstream.dao;

import com.jstream.model.Admin;
import java.sql.*;

public class AdminDAO {

    public Admin findByEmail(String email) throws SQLException {
        PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(
                "SELECT * FROM admin WHERE email=?");
        ps.setString(1, email);
        ResultSet rs = ps.executeQuery();
        if (rs.next())
            return new Admin( rs.getString("email"), rs.getString("password"));
        return null;
    }
}
