package gamifier;

/**
 * Abstract parent class for all savings goals.
 */
public abstract class Goal {
    protected String goalId;
    protected String userId;
    protected String name;
    protected double targetAmount;
    protected double currentAmount;

    public Goal(String goalId, String userId, String name, double targetAmount, double currentAmount) {
        this.goalId = goalId;
        this.userId = userId;
        this.name = name;
        this.targetAmount = targetAmount;
        this.currentAmount = currentAmount;
    }

    public String getGoalId() {
        return goalId;
    }

    public void setGoalId(String goalId) {
        this.goalId = goalId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getTargetAmount() {
        return targetAmount;
    }

    public void setTargetAmount(double targetAmount) {
        this.targetAmount = targetAmount;
    }

    public double getCurrentAmount() {
        return currentAmount;
    }

    public void setCurrentAmount(double currentAmount) {
        this.currentAmount = currentAmount;
    }

    /**
     * Abstract method to calculate progress percentage.
     * Must be implemented by subclasses.
     * 
     * @return progress as a percentage (0-100)
     */
    public abstract double calculateProgress();

    /**
     * Get the goal type for saving/loading.
     * @return "Standard" or "Gamified"
     */
    public abstract String getGoalType();

    /**
     * Convert goal to CSV string for file storage.
     */
    public String toCSV() {
        return String.format("%s,%s,%s,%.2f,%.2f", getGoalType(), goalId, userId, targetAmount, currentAmount);
    }

    @Override
    public String toString() {
        return String.format("%s: $%.2f / $%.2f (%.1f%%)", name, currentAmount, targetAmount, calculateProgress());
    }
}