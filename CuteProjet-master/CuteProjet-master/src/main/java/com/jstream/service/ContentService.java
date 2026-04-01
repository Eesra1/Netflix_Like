package com.jstream.service;

import com.jstream.dao.FilmDAO;
import com.jstream.dao.SeriesDAO;
import com.jstream.model.Film;
import com.jstream.model.Series;
import java.util.List;

public class ContentService {

    FilmDAO filmDAO = new FilmDAO();
    SeriesDAO seriesDAO = new SeriesDAO();

    public List<Film> getAllFilms() throws Exception {
        return filmDAO.findAll();
    }

    public List<Film> getFilmsByCategory(int categoryId) throws Exception {
        return filmDAO.findByCategory(categoryId);
    }

    public List<Series> getAllSeries() throws Exception {
        return seriesDAO.findAll();
    }

    public void addFilm(Film f) throws Exception {
        filmDAO.add(f);
    }

    public void deleteFilm(int id) throws Exception {
        filmDAO.delete(id);
    }
}