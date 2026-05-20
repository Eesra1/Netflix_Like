package com.jstream.service;

import com.jstream.dao.UserDAO;
import com.jstream.model.User;
import org.mindrot.jbcrypt.BCrypt;

public class AuthService {

    UserDAO userDAO = new UserDAO();

    public boolean register(String name, String email, String password) throws Exception {
        if (!email.contains("@") || !email.contains("."))
            throw new Exception("Email invalide");
        if (name.isEmpty() || email.isEmpty() || password.isEmpty())
            throw new Exception("Champs vides");
        if (userDAO.findByEmail(email) != null)
            throw new Exception("Email déjà utilisé");
        String hash = BCrypt.hashpw(password, BCrypt.gensalt());
        userDAO.add(new User(0, name, email, hash, "USER"));
        return true;
    }

    public User login(String email, String password) throws Exception {
        User u = userDAO.findByEmail(email);
        if (u == null)
            throw new Exception("Email introuvable");
        if (!BCrypt.checkpw(password, u.getPasswordHash()))
            throw new Exception("Mot de passe incorrect");
        return u;
    }
}