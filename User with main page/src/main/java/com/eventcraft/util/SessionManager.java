package com.eventcraft.util;

import com.eventcraft.model.User;
import java.io.*;
import java.time.LocalDateTime;

public class SessionManager {
    private static User currentUser;
    private static final String SESSION_FILE = "session.txt";

    public static void setUser(User user) {
        currentUser = user;
        saveSessionToFile();
    }

    public static User getUser() {
        if (currentUser == null) {
            loadSessionFromFile();
        }
        return currentUser;
    }

    public static String getUserRole() {
        return currentUser != null ? currentUser.getRole() : null;
    }

    public static boolean isAdmin() {
        boolean isAdmin = "admin".equalsIgnoreCase(getUserRole());
        System.out.println("Checking admin status: " + isAdmin);
        return isAdmin;
    }

    public static boolean isClient() {
        boolean isClient = "client".equalsIgnoreCase(getUserRole());
        System.out.println("Checking client status: " + isClient);
        return isClient;
    }

    public static void logout() {
        currentUser = null;
        clearSessionFile();
    }

    private static void saveSessionToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SESSION_FILE))) {
            if (currentUser != null) {
                writer.write("Username: " + currentUser.getNom() + " " + currentUser.getPrenom());
                writer.newLine();
                writer.write("Role: " + currentUser.getRole());
                writer.newLine();
                writer.write("Login Time: " + LocalDateTime.now().toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadSessionFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(SESSION_FILE))) {
            String line;
            String username = null;
            String role = null;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Username:")) {
                    username = line.substring("Username: ".length());
                } else if (line.startsWith("Role:")) {
                    role = line.substring("Role: ".length());
                }
            }
            if (username != null && role != null) {
                currentUser = new User();
                currentUser.setNom(username.split(" ")[0]);
                currentUser.setPrenom(username.split(" ")[1]);
                currentUser.setRole(role);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void clearSessionFile() {
        try (PrintWriter writer = new PrintWriter(SESSION_FILE)) {
            writer.print("");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    // Method to set the current user (e.g., after login)
    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    // Method to get the current user
    public static User getCurrentUser() {
        return currentUser;
    }

}
