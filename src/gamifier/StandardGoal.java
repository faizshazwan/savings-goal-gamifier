package gamifier;

/**
 * Standard savings goal with basic progress calculation.
 */
public class StandardGoal extends Goal {

    public StandardGoal(String name, double targetAmount, double currentAmount) {
        super(name, targetAmount, currentAmount);
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
        return String.format("%s,%s,%.2f,%.2f", getGoalType(), name, targetAmount, currentAmount);
    }

    /**
     * Create a StandardGoal from CSV parts.
     * Expected format: Standard,name,targetAmount,currentAmount
     */
    public static StandardGoal fromCSV(String[] parts) {
        String name = parts[1];
        double targetAmount = Double.parseDouble(parts[2]);
        double currentAmount = Double.parseDouble(parts[3]);
        return new StandardGoal(name, targetAmount, currentAmount);
    }
}