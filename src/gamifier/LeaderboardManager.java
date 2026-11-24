package gamifier;

/**
 * Manages leaderboard functionality with manual sorting (no Arrays.sort).
 */
public class LeaderboardManager {
    private UserManager userManager;
    private FriendshipManager friendshipManager;

    public LeaderboardManager(UserManager userManager, FriendshipManager friendshipManager) {
        this.userManager = userManager;
        this.friendshipManager = friendshipManager;
    }

    /**
     * Get leaderboard data for a user and their friends.
     * Returns a 2D Object array for JTable: [rank, displayName, username, totalXP]
     * 
     * @param userId the logged-in user's ID
     * @return Object[][] for JTable
     */
    public Object[][] getLeaderboardData(String userId) {
        // Step 1: Get friend IDs
        String[] friendIds = friendshipManager.getFriends(userId);
        
        // Step 2: Calculate total number of users (user + friends)
        int totalUsers = 1 + friendIds.length;
        
        // Step 3: Create User array with all participants
        User[] participants = new User[totalUsers];
        
        // Add the logged-in user
        User currentUser = userManager.getUserById(userId);
        if (currentUser != null) {
            participants[0] = currentUser;
        }
        
        // Add friends
        for (int i = 0; i < friendIds.length; i++) {
            User friend = userManager.getUserById(friendIds[i]);
            if (friend != null) {
                participants[i + 1] = friend;
            }
        }
        
        // Step 4: Manual Bubble Sort by totalXP (descending - highest first)
        bubbleSortByXPDescending(participants);
        
        // Step 5: Convert to Object[][] for JTable
        Object[][] tableData = new Object[totalUsers][4];
        for (int i = 0; i < totalUsers; i++) {
            User u = participants[i];
            if (u != null) {
                tableData[i][0] = i + 1; // Rank
                tableData[i][1] = u.getDisplayName();
                tableData[i][2] = u.getUsername();
                tableData[i][3] = u.getTotalXP();
            }
        }
        
        return tableData;
    }

    /**
     * Bubble Sort implementation to sort User array by totalXP descending.
     * This is a manual sorting algorithm - no Arrays.sort() used.
     */
    private void bubbleSortByXPDescending(User[] arr) {
        int n = arr.length;
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                // Handle null elements (push them to the end)
                if (arr[j] == null) {
                    // Swap null to the right
                    User temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                } else if (arr[j + 1] != null && arr[j].getTotalXP() < arr[j + 1].getTotalXP()) {
                    // Swap if current has less XP than next
                    User temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                }
            }
        }
    }

    /**
     * Get column names for the leaderboard table.
     */
    public String[] getColumnNames() {
        return new String[]{"Rank", "Display Name", "Username", "Total XP"};
    }
}