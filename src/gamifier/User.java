package gamifier;

/**
 * User model class representing a registered user.
 */
public class User {
    private String userId;
    private String username;
    private String password;
    private String displayName;
    private int totalXP;

    public User(String userId, String username, String password, String displayName, int totalXP) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.displayName = displayName;
        this.totalXP = totalXP;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public int getTotalXP() {
        return totalXP;
    }

    public void setTotalXP(int totalXP) {
        this.totalXP = totalXP;
    }

    public void addXP(int xp) {
        this.totalXP += xp;
    }

    /**
     * Convert user to CSV string for file storage.
     * Format: U001,faiz,pass123,Faiz,150
     */
    public String toCSV() {
        return String.format("%s,%s,%s,%s,%d", userId, username, password, displayName, totalXP);
    }

    /**
     * Create a User from CSV parts.
     * Expected format: userId,username,password,displayName,totalXP
     */
    public static User fromCSV(String[] parts) {
        String userId = parts[0];
        String username = parts[1];
        String password = parts[2];
        String displayName = parts[3];
        int totalXP = Integer.parseInt(parts[4]);
        return new User(userId, username, password, displayName, totalXP);
    }

    @Override
    public String toString() {
        return String.format("%s (%s) - XP: %d", displayName, username, totalXP);
    }
}