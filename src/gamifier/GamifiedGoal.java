package gamifier;

/**
 * Gamified savings goal with streak tracking and badge earning.
 */
public class GamifiedGoal extends Goal {
    private int currentStreak;
    private int badgesEarned;

    public GamifiedGoal(String name, double targetAmount, double currentAmount, int currentStreak, int badgesEarned) {
        super(name, targetAmount, currentAmount);
        this.currentStreak = currentStreak;
        this.badgesEarned = badgesEarned;
    }

    // Constructor for backward compatibility (when loading old data without streak/badges)
    public GamifiedGoal(String name, double targetAmount, double currentAmount) {
        this(name, targetAmount, currentAmount, 0, 0);
    }

    public int getCurrentStreak() {
        return currentStreak;
    }

    public void setCurrentStreak(int currentStreak) {
        this.currentStreak = currentStreak;
    }

    public int getBadgesEarned() {
        return badgesEarned;
    }

    public void setBadgesEarned(int badgesEarned) {
        this.badgesEarned = badgesEarned;
    }

    /**
     * Increment streak when a contribution is made.
     * Awards badges based on streak milestones.
     */
    public void incrementStreak() {
        currentStreak++;
        // Award badges at milestones
        if (currentStreak == 7 || currentStreak == 30 || currentStreak == 90) {
            badgesEarned++;
        }
    }

    /**
     * Reset streak (e.g., if user misses a day).
     */
    public void resetStreak() {
        currentStreak = 0;
    }

    @Override
    public double calculateProgress() {
        if (targetAmount <= 0) {
            return 0.0;
        }
        double progress = (currentAmount / targetAmount) * 100.0;
        return Math.min(progress, 100.0);
    }

    @Override
    public String getGoalType() {
        return "Gamified";
    }

    @Override
    public String toCSV() {
        return String.format("%s,%s,%.2f,%.2f,%d,%d", getGoalType(), name, targetAmount, currentAmount, currentStreak, badgesEarned);
    }

    @Override
    public String toString() {
        return String.format("%s: $%.2f / $%.2f (%.1f%%) | Streak: %d days | Badges: %d", 
                name, currentAmount, targetAmount, calculateProgress(), currentStreak, badgesEarned);
    }

    /**
     * Create a GamifiedGoal from CSV parts.
     * Expected format: Gamified,name,targetAmount,currentAmount,currentStreak,badgesEarned
     * Also handles legacy format without streak/badges.
     */
    public static GamifiedGoal fromCSV(String[] parts) {
        String name = parts[1];
        double targetAmount = Double.parseDouble(parts[2]);
        double currentAmount = Double.parseDouble(parts[3]);
        
        int currentStreak = 0;
        int badgesEarned = 0;
        
        if (parts.length >= 5) {
            currentStreak = Integer.parseInt(parts[4]);
        }
        if (parts.length >= 6) {
            badgesEarned = Integer.parseInt(parts[5]);
        }
        
        return new GamifiedGoal(name, targetAmount, currentAmount, currentStreak, badgesEarned);
    }
}