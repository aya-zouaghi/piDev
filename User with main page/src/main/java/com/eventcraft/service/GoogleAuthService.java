package com.eventcraft.service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.oauth2.model.Userinfo;
import com.eventcraft.dao.UserDAO;
import com.eventcraft.model.User;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;

public class GoogleAuthService {
    private static final String APPLICATION_NAME = "EventCraft";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    // Path to your client_secret.json file
    private static final String CREDENTIALS_FILE_PATH = "src/main/resources/client_secret.json";

    // Scope for accessing user info
    private static final String USER_INFO_SCOPE = "https://www.googleapis.com/auth/userinfo.profile https://www.googleapis.com/auth/userinfo.email";

    private UserDAO userDAO = new UserDAO();

    /**
     * Creates an authorized Credential object.
     */
    public Credential getCredentials() throws IOException, GeneralSecurityException {
        // Load client secrets from file
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
                new InputStreamReader(new FileInputStream(CREDENTIALS_FILE_PATH)));

        // Build flow and trigger user authorization request
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, clientSecrets,
                Collections.singleton(USER_INFO_SCOPE))
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .setApprovalPrompt("force") // Force re-authentication
                .build();

        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }


    /**
     * Get user info and handle user registration
     */
    public User signInWithGoogle() throws IOException, GeneralSecurityException {
        // Build the OAuth client
        final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        Credential credential = getCredentials();

        // Create OAuth2 service for user info
        Oauth2 oauth2 = new Oauth2.Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();

        // Get user info
        Userinfo userInfo = oauth2.userinfo().get().execute();

        // Extract user details
        String email = userInfo.getEmail();
        String firstName = userInfo.getGivenName();
        String lastName = userInfo.getFamilyName();

        // Check if user already exists in the database
        User existingUser = UserDAO.getUserByEmail(email);

        if (existingUser != null) {
            // User already exists, just return the user
            return existingUser;
        } else {
            // Create a new user
            User newUser = new User(
                    lastName,               // nom
                    firstName,              // prenom
                    "GOOGLE_AUTH_USER",     // password (special marker for Google auth users)
                    "Active",               // statutCompte
                    "Client",                 // default role
                    email                   // email
            );

            // Save user to database
            boolean success = userDAO.insertUser(newUser);

            if (success) {
                // Get the newly created user with ID populated
                return UserDAO.getUserByEmail(email);
            } else {
                throw new IOException("Failed to create user account");
            }
        }
    }
}