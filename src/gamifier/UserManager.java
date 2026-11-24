package gamifier;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Manages users using plain arrays (no Collections).
 */
public class UserManager {
    private static final int MAX_USERS = 100;
    private User[] users;
    private int userCount;
    private static final String DATA_FILE = "users.txt";

    public UserManager() {
        users = new User[MAX_USERS];
        userCount = 0;
        loadUsers();
    }

    /**
     * Register a new user.
     * @param username the username
     * @param password the password
     * @param displayName the display name
     * @return the newly created user
     * @throws UsernameTakenException if username already exists
     * @throws IllegalStateException if max users reached
     */
    public User register(String username, String password, String displayName) throws UsernameTakenException {
        // Check if username exists
        for (int i = 0; i < userCount; i++) {
            if (users[i].getUsername().equals(username)) {
                throw new UsernameTakenException("Username '" + username + "' is already taken");
            }
        }

        if (userCount >= MAX_USERS) {
            throw new IllegalStateException("Maximum number of users reached");
        }

        String userId = generateUserId();
        User user = new User(userId, username, password, displayName, 0);
        users[userCount] = user;
        userCount++;
        saveUsers();
        return user;
    }

    /**
     * Login a user.
     * @param username the username
     * @param password the password
     * @return the user if credentials match, null otherwise
     */
    public User login(String username, String password) {
        for (int i = 0; i < userCount; i++) {
            if (users[i].getUsername().equals(username) && users[i].getPassword().equals(password)) {
                return users[i];
            }
        }
        return null;
    }

    /**
     * Get a user by userId.
     * @param userId the user ID
     * @return the user or null if not found
     */
    public User getUserById(String userId) {
        for (int i = 0; i < userCount; i++) {
            if (users[i].getUserId().equals(userId)) {
                return users[i];
            }
        }
        return null;
    }

    /**
     * Get a user by username.
     * @param username the username
     * @return the user or null if not found
     */
    public User getUserByUsername(String username) {
        for (int i = 0; i < userCount; i++) {
            if (users[i].getUsername().equals(username)) {
                return users[i];
            }
        }
        return null;
    }

    /**
     * Update a user's data (e.g., XP).
     * @param user the user to update
     */
    public void updateUser(User user) {
        for (int i = 0; i < userCount; i++) {
            if (users[i].getUserId().equals(user.getUserId())) {
                users[i] = user;
                saveUsers();
                return;
            }
        }
    }

    /**
     * Get all users.
     * @return array of all users
     */
    public User[] getAllUsers() {
        User[] result = new User[userCount];
        for (int i = 0; i < userCount; i++) {
            result[i] = users[i];
        }
        return result;
    }

    /**
     * Get user count.
     * @return number of users
     */
    public int getUserCount() {
        return userCount;
    }

    /**
     * Generate a unique user ID.
     */
    private String generateUserId() {
        int maxId = 0;
        for (int i = 0; i < userCount; i++) {
            String id = users[i].getUserId();
            if (id.startsWith("U")) {
                try {
                    int num = Integer.parseInt(id.substring(1));
                    if (num > maxId) maxId = num;
                } catch (NumberFormatException e) {
                    // ignore
                }
            }
        }
        return String.format("U%03d", maxId + 1);
    }

    /**
     * Save all users to CSV file.
     */
    public void saveUsers() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_FILE))) {
            for (int i = 0; i < userCount; i++) {
                writer.write(users[i].toCSV());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving users: " + e.getMessage());
        }
    }

    /**
     * Load users from CSV file.
     */
    public void loadUsers() {
        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(",");
                if (parts.length < 5) continue;

                User user = User.fromCSV(parts);
                if (userCount < MAX_USERS) {
                    users[userCount] = user;
                    userCount++;
                }
            }
        } catch (IOException e) {
            System.out.println("No existing users file found. Starting fresh.");
        }
    }
}