package gamifier;

/**
 * Custom exception thrown when a goal is not found.
 */
public class GoalNotFoundException extends Exception {
    public GoalNotFoundException(String message) {
        super(message);
    }
}