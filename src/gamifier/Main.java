package gamifier;

/**
 * Main entry point for the Savings Goal Gamifier application.
 * Launches the Swing GUI.
 */
public class Main {
    public static void main(String[] args) {
        // Set system look and feel
        try {
            javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Use default look and feel if system one fails
        }
        
        // Run GUI on Event Dispatch Thread
        javax.swing.SwingUtilities.invokeLater(() -> {
            new SavingsGoalGamifierGUI().setVisible(true);
        });
    }
}