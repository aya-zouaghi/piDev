package com.eventcraft.util;

import java.util.Stack;

public class NavigationHistory {
    private static Stack<String> backStack = new Stack<>();
    private static Stack<String> forwardStack = new Stack<>();
    private static String currentPage;

    /**
     * Add a page to the navigation history
     * @param fxmlPath The path to the FXML file
     */
    public static void addPage(String fxmlPath) {
        // If we're on a page already, add it to the back stack
        if (currentPage != null) {
            backStack.push(currentPage);
        }

        // Clear forward stack when directly navigating to a new page
        forwardStack.clear();

        // Update current page
        currentPage = fxmlPath;
    }

    /**
     * Go back to the previous page
     * @return The FXML path of the previous page
     */
    public static String goBack() {
        if (!backStack.isEmpty()) {
            // Add current page to forward stack
            forwardStack.push(currentPage);

            // Pop and set new current page
            String previousPage = backStack.pop();
            currentPage = previousPage;

            return previousPage;
        }
        return null;
    }

    /**
     * Go forward to the next page
     * @return The FXML path of the next page
     */
    public static String goForward() {
        if (!forwardStack.isEmpty()) {
            // Add current page to back stack
            backStack.push(currentPage);

            // Pop and set new current page
            String nextPage = forwardStack.pop();
            currentPage = nextPage;

            return nextPage;
        }
        return null;
    }

    /**
     * Check if can go back
     * @return true if there are pages in the back stack
     */
    public static boolean canGoBack() {
        return !backStack.isEmpty();
    }

    /**
     * Check if can go forward
     * @return true if there are pages in the forward stack
     */
    public static boolean canGoForward() {
        return !forwardStack.isEmpty();
    }

    /**
     * Clear all navigation history
     */
    public static void clearHistory() {
        backStack.clear();
        forwardStack.clear();
        currentPage = null;
    }
}