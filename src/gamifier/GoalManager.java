package gamifier;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Manages goals using plain arrays.
 */
public class GoalManager {
    private static final int MAX_GOALS = 200;
    private Goal[] goals;
    private int goalCount;
    private static final String DATA_FILE = "goals.txt";

    public GoalManager() {
        goals = new Goal[MAX_GOALS];
        goalCount = 0;
        loadGoals();
    }

    /**
     * Add a goal.
     * @param goal the goal to add
     * @throws IllegalStateException if max goals reached
     */
    public void addGoal(Goal goal) {
        if (goalCount >= MAX_GOALS) {
            throw new IllegalStateException("Maximum number of goals reached");
        }
        goals[goalCount] = goal;
        goalCount++;
        saveGoals();
    }

    /**
     * Get a goal by ID.
     * @param goalId the goal ID
     * @return the goal or null if not found
     */
    public Goal getGoalById(String goalId) {
        for (int i = 0; i < goalCount; i++) {
            if (goals[i].getGoalId().equals(goalId)) {
                return goals[i];
            }
        }
        return null;
    }

    /**
     * Get all goals for a specific user.
     * @param userId the user ID
     * @return array of user's goals
     */
    public Goal[] getGoalsByUser(String userId) {
        Goal[] userGoals = new Goal[goalCount];
        int count = 0;
        for (int i = 0; i < goalCount; i++) {
            if (goals[i].getUserId().equals(userId)) {
                userGoals[count] = goals[i];
                count++;
            }
        }
        Goal[] result = new Goal[count];
        for (int i = 0; i < count; i++) {
            result[i] = userGoals[i];
        }
        return result;
    }

    /**
     * Get all goals.
     * @return array of all goals
     */
    public Goal[] getAllGoals() {
        Goal[] result = new Goal[goalCount];
        for (int i = 0; i < goalCount; i++) {
            result[i] = goals[i];
        }
        return result;
    }

    /**
     * Log a contribution to a goal.
     * @param goalId the goal ID
     * @param amount the contribution amount
     * @param userManager the user manager to update XP
     * @return XP earned from contribution
     * @throws GoalNotFoundException if goal not found
     * @throws InvalidAmountException if amount <= 0
     */
    public int logContribution(String goalId, double amount, UserManager userManager) throws GoalNotFoundException, InvalidAmountException {
        if (amount <= 0) {
            throw new InvalidAmountException("Contribution amount must be greater than zero");
        }

        Goal goal = getGoalById(goalId);
        if (goal == null) {
            throw new GoalNotFoundException("Goal not found: " + goalId);
        }

        int xpEarned = 0;
        if (goal instanceof GamifiedGoal) {
            GamifiedGoal gamifiedGoal = (GamifiedGoal) goal;
            xpEarned = gamifiedGoal.addContribution(amount);
            
            // Update user's total XP
            User user = userManager.getUserById(goal.getUserId());
            if (user != null) {
                user.addXP(xpEarned);
                userManager.updateUser(user);
            }
        } else {
            goal.setCurrentAmount(goal.getCurrentAmount() + amount);
        }

        saveGoals();
        return xpEarned;
    }

    /**
     * Delete a goal.
     * @param goalId the goal ID to delete
     * @throws GoalNotFoundException if goal not found
     */
    public void deleteGoal(String goalId) throws GoalNotFoundException {
        int index = -1;
        for (int i = 0; i < goalCount; i++) {
            if (goals[i].getGoalId().equals(goalId)) {
                index = i;
                break;
            }
        }
        if (index == -1) {
            throw new GoalNotFoundException("Goal not found: " + goalId);
        }

        // Shift array
        for (int i = index; i < goalCount - 1; i++) {
            goals[i] = goals[i + 1];
        }
        goals[goalCount - 1] = null;
        goalCount--;
        saveGoals();
    }

    /**
     * Generate a unique goal ID.
     */
    public String generateGoalId() {
        int maxId = 0;
        for (int i = 0; i < goalCount; i++) {
            String id = goals[i].getGoalId();
            if (id.startsWith("G")) {
                try {
                    int num = Integer.parseInt(id.substring(1));
                    if (num > maxId) maxId = num;
                } catch (NumberFormatException e) {
                    // ignore
                }
            }
        }
        return String.format("G%03d", maxId + 1);
    }

    /**
     * Save all goals to CSV file.
     */
    public void saveGoals() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_FILE))) {
            for (int i = 0; i < goalCount; i++) {
                writer.write(goals[i].toCSV());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving goals: " + e.getMessage());
        }
    }

    /**
     * Load goals from CSV file.
     */
    public void loadGoals() {
        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(",");
                if (parts.length < 6) continue;

                String type = parts[0];
                Goal goal = null;

                if ("Standard".equals(type)) {
                    goal = StandardGoal.fromCSV(parts);
                } else if ("Gamified".equals(type)) {
                    goal = GamifiedGoal.fromCSV(parts);
                }

                if (goal != null && goalCount < MAX_GOALS) {
                    goals[goalCount] = goal;
                    goalCount++;
                }
            }
        } catch (IOException e) {
            System.out.println("No existing goals file found. Starting fresh.");
        }
    }
}