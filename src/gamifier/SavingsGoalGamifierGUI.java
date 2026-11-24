package gamifier;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Main GUI window for the Savings Goal Gamifier application.
 * Built with Java Swing.
 */
public class SavingsGoalGamifierGUI extends JFrame {
    private GoalManager goalManager;
    private JTable goalsTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> goalSelector;
    private JTextField contributionField;

    public SavingsGoalGamifierGUI() {
        goalManager = new GoalManager();
        initializeUI();
        refreshTable();
    }

    private void initializeUI() {
        setTitle("Savings Goal Gamifier");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Top panel - Title
        JLabel titleLabel = new JLabel("Savings Goal Gamifier", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        // Center panel - Goals Table
        String[] columnNames = {"Name", "Type", "Target", "Current", "Progress", "Streak", "Badges"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        
        goalsTable = new JTable(tableModel);
        goalsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        goalsTable.setRowHeight(25);
        goalsTable.getTableHeader().setReorderingAllowed(false);
        
        JScrollPane scrollPane = new JScrollPane(goalsTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Your Goals"));
        add(scrollPane, BorderLayout.CENTER);

        // Bottom panel - Controls
        JPanel controlPanel = new JPanel(new BorderLayout(10, 10));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Left side - Add Goal buttons
        JPanel addGoalPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        addGoalPanel.setBorder(BorderFactory.createTitledBorder("Add New Goal"));
        
        JButton addStandardBtn = new JButton("Add Standard Goal");
        addStandardBtn.addActionListener(e -> showAddGoalDialog(false));
        addGoalPanel.add(addStandardBtn);
        
        JButton addGamifiedBtn = new JButton("Add Gamified Goal");
        addGamifiedBtn.addActionListener(e -> showAddGoalDialog(true));
        addGoalPanel.add(addGamifiedBtn);
        
        JButton removeBtn = new JButton("Remove Selected Goal");
        removeBtn.addActionListener(e -> removeSelectedGoal());
        addGoalPanel.add(removeBtn);
        
        controlPanel.add(addGoalPanel, BorderLayout.WEST);

        // Right side - Contribution panel
        JPanel contributionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        contributionPanel.setBorder(BorderFactory.createTitledBorder("Log Contribution"));
        
        contributionPanel.add(new JLabel("Goal:"));
        goalSelector = new JComboBox<>();
        goalSelector.setPreferredSize(new Dimension(150, 25));
        contributionPanel.add(goalSelector);
        
        contributionPanel.add(new JLabel("Amount ($):"));
        contributionField = new JTextField(10);
        contributionPanel.add(contributionField);
        
        JButton contributeBtn = new JButton("Add Contribution");
        contributeBtn.addActionListener(e -> logContribution());
        contributionPanel.add(contributeBtn);
        
        controlPanel.add(contributionPanel, BorderLayout.EAST);

        add(controlPanel, BorderLayout.SOUTH);

        // Update goal selector
        updateGoalSelector();
    }

    private void showAddGoalDialog(boolean isGamified) {
        JDialog dialog = new JDialog(this, isGamified ? "Add Gamified Goal" : "Add Standard Goal", true);
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Name field
        gbc.gridx = 0; gbc.gridy = 0;
        dialog.add(new JLabel("Goal Name:"), gbc);
        gbc.gridx = 1;
        JTextField nameField = new JTextField(20);
        dialog.add(nameField, gbc);
        
        // Target Amount field
        gbc.gridx = 0; gbc.gridy = 1;
        dialog.add(new JLabel("Target Amount ($):"), gbc);
        gbc.gridx = 1;
        JTextField targetField = new JTextField(20);
        dialog.add(targetField, gbc);
        
        // Initial Amount field (optional)
        gbc.gridx = 0; gbc.gridy = 2;
        dialog.add(new JLabel("Initial Amount ($):"), gbc);
        gbc.gridx = 1;
        JTextField initialField = new JTextField("0", 20);
        dialog.add(initialField, gbc);
        
        // Buttons
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        
        JButton saveBtn = new JButton("Save");
        saveBtn.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please enter a goal name", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                double target = Double.parseDouble(targetField.getText().trim());
                if (target <= 0) {
                    JOptionPane.showMessageDialog(dialog, "Target amount must be greater than zero", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                double initial = Double.parseDouble(initialField.getText().trim());
                if (initial < 0) {
                    JOptionPane.showMessageDialog(dialog, "Initial amount cannot be negative", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                Goal goal;
                if (isGamified) {
                    goal = new GamifiedGoal(name, target, initial);
                } else {
                    goal = new StandardGoal(name, target, initial);
                }
                
                goalManager.addGoal(goal);
                refreshTable();
                updateGoalSelector();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Goal added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter valid numbers", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalStateException ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        buttonPanel.add(saveBtn);
        
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(e -> dialog.dispose());
        buttonPanel.add(cancelBtn);
        
        dialog.add(buttonPanel, gbc);
        dialog.setVisible(true);
    }

    private void logContribution() {
        int selectedIndex = goalSelector.getSelectedIndex();
        if (selectedIndex < 0) {
            JOptionPane.showMessageDialog(this, "Please select a goal", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String amountText = contributionField.getText().trim();
        if (amountText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter an amount", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            double amount = Double.parseDouble(amountText);
            goalManager.logContribution(selectedIndex, amount);
            refreshTable();
            contributionField.setText("");
            JOptionPane.showMessageDialog(this, "Contribution logged successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (InvalidAmountException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Invalid Amount", JOptionPane.ERROR_MESSAGE);
        } catch (GoalNotFoundException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void removeSelectedGoal() {
        int selectedRow = goalsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a goal to remove", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to remove this goal?", 
            "Confirm Removal", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                goalManager.removeGoal(selectedRow);
                refreshTable();
                updateGoalSelector();
                JOptionPane.showMessageDialog(this, "Goal removed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (GoalNotFoundException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        Goal[] goals = goalManager.getAllGoals();
        for (Goal goal : goals) {
            String type = goal.getGoalType();
            String streak = "";
            String badges = "";
            
            if (goal instanceof GamifiedGoal) {
                GamifiedGoal gg = (GamifiedGoal) goal;
                streak = String.valueOf(gg.getCurrentStreak());
                badges = String.valueOf(gg.getBadgesEarned());
            }
            
            tableModel.addRow(new Object[]{
                goal.getName(),
                type,
                String.format("$%.2f", goal.getTargetAmount()),
                String.format("$%.2f", goal.getCurrentAmount()),
                String.format("%.1f%%", goal.calculateProgress()),
                streak,
                badges
            });
        }
    }

    private void updateGoalSelector() {
        goalSelector.removeAllItems();
        String[] names = goalManager.getGoalNames();
        for (String name : names) {
            goalSelector.addItem(name);
        }
    }
}