package com.eventcraft.service;

import com.eventcraft.dao.UserDAO;
import com.eventcraft.model.User;

public class UserService {
    private UserDAO userDAO = new UserDAO();

    public boolean verifyPassword(User user, String currentPassword) {
        // Assuming the user object contains the hashed password
        return user.getPassword().equals(currentPassword);
    }

    public boolean changeUserPassword(User user, String newPassword) {
        return userDAO.updatePassword(user, newPassword);
    }
}
