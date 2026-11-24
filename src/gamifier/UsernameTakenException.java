package gamifier;

/**
 * Exception thrown during registration if the username already exists.
 */
public class UsernameTakenException extends Exception {
    public UsernameTakenException(String message) {
        super(message);
    }
}