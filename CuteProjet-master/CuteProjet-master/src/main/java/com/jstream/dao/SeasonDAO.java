package com.jstream.dao;

import com.jstream.model.Season;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SeasonDAO {

    public void add(Season s) throws SQLException {
        PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(
                "INSERT INTO seasons (season_number, series_id) VALUES (?, ?)");
        ps.setInt(1, s.getSeasonNumber());
        ps.setInt(2, s.getSeriesId());
        ps.executeUpdate();
    }

    public void delete(int id) throws SQLException {
        PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(
                "DELETE FROM seasons WHERE id=?");
        ps.setInt(1, id);
        ps.executeUpdate();
    }

    public List<Season> findBySeries(int seriesId) throws SQLException {
        List<Season> list = new ArrayList<>();
        PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(
                "SELECT * FROM seasons WHERE series_id=?");
        ps.setInt(1, seriesId);
        ResultSet rs = ps.executeQuery();
        while (rs.next())
            list.add(new Season(rs.getInt("id"), rs.getInt("season_number"), rs.getInt("series_id")));
        return list;
    }

    public List<Season> findAll() throws SQLException {
        List<Season> list = new ArrayList<>();
        ResultSet rs = DatabaseConnection.getInstance().createStatement()
                .executeQuery("SELECT * FROM seasons");
        while (rs.next())
            list.add(new Season(rs.getInt("id"), rs.getInt("season_number"), rs.getInt("series_id")));
        return list;
    }
}