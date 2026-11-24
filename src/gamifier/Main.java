package gamifier;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Savings Goal Gamifier");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);

        JLabel label = new JLabel("Welcome to the Gamifier!", SwingConstants.CENTER);
        frame.add(label);

        frame.setVisible(true);
    }
}