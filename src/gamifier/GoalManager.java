package gamifier;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Manages the array of goals and handles file I/O operations.
 * Uses plain Java arrays with a separate counter (no Collections).
 */
public class GoalManager {
    private static final int MAX_GOALS = 50;
    private Goal[] goals;
    private int goalCount;
    private static final String DATA_FILE = "goals.txt";

    public GoalManager() {
        goals = new Goal[MAX_GOALS];
        goalCount = 0;
        loadGoals();
    }

    /**
     * Add a goal to the array.
     * @param goal the goal to add
     * @throws IllegalStateException if the array is full
     */
    public void addGoal(Goal goal) {
        if (goalCount >= MAX_GOALS) {
            throw new IllegalStateException("Maximum number of goals reached (" + MAX_GOALS + ")");
        }
        goals[goalCount] = goal;
        goalCount++;
        saveGoals();
    }

    /**
     * Get a goal by index.
     * @param index the index of the goal
     * @return the goal at the specified index
     * @throws GoalNotFoundException if index is out of bounds
     */
    public Goal getGoal(int index) throws GoalNotFoundException {
        if (index < 0 || index >= goalCount) {
            throw new GoalNotFoundException("Goal not found at index: " + index);
        }
        return goals[index];
    }

    /**
     * Get a goal by name.
     * @param name the name of the goal to find
     * @return the goal with the matching name
     * @throws GoalNotFoundException if no goal with that name exists
     */
    public Goal getGoalByName(String name) throws GoalNotFoundException {
        for (int i = 0; i < goalCount; i++) {
            if (goals[i].getName().equalsIgnoreCase(name)) {
                return goals[i];
            }
        }
        throw new GoalNotFoundException("Goal not found: " + name);
    }

    /**
     * Get all goals as an array (copy).
     * @return array containing all goals
     */
    public Goal[] getAllGoals() {
        Goal[] result = new Goal[goalCount];
        for (int i = 0; i < goalCount; i++) {
            result[i] = goals[i];
        }
        return result;
    }

    /**
     * Get the number of goals.
     * @return the current goal count
     */
    public int getGoalCount() {
        return goalCount;
    }

    /**
     * Log a contribution to a goal.
     * @param index the index of the goal
     * @param amount the contribution amount
     * @throws GoalNotFoundException if index is invalid
     * @throws InvalidAmountException if amount is zero or negative
     */
    public void logContribution(int index, double amount) throws GoalNotFoundException, InvalidAmountException {
        if (amount <= 0) {
            throw new InvalidAmountException("Contribution amount must be greater than zero");
        }
        
        Goal goal = getGoal(index);
        goal.setCurrentAmount(goal.getCurrentAmount() + amount);
        
        // If it's a gamified goal, increment streak
        if (goal instanceof GamifiedGoal) {
            ((GamifiedGoal) goal).incrementStreak();
        }
        
        saveGoals();
    }

    /**
     * Remove a goal by index (shifts remaining goals).
     * @param index the index of the goal to remove
     * @throws GoalNotFoundException if index is invalid
     */
    public void removeGoal(int index) throws GoalNotFoundException {
        if (index < 0 || index >= goalCount) {
            throw new GoalNotFoundException("Goal not found at index: " + index);
        }
        
        // Shift all subsequent goals left
        for (int i = index; i < goalCount - 1; i++) {
            goals[i] = goals[i + 1];
        }
        goals[goalCount - 1] = null;
        goalCount--;
        saveGoals();
    }

    /**
     * Save all goals to a CSV text file.
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
     * Load goals from the CSV text file.
     */
    public void loadGoals() {
        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                
                String[] parts = line.split(",");
                if (parts.length < 4) continue;
                
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
            // File doesn't exist or can't be read - start fresh
            System.out.println("No existing goals file found. Starting fresh.");
        }
    }

    /**
     * Get goal names for dropdown/combobox.
     * @return array of goal names
     */
    public String[] getGoalNames() {
        String[] names = new String[goalCount];
        for (int i = 0; i < goalCount; i++) {
            names[i] = goals[i].getName();
        }
        return names;
    }
}