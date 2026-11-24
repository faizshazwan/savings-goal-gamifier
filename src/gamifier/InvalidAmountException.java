package gamifier;

/**
 * Custom exception thrown when a user tries to contribute $0 or a negative amount.
 */
public class InvalidAmountException extends Exception {
    public InvalidAmountException(String message) {
        super(message);
    }
}