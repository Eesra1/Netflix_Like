package com.jstream.service;

import com.jstream.dao.*;
import com.jstream.model.*;

import java.util.List;

public class AdminService {

    AdminDAO adminDAO = new AdminDAO();
    FilmDAO filmDAO = new FilmDAO();
    SeriesDAO seriesDAO = new SeriesDAO();
    SeasonDAO seasonDAO = new SeasonDAO();
    EpisodeDAO episodeDAO = new EpisodeDAO();
    CategoryDAO categoryDAO = new CategoryDAO();
    CommentDAO commentDAO = new CommentDAO();

    public Admin login(String email, String password) throws Exception {
        Admin admin = adminDAO.findByEmail(email);
        if (admin != null) {
            if (password.equals(admin.getPassword())) {
                return admin;
            }
            try {
                if (org.mindrot.jbcrypt.BCrypt.checkpw(password, admin.getPassword())) {
                    return admin;
                }
            } catch (Exception e) {
            }
        }
        return null;
    }

    public void addFilm(Film f) throws Exception { filmDAO.add(f); }
    public void updateFilm(Film f) throws Exception { filmDAO.update(f); }
    public void deleteFilm(int id) throws Exception { filmDAO.delete(id); }

    public void addSeries(Series s) throws Exception { seriesDAO.add(s); }
    public void updateSeries(Series s) throws Exception { seriesDAO.update(s); }
    public void deleteSeries(int id) throws Exception { seriesDAO.delete(id); }

    public void addSeason(Season s) throws Exception { seasonDAO.add(s); }
    public void addEpisode(Episode e) throws Exception { episodeDAO.add(e); }

    public void addCategory(Category c) throws Exception { categoryDAO.add(c); }
    public void updateCategory(Category c) throws Exception { categoryDAO.update(c); }
    public void deleteCategory(int id) throws Exception { categoryDAO.delete(id); }

    public void deleteComment(int id) throws Exception { commentDAO.delete(id); }
    public List<Comment> getFlaggedComments() throws Exception { return commentDAO.getFlagged(); }

    public List<Category> getAllCategories() throws Exception { return categoryDAO.findAll(); }
    public List<Series> getAllSeries() throws Exception { return seriesDAO.findAll(); }
    public List<Season> getAllSeasons() throws Exception { return seasonDAO.findAll(); }
    public List<Season> getSeasonsBySeries(int seriesId) throws Exception { return seasonDAO.findBySeries(seriesId); }
}