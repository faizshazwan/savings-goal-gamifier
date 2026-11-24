package gamifier;

/**
 * Standard savings goal with basic progress calculation.
 */
public class StandardGoal extends Goal {

    public StandardGoal(String goalId, String userId, String name, double targetAmount, double currentAmount) {
        super(goalId, userId, name, targetAmount, currentAmount);
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
        return "Standard";
    }

    @Override
    public String toCSV() {
        return String.format("%s,%s,%s,%s,%.2f,%.2f", getGoalType(), goalId, userId, name, targetAmount, currentAmount);
    }

    /**
     * Create a StandardGoal from CSV parts.
     * Expected format: Standard,goalId,userId,name,targetAmount,currentAmount
     */
    public static StandardGoal fromCSV(String[] parts) {
        String goalId = parts[1];
        String userId = parts[2];
        String name = parts[3];
        double targetAmount = Double.parseDouble(parts[4]);
        double currentAmount = Double.parseDouble(parts[5]);
        return new StandardGoal(goalId, userId, name, targetAmount, currentAmount);
    }
}