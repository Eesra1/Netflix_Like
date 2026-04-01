package com.jstream.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static Connection connexion;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/jstream_db";
    private static final String USER = "root";
    private static final String PASS = "";

    private DatabaseConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connexion = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println("✅ Connexion à la base de données réussie !");
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("❌ Erreur de connexion à la base : " + e.getMessage());
        }
    }

    public static Connection getInstance() {
        try {
            if (connexion == null || connexion.isClosed()) {
                new DatabaseConnection();
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la vérification de la connexion : " + e.getMessage());
        }
        return connexion;
    }
}