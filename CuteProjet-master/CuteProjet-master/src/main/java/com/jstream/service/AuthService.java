package com.jstream.service;

import com.jstream.dao.UserDAO;
import com.jstream.model.User;
import org.mindrot.jbcrypt.BCrypt;

public class AuthService {

    UserDAO userDAO = new UserDAO();

    public boolean register(String name, String email, String password) throws Exception {
        if (userDAO.findByEmail(email) != null)
            return false;
        String hash = BCrypt.hashpw(password, BCrypt.gensalt());
        userDAO.add(new User(0, name, email, hash, "USER"));
        return true;
    }

    public User login(String email, String password) throws Exception {
        User u = userDAO.findByEmail(email);
        if (u != null && BCrypt.checkpw(password, u.getPasswordHash()))
            return u;
        return null;
    }
}