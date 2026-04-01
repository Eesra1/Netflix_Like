package com.jstream.dao;

import java.sql.Connection;

import java.sql.Connection;

public class TestConnexion {
    public static void main(String[] args) {
        Connection conn = DatabaseConnection.getInstance();

        if (conn != null) {
            System.out.println("Le test est un succès, la base est connectée !");
        } else {
            System.out.println(" Échec du test.");
        }
    }
}