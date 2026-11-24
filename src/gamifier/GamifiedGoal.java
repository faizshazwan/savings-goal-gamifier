package gamifier;

/**
 * Gamified savings goal with streak tracking, XP earning, and badge earning.
 */
public class GamifiedGoal extends Goal {
    private int currentStreak;
    private int totalXP;

    public GamifiedGoal(String goalId, String userId, String name, double targetAmount, double currentAmount, int currentStreak, int totalXP) {
        super(goalId, userId, name, targetAmount, currentAmount);
        this.currentStreak = currentStreak;
        this.totalXP = totalXP;
    }

    // Constructor for new goals (without streak/XP)
    public GamifiedGoal(String goalId, String userId, String name, double targetAmount, double currentAmount) {
        this(goalId, userId, name, targetAmount, currentAmount, 0, 0);
    }

    public int getCurrentStreak() {
        return currentStreak;
    }

    public void setCurrentStreak(int currentStreak) {
        this.currentStreak = currentStreak;
    }

    public int getTotalXP() {
        return totalXP;
    }

    public void setTotalXP(int totalXP) {
        this.totalXP = totalXP;
    }

    /**
     * Add a contribution to this goal.
     * Increases current amount, streak, and XP.
     * @param amount the contribution amount
     * @return XP earned from this contribution
     */
    public int addContribution(double amount) {
        // Add to current amount
        this.currentAmount += amount;
        
        // Increment streak
        this.currentStreak++;
        
        // Calculate XP: base 10 XP + streak bonus (1 XP per day of streak) + progress bonus
        int baseXP = 10;
        int streakBonus = currentStreak;
        int progressBonus = (int) (calculateProgress() / 10); // 1 XP per 10% progress
        
        int xpEarned = baseXP + streakBonus + progressBonus;
        this.totalXP += xpEarned;
        
        return xpEarned;
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
        return String.format("%s,%s,%s,%s,%.2f,%.2f,%d,%d", getGoalType(), goalId, userId, name, targetAmount, currentAmount, currentStreak, totalXP);
    }

    @Override
    public String toString() {
        return String.format("%s: $%.2f / $%.2f (%.1f%%) | Streak: %d | XP: %d", 
                name, currentAmount, targetAmount, calculateProgress(), currentStreak, totalXP);
    }

    /**
     * Create a GamifiedGoal from CSV parts.
     * Expected format: Gamified,goalId,userId,name,targetAmount,currentAmount,currentStreak,totalXP
     */
    public static GamifiedGoal fromCSV(String[] parts) {
        String goalId = parts[1];
        String userId = parts[2];
        String name = parts[3];
        double targetAmount = Double.parseDouble(parts[4]);
        double currentAmount = Double.parseDouble(parts[5]);
        
        int currentStreak = 0;
        int totalXP = 0;
        
        if (parts.length >= 7) {
            currentStreak = Integer.parseInt(parts[6]);
        }
        if (parts.length >= 8) {
            totalXP = Integer.parseInt(parts[7]);
        }
        
        return new GamifiedGoal(goalId, userId, name, targetAmount, currentAmount, currentStreak, totalXP);
    }
}