package org.example.eventcraft.controllers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Sms {
    private static final String API_KEY = "66db1aaee1aee379faa15ede63b4f0de-eda82f70-a206-4544-882c-04efdbbd8ee6";
    private static final String BASE_URL = "https://api.infobip.com/sms/2/text/advanced";
    private static final String FROM_NUMBER = "+44 7491 163443"; // Your Infobip Virtual Number

    public void sendSms(String to, String messageContent) {
        try {
            // Debugging: Print the parameters
            System.out.println("Sending SMS with:");
            System.out.println("From: " + FROM_NUMBER);
            System.out.println("To: " + to);
            System.out.println("Message: " + messageContent);

            // Check if any of the fields are empty
            if (FROM_NUMBER.isEmpty() || to.isEmpty() || messageContent.isEmpty()) {
                System.out.println("Error: 'from', 'to', or 'text' is empty.");
                return;
            }

            // Create the URL object for Infobip API
            URL url = new URL(BASE_URL);

            // Open a connection
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "App " + API_KEY);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // Test minimal JSON payload
            String jsonPayload = "{"
                    + "\"messages\": [{"
                    + "\"from\": \"" + FROM_NUMBER + "\","
                    + "\"destinations\": [{"
                    + "\"to\": \"" + to + "\""
                    + "}],"
                    + "\"text\": \"" + messageContent + "\""
                    + "}]"
                    + "}";

            // Write the JSON payload to the output stream
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonPayload.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Get the response code
            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            // Read the response from the input stream
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            System.out.println("Response: " + response.toString());

            // Handle response based on status code
            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("Message sent successfully!");
            } else {
                System.out.println("Failed to send message. Response Code: " + responseCode);
                // Print out the error message from the error stream if necessary
                BufferedReader errorStream = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                StringBuilder errorResponse = new StringBuilder();
                String errorLine;
                while ((errorLine = errorStream.readLine()) != null) {
                    errorResponse.append(errorLine);
                }
                errorStream.close();
                System.out.println("Error response: " + errorResponse.toString());
            }

        } catch (Exception e) {
            System.err.println("Exception occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Main method for testing the SMS functionality
    public static void main(String[] args) {
        Sms sms = new Sms();
        sms.sendSms("+21629444051", "Hello, this is a test message from Infobip!");
    }
}
