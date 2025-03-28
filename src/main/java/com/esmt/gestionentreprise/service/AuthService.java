package com.esmt.gestionentreprise.service;

import com.esmt.gestionentreprise.entities.Role;
import com.esmt.gestionentreprise.entities.User;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.mindrot.jbcrypt.BCrypt;

@Stateless
public class AuthService {
    @PersistenceContext
    private EntityManager em;

    public User findByEmail(String email) {
        TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class);
        query.setParameter("email", email);
        return query.getResultStream().findFirst().orElse(null);
    }

    public User authenticate(String email, String password) {
        User user = findByEmail(email);
        if (user != null && verifyPassword(password, user.getPassword())) {
            return user;
        }
        return null;
    }

    public User register(String email, String password) {
        User existingUser = findByEmail(email);
        if (existingUser != null) {
            return null;
        }
        User user = new User(email, hashPassword(password), Role.EMPLOYE);
        em.persist(user);
        return user;
    }

    public String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(12));
    }

    private boolean verifyPassword(String enteredPassword, String storedHashedPassword) {
        return BCrypt.checkpw(enteredPassword, storedHashedPassword);
    }
}
