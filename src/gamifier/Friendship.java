package gamifier;

/**
 * Represents a friendship between two users.
 */
public class Friendship {
    private String userId1;
    private String userId2;

    public Friendship(String userId1, String userId2) {
        this.userId1 = userId1;
        this.userId2 = userId2;
    }

    public String getUserId1() {
        return userId1;
    }

    public void setUserId1(String userId1) {
        this.userId1 = userId1;
    }

    public String getUserId2() {
        return userId2;
    }

    public void setUserId2(String userId2) {
        this.userId2 = userId2;
    }

    /**
     * Check if this friendship involves the given user ID.
     */
    public boolean involves(String userId) {
        return userId1.equals(userId) || userId2.equals(userId);
    }

    /**
     * Get the other user's ID in this friendship.
     */
    public String getOtherUserId(String userId) {
        if (userId1.equals(userId)) {
            return userId2;
        } else if (userId2.equals(userId)) {
            return userId1;
        }
        return null;
    }

    /**
     * Convert to CSV string.
     * Format: U001,U002
     */
    public String toCSV() {
        return userId1 + "," + userId2;
    }

    /**
     * Create from CSV parts.
     */
    public static Friendship fromCSV(String[] parts) {
        return new Friendship(parts[0], parts[1]);
    }
}