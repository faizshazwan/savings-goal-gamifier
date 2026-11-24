package gamifier;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Manages friendships using plain arrays.
 */
public class FriendshipManager {
    private static final int MAX_FRIENDSHIPS = 500;
    private Friendship[] friendships;
    private int friendCount;
    private static final String DATA_FILE = "friends.txt";

    public FriendshipManager() {
        friendships = new Friendship[MAX_FRIENDSHIPS];
        friendCount = 0;
        loadFriendships();
    }

    /**
     * Add a friendship between two users.
     * @param userId1 first user ID
     * @param userId2 second user ID
     * @throws IllegalArgumentException if users are the same or already friends
     */
    public void addFriendship(String userId1, String userId2) {
        if (userId1.equals(userId2)) {
            throw new IllegalArgumentException("Cannot add yourself as a friend");
        }

        // Check if already friends
        for (int i = 0; i < friendCount; i++) {
            Friendship f = friendships[i];
            if ((f.getUserId1().equals(userId1) && f.getUserId2().equals(userId2)) ||
                (f.getUserId1().equals(userId2) && f.getUserId2().equals(userId1))) {
                throw new IllegalArgumentException("Already friends");
            }
        }

        if (friendCount >= MAX_FRIENDSHIPS) {
            throw new IllegalStateException("Maximum number of friendships reached");
        }

        friendships[friendCount] = new Friendship(userId1, userId2);
        friendCount++;
        saveFriendships();
    }

    /**
     * Get all friends of a user.
     * @param userId the user ID
     * @return array of friend user IDs
     */
    public String[] getFriends(String userId) {
        String[] friendIds = new String[friendCount];
        int count = 0;
        for (int i = 0; i < friendCount; i++) {
            if (friendships[i].involves(userId)) {
                friendIds[count] = friendships[i].getOtherUserId(userId);
                count++;
            }
        }
        // Trim array
        String[] result = new String[count];
        for (int i = 0; i < count; i++) {
            result[i] = friendIds[i];
        }
        return result;
    }

    /**
     * Check if two users are friends.
     */
    public boolean areFriends(String userId1, String userId2) {
        for (int i = 0; i < friendCount; i++) {
            Friendship f = friendships[i];
            if ((f.getUserId1().equals(userId1) && f.getUserId2().equals(userId2)) ||
                (f.getUserId1().equals(userId2) && f.getUserId2().equals(userId1))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Save all friendships to CSV file.
     */
    public void saveFriendships() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_FILE))) {
            for (int i = 0; i < friendCount; i++) {
                writer.write(friendships[i].toCSV());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving friendships: " + e.getMessage());
        }
    }

    /**
     * Load friendships from CSV file.
     */
    public void loadFriendships() {
        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(",");
                if (parts.length < 2) continue;

                if (friendCount < MAX_FRIENDSHIPS) {
                    friendships[friendCount] = Friendship.fromCSV(parts);
                    friendCount++;
                }
            }
        } catch (IOException e) {
            System.out.println("No existing friends file found. Starting fresh.");
        }
    }
}