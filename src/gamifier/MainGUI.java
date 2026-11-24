package gamifier;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Main GUI class using CardLayout for multiple screens.
 * Supports Dark/Light theme toggle via ThemeManager.
 * Top-right theme toggle, black text on white inputs/buttons.
 */
public class MainGUI extends JFrame implements ThemeManager.ThemeChangeListener {
    // Fixed sizes for consistent UI
    private static final int FIELD_WIDTH = 300;
    private static final int FIELD_HEIGHT = 40;
    private static final int BUTTON_WIDTH = 220;
    private static final int BUTTON_HEIGHT = 44;
    private static final Dimension FIELD_SIZE = new Dimension(FIELD_WIDTH, FIELD_HEIGHT);
    private static final Dimension BUTTON_SIZE = new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT);

    // Standard ASCII font for all tables
    private static final Font TABLE_FONT = new Font("SansSerif", Font.PLAIN, 14);
    private static final Font TABLE_HEADER_FONT = new Font("SansSerif", Font.BOLD, 14);
    private static final Font BUTTON_FONT = new Font("SansSerif", Font.BOLD, 14);
    private static final Font LABEL_FONT = new Font("SansSerif", Font.PLAIN, 14);

    // Managers
    private UserManager userManager;
    private FriendshipManager friendshipManager;
    private GoalManager goalManager;
    private LeaderboardManager leaderboardManager;
    private AIService aiService;
    private ThemeManager themeManager;

    // Current user
    private User currentUser;

    // CardLayout components
    private JPanel cardPanel;
    private CardLayout cardLayout;
    
    // Screen names
    private static final String LOGIN_SCREEN = "login";
    private static final String DASHBOARD_SCREEN = "dashboard";
    
    // UI Components - Login
    private JTextField loginUsernameField;
    private JPasswordField loginPasswordField;
    
    // UI Components - Register
    private JTextField registerUsernameField;
    private JPasswordField registerPasswordField;
    private JPasswordField registerConfirmPasswordField;
    private JTextField registerDisplayNameField;
    
    // UI Components - Dashboard
    private JLabel dashboardWelcomeLabel;
    private JLabel dashboardXPLabel;
    private JTable goalsTable;
    private DefaultTableModel goalsTableModel;
    
    // Theme toggle button (top-right)
    private JToggleButton themeToggleButton;
    
    // External windows
    private JFrame leaderboardFrame;
    private JFrame friendsFrame;

    public MainGUI() {
        // Initialize managers
        userManager = new UserManager();
        friendshipManager = new FriendshipManager();
        goalManager = new GoalManager();
        leaderboardManager = new LeaderboardManager(userManager, friendshipManager);
        aiService = new AIService();
        themeManager = ThemeManager.getInstance();
        themeManager.addThemeChangeListener(this);

        initializeUI();
    }

    private void initializeUI() {
        setTitle("Savings Goal Gamifier");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(900, 600));

        // Apply initial theme
        themeManager.applyThemeColors(false);
        
        // Main panel with BorderLayout for top bar + CardLayout center
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(themeManager.getBg());

        // TOP BAR - Theme toggle anchored to top-right
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        topBar.setBackground(themeManager.getBg());
        topBar.setBorder(new EmptyBorder(10, 20, 10, 20));
        
        themeToggleButton = new JToggleButton(themeManager.isDarkMode() ? "Light Mode" : "Dark Mode");
        themeToggleButton.setFont(BUTTON_FONT);
        themeToggleButton.setFocusPainted(false);
        themeToggleButton.setBackground(Color.WHITE);
        themeToggleButton.setForeground(Color.BLACK);
        themeToggleButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(themeManager.getBorder()),
            new EmptyBorder(8, 16, 8, 16)
        ));
        themeToggleButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        themeToggleButton.setSelected(themeManager.isDarkMode());
        themeToggleButton.addActionListener(e -> themeManager.toggleTheme());
        topBar.add(themeToggleButton);
        
        mainPanel.add(topBar, BorderLayout.NORTH);

        // CardLayout for screens
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBackground(themeManager.getBg());

        // Create screens
        createLoginScreen();
        createDashboardScreen();

        // Start with login screen
        cardLayout.show(cardPanel, LOGIN_SCREEN);

        mainPanel.add(cardPanel, BorderLayout.CENTER);
        add(mainPanel);

        // IMPORTANT: pack() before setLocationRelativeTo for proper sizing
        pack();
        setLocationRelativeTo(null);
    }
    
    private void updateCardPanelBackground() {
        if (cardPanel != null) {
            cardPanel.setBackground(themeManager.getBg());
        }
    }

    /**
     * Style a JTable with dark theme colors and visible headers using custom renderer.
     */
    private void styleTable(JTable table) {
        table.setFont(TABLE_FONT);
        table.setRowHeight(40);
        table.setShowGrid(true);
        table.setGridColor(new Color(80, 80, 80));
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setFillsViewportHeight(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setBackground(new Color(40, 40, 40));        // Dark row background
        table.setForeground(Color.WHITE);                   // White text
        table.setSelectionBackground(new Color(76, 175, 80)); // Green selection
        table.setSelectionForeground(Color.BLACK);          // Black text on selection
        
        // Style the header with custom renderer to force colors
        JTableHeader header = table.getTableHeader();
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setBackground(new Color(50, 50, 50)); // Dark gray
                label.setForeground(Color.WHITE);           // White text
                label.setFont(TABLE_HEADER_FONT);
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80)));
                return label;
            }
        });
        header.setPreferredSize(new Dimension(0, 40));
        header.setReorderingAllowed(false);
    }

    /**
     * Create a styled button with fixed size - WHITE background, BLACK text.
     */
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(Color.WHITE);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(themeManager.getBorder()),
            new EmptyBorder(10, 24, 10, 24)
        ));
        button.setFont(BUTTON_FONT);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(BUTTON_SIZE);
        button.setMaximumSize(BUTTON_SIZE);
        button.setMinimumSize(BUTTON_SIZE);
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0xE8F5E9)); // Light green hover
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(Color.WHITE);
            }
        });
        return button;
    }

    /**
     * Create a styled danger button (red) - WHITE background, BLACK text.
     */
    private JButton createDangerButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(Color.WHITE);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(themeManager.getDanger()),
            new EmptyBorder(10, 24, 10, 24)
        ));
        button.setFont(new Font("SansSerif", Font.BOLD, 13));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(160, BUTTON_HEIGHT));
        button.setMaximumSize(new Dimension(160, BUTTON_HEIGHT));
        button.setMinimumSize(new Dimension(160, BUTTON_HEIGHT));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0xFFEBEE)); // Light red hover
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(Color.WHITE);
            }
        });
        return button;
    }

    /**
     * Create a styled text field with fixed size - WHITE background, BLACK text.
     */
    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setBackground(Color.WHITE);
        field.setForeground(Color.BLACK);
        field.setCaretColor(Color.BLACK);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(themeManager.getBorder()),
            new EmptyBorder(8, 12, 8, 12)
        ));
        field.setFont(LABEL_FONT);
        field.setPreferredSize(FIELD_SIZE);
        field.setMaximumSize(FIELD_SIZE);
        field.setMinimumSize(FIELD_SIZE);
        return field;
    }

    /**
     * Create a styled password field with fixed size - WHITE background, BLACK text.
     */
    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField();
        field.setBackground(Color.WHITE);
        field.setForeground(Color.BLACK);
        field.setCaretColor(Color.BLACK);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(themeManager.getBorder()),
            new EmptyBorder(8, 12, 8, 12)
        ));
        field.setFont(LABEL_FONT);
        field.setPreferredSize(FIELD_SIZE);
        field.setMaximumSize(FIELD_SIZE);
        field.setMinimumSize(FIELD_SIZE);
        return field;
    }

    /**
     * Create a styled combo box - WHITE background, BLACK text.
     */
    private JComboBox<String> createStyledComboBox(String[] items) {
        JComboBox<String> combo = new JComboBox<>(items);
        combo.setBackground(Color.WHITE);
        combo.setForeground(Color.BLACK);
        combo.setFont(LABEL_FONT);
        combo.setPreferredSize(FIELD_SIZE);
        combo.setMaximumSize(FIELD_SIZE);
        combo.setMinimumSize(FIELD_SIZE);
        // Fix renderer for black text
        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (isSelected) {
                    c.setBackground(new Color(0xE8F5E9));
                    c.setForeground(Color.BLACK);
                } else {
                    c.setBackground(Color.WHITE);
                    c.setForeground(Color.BLACK);
                }
                return c;
            }
        });
        return combo;
    }

    /**
     * Create a styled label.
     */
    private JLabel createStyledLabel(String text, int fontSize, boolean bold) {
        JLabel label = new JLabel(text);
        label.setForeground(themeManager.getText());
        label.setFont(new Font("SansSerif", bold ? Font.BOLD : Font.PLAIN, fontSize));
        return label;
    }

    /**
     * Create a secondary label (gray text).
     */
    private JLabel createSecondaryLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(themeManager.getTextSecondary());
        label.setFont(new Font("SansSerif", Font.PLAIN, 13));
        return label;
    }

    // ==================== LOGIN SCREEN (BorderLayout + Side-by-Side) ====================
    private void createLoginScreen() {
        // Main container with BorderLayout: Title NORTH, Forms CENTER
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(themeManager.getBg());

        // Title panel at NORTH
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(themeManager.getBg());
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBorder(new EmptyBorder(30, 0, 20, 0));
        
        JLabel titleLabel = createStyledLabel("Savings Goal Gamifier", 36, true);
        titleLabel.setForeground(themeManager.getAccent());
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titlePanel.add(titleLabel);
        
        JLabel subtitleLabel = createSecondaryLabel("Track goals. Build streaks. Earn XP. Compete with friends.");
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitleLabel.setBorder(new EmptyBorder(8, 0, 0, 0));
        titlePanel.add(subtitleLabel);
        
        mainPanel.add(titlePanel, BorderLayout.NORTH);

        // Forms panel at CENTER - side by side with GridLayout
        JPanel formsPanel = new JPanel(new GridLayout(1, 2, 40, 0));
        formsPanel.setBackground(themeManager.getBg());
        formsPanel.setBorder(new EmptyBorder(0, 60, 40, 60));
        formsPanel.setPreferredSize(new Dimension(800, 450));

        // Left: Login Card
        JPanel loginCard = createLoginCard();
        formsPanel.add(loginCard);

        // Right: Register Card
        JPanel registerCard = createRegisterCard();
        formsPanel.add(registerCard);

        mainPanel.add(formsPanel, BorderLayout.CENTER);
        cardPanel.add(mainPanel, LOGIN_SCREEN);
    }

    private JPanel createLoginCard() {
        JPanel card = new JPanel();
        card.setBackground(themeManager.getCard());
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(themeManager.getBorder()),
            new EmptyBorder(20, 20, 20, 20)
        ));

        // Title
        JLabel formTitle = createStyledLabel("Login", 22, true);
        formTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(formTitle);
        card.add(Box.createRigidArea(new Dimension(0, 20)));

        // Username field
        JLabel userLabel = createSecondaryLabel("Username");
        userLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginUsernameField = createStyledTextField();
        loginUsernameField.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        card.add(userLabel);
        card.add(Box.createRigidArea(new Dimension(0, 6)));
        card.add(loginUsernameField);
        card.add(Box.createRigidArea(new Dimension(0, 16)));

        // Password field
        JLabel passLabel = createSecondaryLabel("Password");
        passLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginPasswordField = createStyledPasswordField();
        loginPasswordField.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        card.add(passLabel);
        card.add(Box.createRigidArea(new Dimension(0, 6)));
        card.add(loginPasswordField);
        
        // Fixed spacing before button (not glue)
        card.add(Box.createRigidArea(new Dimension(0, 30)));
        
        // Login button
        JButton loginBtn = createStyledButton("Login");
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginBtn.addActionListener(e -> handleLogin());
        card.add(loginBtn);

        return card;
    }

    private JPanel createRegisterCard() {
        JPanel card = new JPanel();
        card.setBackground(themeManager.getCard());
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(themeManager.getBorder()),
            new EmptyBorder(20, 20, 20, 20)
        ));

        // Title
        JLabel formTitle = createStyledLabel("Create Account", 22, true);
        formTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(formTitle);
        card.add(Box.createRigidArea(new Dimension(0, 20)));

        // Username field
        JLabel userLabel = createSecondaryLabel("Username");
        userLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerUsernameField = createStyledTextField();
        registerUsernameField.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        card.add(userLabel);
        card.add(Box.createRigidArea(new Dimension(0, 6)));
        card.add(registerUsernameField);
        card.add(Box.createRigidArea(new Dimension(0, 14)));

        // Password field
        JLabel passLabel = createSecondaryLabel("Password");
        passLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerPasswordField = createStyledPasswordField();
        registerPasswordField.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        card.add(passLabel);
        card.add(Box.createRigidArea(new Dimension(0, 6)));
        card.add(registerPasswordField);
        card.add(Box.createRigidArea(new Dimension(0, 14)));

        // Confirm Password field
        JLabel confirmLabel = createSecondaryLabel("Confirm Password");
        confirmLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerConfirmPasswordField = createStyledPasswordField();
        registerConfirmPasswordField.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        card.add(confirmLabel);
        card.add(Box.createRigidArea(new Dimension(0, 6)));
        card.add(registerConfirmPasswordField);
        card.add(Box.createRigidArea(new Dimension(0, 14)));

        // Display Name field
        JLabel nameLabel = createSecondaryLabel("Display Name");
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerDisplayNameField = createStyledTextField();
        registerDisplayNameField.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        card.add(nameLabel);
        card.add(Box.createRigidArea(new Dimension(0, 6)));
        card.add(registerDisplayNameField);
        
        // Fixed spacing before button (not glue)
        card.add(Box.createRigidArea(new Dimension(0, 30)));
        
        // Register button
        JButton registerBtn = createStyledButton("Create Account");
        registerBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerBtn.addActionListener(e -> handleRegister());
        card.add(registerBtn);

        return card;
    }

    private void handleLogin() {
        String username = loginUsernameField.getText().trim();
        String password = new String(loginPasswordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter both username and password");
            return;
        }

        User user = userManager.login(username, password);
        if (user != null) {
            currentUser = user;
            switchToDashboard();
        } else {
            showError("Invalid username or password");
        }
    }

    private void handleRegister() {
        String username = registerUsernameField.getText().trim();
        String password = new String(registerPasswordField.getPassword());
        String confirmPassword = new String(registerConfirmPasswordField.getPassword());
        String displayName = registerDisplayNameField.getText().trim();

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || displayName.isEmpty()) {
            showError("Please fill in all fields");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showError("Passwords do not match");
            return;
        }

        try {
            User user = userManager.register(username, password, displayName);
            currentUser = user;
            showSuccess("Account created successfully! Welcome, " + displayName + "!");
            switchToDashboard();
        } catch (UsernameTakenException e) {
            showError(e.getMessage());
        } catch (Exception e) {
            showError("Registration failed: " + e.getMessage());
        }
    }

    // ==================== DASHBOARD SCREEN ====================
    private void createDashboardScreen() {
        JPanel panel = new JPanel(new BorderLayout(0, 0));
        panel.setBackground(themeManager.getBg());

        // Top bar
        JPanel topBar = createTopBar();
        panel.add(topBar, BorderLayout.NORTH);

        // Center - Goals table with scroll
        JScrollPane scrollPane = createGoalsTableScrollPane();
        panel.add(scrollPane, BorderLayout.CENTER);

        // Bottom - Button bar with proper FlowLayout
        JPanel buttonBar = createDashboardButtonBar();
        panel.add(buttonBar, BorderLayout.SOUTH);

        cardPanel.add(panel, DASHBOARD_SCREEN);
    }

    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(themeManager.getBg());
        topBar.setBorder(new EmptyBorder(15, 30, 10, 30));
        topBar.setPreferredSize(new Dimension(0, 70));

        // Welcome label - left
        dashboardWelcomeLabel = createStyledLabel("Welcome back!", 26, true);
        topBar.add(dashboardWelcomeLabel, BorderLayout.WEST);

        // Right side: XP display
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightPanel.setBackground(themeManager.getBg());
        
        dashboardXPLabel = createStyledLabel("XP: 0", 20, true);
        dashboardXPLabel.setForeground(themeManager.getAccent());
        rightPanel.add(dashboardXPLabel);
        
        topBar.add(rightPanel, BorderLayout.EAST);

        return topBar;
    }

    private JScrollPane createGoalsTableScrollPane() {
        String[] columnNames = {"Goal", "Type", "Target", "Current", "Progress", "Streak", "XP"};
        goalsTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        goalsTable = new JTable(goalsTableModel);
        styleTable(goalsTable);

        JScrollPane scrollPane = new JScrollPane(goalsTable);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(themeManager.getBorder()),
                "Your Goals",
                0, 0,
                new Font("SansSerif", Font.BOLD, 14),
                themeManager.getText()
            ),
            new EmptyBorder(10, 20, 10, 20)
        ));
        scrollPane.getViewport().setBackground(new Color(40, 40, 40));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        // Style scrollbar
        scrollPane.getVerticalScrollBar().setBackground(themeManager.getBg());
        scrollPane.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = themeManager.getAccent();
                this.trackColor = themeManager.getBg();
            }
            @Override
            protected JButton createDecreaseButton(int orientation) { return createZeroButton(); }
            @Override
            protected JButton createIncreaseButton(int orientation) { return createZeroButton(); }
            private JButton createZeroButton() {
                JButton btn = new JButton();
                btn.setPreferredSize(new Dimension(0, 0));
                btn.setMinimumSize(new Dimension(0, 0));
                btn.setMaximumSize(new Dimension(0, 0));
                return btn;
            }
        });

        return scrollPane;
    }

    private JPanel createDashboardButtonBar() {
        // Use FlowLayout.CENTER with proper spacing - buttons will wrap if needed
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(themeManager.getBg());
        buttonPanel.setBorder(new EmptyBorder(10, 20, 20, 20));

        JButton addGoalBtn = createStyledButton("Add Goal");
        addGoalBtn.addActionListener(e -> showAddGoalDialog());
        buttonPanel.add(addGoalBtn);

        JButton contributeBtn = createStyledButton("Contribute");
        contributeBtn.addActionListener(e -> handleContribution());
        buttonPanel.add(contributeBtn);

        JButton aiAdviceBtn = createStyledButton("Get AI Advice");
        aiAdviceBtn.addActionListener(e -> handleAIAdvice());
        buttonPanel.add(aiAdviceBtn);

        JButton friendsBtn = createStyledButton("Friends");
        friendsBtn.addActionListener(e -> showFriendsWindow());
        buttonPanel.add(friendsBtn);

        JButton leaderboardBtn = createStyledButton("Leaderboard");
        leaderboardBtn.addActionListener(e -> showLeaderboardWindow());
        buttonPanel.add(leaderboardBtn);

        JButton logoutBtn = createDangerButton("Logout");
        logoutBtn.addActionListener(e -> handleLogout());
        buttonPanel.add(logoutBtn);

        return buttonPanel;
    }

    private void switchToDashboard() {
        dashboardWelcomeLabel.setText("Welcome, " + currentUser.getDisplayName() + "!");
        dashboardXPLabel.setText("XP: " + currentUser.getTotalXP());
        
        refreshGoalsTable();
        
        // Clear login/register fields
        loginUsernameField.setText("");
        loginPasswordField.setText("");
        registerUsernameField.setText("");
        registerPasswordField.setText("");
        registerConfirmPasswordField.setText("");
        registerDisplayNameField.setText("");
        
        // Update theme toggle button text
        if (themeToggleButton != null) {
            themeToggleButton.setText(themeManager.isDarkMode() ? "Light Mode" : "Dark Mode");
        }
        
        cardLayout.show(cardPanel, DASHBOARD_SCREEN);
    }

    private void handleLogout() {
        currentUser = null;
        cardLayout.show(cardPanel, LOGIN_SCREEN);
        
        if (leaderboardFrame != null) leaderboardFrame.dispose();
        if (friendsFrame != null) friendsFrame.dispose();
    }

    private void refreshGoalsTable() {
        goalsTableModel.setRowCount(0);
        Goal[] goals = goalManager.getGoalsByUser(currentUser.getUserId());
        
        for (Goal goal : goals) {
            String type = goal.getGoalType();
            String streak = "-";
            String xp = "-";
            
            if (goal instanceof GamifiedGoal) {
                GamifiedGoal gg = (GamifiedGoal) goal;
                streak = String.valueOf(gg.getCurrentStreak());
                xp = String.valueOf(gg.getTotalXP());
            }
            
            double progress = goal.calculateProgress();
            // Use ASCII text only - no unicode box characters
            String progressStr = String.format("%.1f%% Complete", progress);
            
            goalsTableModel.addRow(new Object[]{
                goal.getName(),
                type,
                String.format("$%.2f", goal.getTargetAmount()),
                String.format("$%.2f", goal.getCurrentAmount()),
                progressStr,
                streak,
                xp
            });
        }
    }

    // ==================== ADD GOAL DIALOG ====================
    private void showAddGoalDialog() {
        JDialog dialog = new JDialog(this, "Add New Goal", true);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(themeManager.getBg());

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(themeManager.getCard());
        formPanel.setBorder(new EmptyBorder(30, 40, 30, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 18, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0;
        gbc.weightx = 1.0;

        gbc.gridy = 0; gbc.insets = new Insets(0, 0, 24, 0);
        JLabel titleLabel = createStyledLabel("Add New Goal", 22, true);
        formPanel.add(titleLabel, gbc);

        gbc.gridy = 1; gbc.insets = new Insets(0, 0, 6, 0);
        formPanel.add(createSecondaryLabel("Goal Name"), gbc);
        gbc.gridy = 2; gbc.insets = new Insets(0, 0, 18, 0);
        JTextField nameField = createStyledTextField();
        nameField.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(nameField, gbc);

        gbc.gridy = 3; gbc.insets = new Insets(0, 0, 6, 0);
        formPanel.add(createSecondaryLabel("Target Amount ($)"), gbc);
        gbc.gridy = 4; gbc.insets = new Insets(0, 0, 18, 0);
        JTextField targetField = createStyledTextField();
        targetField.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(targetField, gbc);

        gbc.gridy = 5; gbc.insets = new Insets(0, 0, 6, 0);
        formPanel.add(createSecondaryLabel("Initial Amount ($)"), gbc);
        gbc.gridy = 6; gbc.insets = new Insets(0, 0, 18, 0);
        JTextField initialField = createStyledTextField();
        initialField.setText("0");
        initialField.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(initialField, gbc);

        gbc.gridy = 7; gbc.insets = new Insets(0, 0, 6, 0);
        formPanel.add(createSecondaryLabel("Goal Type"), gbc);
        gbc.gridy = 8; gbc.insets = new Insets(0, 0, 28, 0);
        JComboBox<String> typeCombo = createStyledComboBox(new String[]{"Standard", "Gamified"});
        typeCombo.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(typeCombo, gbc);

        gbc.gridy = 9; gbc.insets = new Insets(0, 0, 0, 0);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setBackground(themeManager.getCard());
        
        JButton saveBtn = createStyledButton("Save Goal");
        saveBtn.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                if (name.isEmpty()) {
                    showErrorDialog(dialog, "Please enter a goal name");
                    return;
                }

                double target = Double.parseDouble(targetField.getText().trim());
                if (target <= 0) {
                    showErrorDialog(dialog, "Target amount must be greater than zero");
                    return;
                }

                double initial = Double.parseDouble(initialField.getText().trim());
                if (initial < 0) {
                    showErrorDialog(dialog, "Initial amount cannot be negative");
                    return;
                }

                String goalId = goalManager.generateGoalId();
                String userId = currentUser.getUserId();
                boolean isGamified = "Gamified".equals(typeCombo.getSelectedItem());

                Goal goal;
                if (isGamified) {
                    goal = new GamifiedGoal(goalId, userId, name, target, initial);
                } else {
                    goal = new StandardGoal(goalId, userId, name, target, initial);
                }

                goalManager.addGoal(goal);
                refreshGoalsTable();
                dialog.dispose();
                showSuccess("Goal created successfully!");
            } catch (NumberFormatException ex) {
                showErrorDialog(dialog, "Please enter valid numbers");
            } catch (Exception ex) {
                showErrorDialog(dialog, "Error: " + ex.getMessage());
            }
        });
        buttonPanel.add(saveBtn);

        JButton cancelBtn = createDangerButton("Cancel");
        cancelBtn.addActionListener(e -> dialog.dispose());
        buttonPanel.add(cancelBtn);

        formPanel.add(buttonPanel, gbc);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    // ==================== CONTRIBUTION ====================
    private void handleContribution() {
        int selectedRow = goalsTable.getSelectedRow();
        if (selectedRow < 0) {
            showError("Please select a goal first");
            return;
        }

        Goal[] goals = goalManager.getGoalsByUser(currentUser.getUserId());
        if (selectedRow >= goals.length) return;

        Goal selectedGoal = goals[selectedRow];

        JDialog dialog = new JDialog(this, "Contribute to " + selectedGoal.getName(), true);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(themeManager.getBg());

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(themeManager.getCard());
        formPanel.setBorder(new EmptyBorder(30, 40, 30, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(0, 0, 10, 0);

        gbc.gridy = 0;
        formPanel.add(createSecondaryLabel("Contribution Amount ($)"), gbc);
        
        gbc.gridy = 1; gbc.insets = new Insets(0, 0, 24, 0);
        JTextField amountField = createStyledTextField();
        amountField.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(amountField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setBackground(themeManager.getCard());

        JButton confirmBtn = createStyledButton("Confirm");
        confirmBtn.addActionListener(e -> {
            try {
                double amount = Double.parseDouble(amountField.getText().trim());
                int xpEarned = goalManager.logContribution(selectedGoal.getGoalId(), amount, userManager);
                refreshGoalsTable();
                dashboardXPLabel.setText("XP: " + currentUser.getTotalXP());
                
                String message = "Contribution of $" + String.format("%.2f", amount) + " added!";
                if (xpEarned > 0) {
                    message += "\nYou earned " + xpEarned + " XP!";
                    if (selectedGoal instanceof GamifiedGoal) {
                        GamifiedGoal gg = (GamifiedGoal) selectedGoal;
                        message += "\nStreak: " + gg.getCurrentStreak() + " days";
                    }
                }
                dialog.dispose();
                showSuccess(message);
            } catch (NumberFormatException ex) {
                showErrorDialog(dialog, "Please enter a valid number");
            } catch (InvalidAmountException ex) {
                showErrorDialog(dialog, ex.getMessage());
            } catch (GoalNotFoundException ex) {
                showErrorDialog(dialog, ex.getMessage());
            } catch (Exception ex) {
                showErrorDialog(dialog, "Error: " + ex.getMessage());
            }
        });
        buttonPanel.add(confirmBtn);

        JButton cancelBtn = createDangerButton("Cancel");
        cancelBtn.addActionListener(e -> dialog.dispose());
        buttonPanel.add(cancelBtn);

        gbc.gridy = 2; gbc.insets = new Insets(0, 0, 0, 0);
        formPanel.add(buttonPanel, gbc);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    // ==================== AI ADVICE ====================
    private void handleAIAdvice() {
        int selectedRow = goalsTable.getSelectedRow();
        if (selectedRow < 0) {
            showError("Please select a goal first");
            return;
        }

        Goal[] goals = goalManager.getGoalsByUser(currentUser.getUserId());
        if (selectedRow >= goals.length) return;

        Goal selectedGoal = goals[selectedRow];

        JDialog loadingDialog = new JDialog(this, "Getting AI Advice", true);
        loadingDialog.setLayout(new BorderLayout());
        loadingDialog.getContentPane().setBackground(themeManager.getBg());
        
        JLabel loadingLabel = new JLabel("Asking AI for advice...", SwingConstants.CENTER);
        loadingLabel.setForeground(themeManager.getText());
        loadingLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        loadingLabel.setBorder(new EmptyBorder(30, 30, 30, 30));
        loadingDialog.add(loadingLabel, BorderLayout.CENTER);
        
        loadingDialog.pack();
        loadingDialog.setLocationRelativeTo(this);
        
        new Thread(() -> {
            String advice = aiService.getSavingsAdvice(
                selectedGoal.getName(),
                selectedGoal.getTargetAmount(),
                selectedGoal.getCurrentAmount()
            );
            
            SwingUtilities.invokeLater(() -> {
                loadingDialog.dispose();
                showAdviceDialog(selectedGoal.getName(), advice);
            });
        }).start();
        
        loadingDialog.setVisible(true);
    }

    private void showAdviceDialog(String goalName, String advice) {
        JDialog dialog = new JDialog(this, "AI Advice for " + goalName, false);
        dialog.setSize(500, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(themeManager.getBg());

        // Non-editable JTextArea with line wrapping - DARK background, WHITE text
        JTextArea textArea = new JTextArea(advice);
        textArea.setEditable(false);
        textArea.setWrapStyleWord(true);
        textArea.setLineWrap(true);
        textArea.setBackground(new Color(0x2C2C2C)); // Dark gray background
        textArea.setForeground(Color.WHITE);         // White text
        textArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
        textArea.setBorder(new EmptyBorder(20, 20, 20, 20));
        textArea.setCaretColor(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(themeManager.getBorder()));
        scrollPane.getViewport().setBackground(new Color(0x2C2C2C));
        dialog.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(themeManager.getBg());
        buttonPanel.setBorder(new EmptyBorder(15, 0, 15, 0));
        
        JButton closeBtn = createStyledButton("Close");
        closeBtn.addActionListener(e -> dialog.dispose());
        buttonPanel.add(closeBtn);

        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    // ==================== FRIENDS WINDOW ====================
    private void showFriendsWindow() {
        if (friendsFrame != null && friendsFrame.isVisible()) {
            friendsFrame.toFront();
            return;
        }

        friendsFrame = new JFrame("Friends");
        friendsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        friendsFrame.getContentPane().setBackground(themeManager.getBg());
        friendsFrame.setLayout(new BorderLayout(0, 0));
        ((JComponent) friendsFrame.getContentPane()).setBorder(new EmptyBorder(20, 20, 20, 20));

        // Title bar
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(themeManager.getBg());
        titleBar.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        JLabel titleLabel = createStyledLabel("Your Friends", 22, true);
        titleBar.add(titleLabel, BorderLayout.WEST);
        
        friendsFrame.add(titleBar, BorderLayout.NORTH);

        // Friends list - center, expands
        String[] friendIds = friendshipManager.getFriends(currentUser.getUserId());
        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (String fid : friendIds) {
            User friend = userManager.getUserById(fid);
            if (friend != null) {
                listModel.addElement(friend.getDisplayName() + " (@" + friend.getUsername() + ") - XP: " + friend.getTotalXP());
            }
        }

        JList<String> friendsList = new JList<>(listModel);
        friendsList.setBackground(new Color(40, 40, 40));
        friendsList.setForeground(Color.WHITE);
        friendsList.setFont(new Font("SansSerif", Font.PLAIN, 14));
        friendsList.setSelectionBackground(themeManager.getAccent());
        friendsList.setSelectionForeground(Color.BLACK);
        friendsList.setBorder(BorderFactory.createLineBorder(themeManager.getBorder()));
        friendsList.setFixedCellHeight(40);
        
        JScrollPane listScroll = new JScrollPane(friendsList);
        listScroll.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(themeManager.getBorder()),
            "Friends List",
            0, 0,
            new Font("SansSerif", Font.BOLD, 13),
            themeManager.getText()
        ));
        listScroll.getViewport().setBackground(new Color(40, 40, 40));
        friendsFrame.add(listScroll, BorderLayout.CENTER);

        // Add friend panel - bottom
        JPanel addPanel = new JPanel(new GridBagLayout());
        addPanel.setBackground(themeManager.getCard());
        addPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(themeManager.getBorder()),
                "Add Friend",
                0, 0,
                new Font("SansSerif", Font.BOLD, 13),
                themeManager.getText()
            ),
            new EmptyBorder(15, 20, 15, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 12, 10);
        gbc.anchor = GridBagConstraints.CENTER;

        gbc.gridx = 0; gbc.gridy = 0;
        addPanel.add(createSecondaryLabel("Username:"), gbc);

        gbc.gridx = 1;
        JTextField friendUsernameField = createStyledTextField();
        friendUsernameField.setAlignmentX(Component.CENTER_ALIGNMENT);
        addPanel.add(friendUsernameField, gbc);

        gbc.gridx = 2;
        JButton addBtn = createStyledButton("Add Friend");
        addBtn.addActionListener(e -> {
            String friendUsername = friendUsernameField.getText().trim();
            if (friendUsername.isEmpty()) {
                showError("Please enter a username");
                return;
            }

            User friend = userManager.getUserByUsername(friendUsername);
            if (friend == null) {
                showError("User not found: " + friendUsername);
                return;
            }

            if (friend.getUserId().equals(currentUser.getUserId())) {
                showError("You cannot add yourself as a friend");
                return;
            }

            try {
                friendshipManager.addFriendship(currentUser.getUserId(), friend.getUserId());
                listModel.addElement(friend.getDisplayName() + " (@" + friend.getUsername() + ") - XP: " + friend.getTotalXP());
                friendUsernameField.setText("");
                showSuccess("Added " + friend.getDisplayName() + " as a friend!");
            } catch (IllegalArgumentException ex) {
                showError(ex.getMessage());
            }
        });
        addPanel.add(addBtn, gbc);

        friendsFrame.add(addPanel, BorderLayout.SOUTH);
        friendsFrame.pack();
        friendsFrame.setLocationRelativeTo(this);
        friendsFrame.setVisible(true);
    }

    // ==================== LEADERBOARD WINDOW ====================
    private void showLeaderboardWindow() {
        if (leaderboardFrame != null && leaderboardFrame.isVisible()) {
            refreshLeaderboard();
            leaderboardFrame.toFront();
            return;
        }

        leaderboardFrame = new JFrame("Leaderboard - You & Friends");
        leaderboardFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        leaderboardFrame.getContentPane().setBackground(themeManager.getBg());
        leaderboardFrame.setLayout(new BorderLayout(0, 0));
        ((JComponent) leaderboardFrame.getContentPane()).setBorder(new EmptyBorder(20, 20, 20, 20));

        // Title bar
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(themeManager.getBg());
        titleBar.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        JLabel titleLabel = createStyledLabel("Leaderboard", 24, true);
        titleLabel.setForeground(themeManager.getAccent());
        titleBar.add(titleLabel, BorderLayout.WEST);
        
        JPanel rightButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightButtons.setBackground(themeManager.getBg());
        
        JButton refreshBtn = createStyledButton("Refresh");
        refreshBtn.addActionListener(e -> refreshLeaderboard());
        rightButtons.add(refreshBtn);
        
        titleBar.add(rightButtons, BorderLayout.EAST);
        leaderboardFrame.add(titleBar, BorderLayout.NORTH);

        // Table - center, expands
        String[] columns = leaderboardManager.getColumnNames();
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        styleTable(table); // Apply same dark styling with custom header renderer

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(themeManager.getBorder()));
        scrollPane.getViewport().setBackground(new Color(40, 40, 40));
        leaderboardFrame.add(scrollPane, BorderLayout.CENTER);

        // Initial load
        refreshLeaderboard();
        leaderboardFrame.pack();
        leaderboardFrame.setLocationRelativeTo(this);
        leaderboardFrame.setVisible(true);
    }

    private void refreshLeaderboard() {
        if (leaderboardFrame == null) return;
        
        JTable table = null;
        for (Component comp : leaderboardFrame.getContentPane().getComponents()) {
            if (comp instanceof JScrollPane) {
                JScrollPane sp = (JScrollPane) comp;
                if (sp.getViewport().getView() instanceof JTable) {
                    table = (JTable) sp.getViewport().getView();
                    break;
                }
            }
        }
        
        if (table != null) {
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            model.setRowCount(0);
            
            Object[][] data = leaderboardManager.getLeaderboardData(currentUser.getUserId());
            for (Object[] row : data) {
                model.addRow(row);
            }
            
            // Highlight current user
            for (int i = 0; i < model.getRowCount(); i++) {
                String username = (String) model.getValueAt(i, 2);
                if (username.equals(currentUser.getUsername())) {
                    table.setRowSelectionInterval(i, i);
                    table.scrollRectToVisible(table.getCellRect(i, 0, true));
                    break;
                }
            }
        }
    }

    // ==================== THEME CHANGE LISTENER ====================
    @Override
    public void onThemeChanged(boolean isDarkMode) {
        // Update theme toggle button
        if (themeToggleButton != null) {
            themeToggleButton.setText(isDarkMode ? "Light Mode" : "Dark Mode");
            themeToggleButton.setSelected(isDarkMode);
        }
        
        // Update card panel background
        updateCardPanelBackground();
        
        // Apply theme to all visible components
        themeManager.applyThemeToComponent(this);
        
        // Also apply to external windows
        if (leaderboardFrame != null && leaderboardFrame.isVisible()) {
            themeManager.applyThemeToComponent(leaderboardFrame);
        }
        if (friendsFrame != null && friendsFrame.isVisible()) {
            themeManager.applyThemeToComponent(friendsFrame);
        }
        
        // Force re-apply black text on white backgrounds for inputs/buttons only
        enforceBlackTextOnWhiteBackground(this);
        
        // Repaint
        repaint();
    }
    
    private void enforceBlackTextOnWhiteBackground(Container container) {
        if (container == null) return;
        
        for (Component comp : container.getComponents()) {
            if (comp instanceof JTextField) {
                JTextField field = (JTextField) comp;
                field.setBackground(Color.WHITE);
                field.setForeground(Color.BLACK);
                field.setCaretColor(Color.BLACK);
            } else if (comp instanceof JPasswordField) {
                JPasswordField field = (JPasswordField) comp;
                field.setBackground(Color.WHITE);
                field.setForeground(Color.BLACK);
                field.setCaretColor(Color.BLACK);
            } else if (comp instanceof JComboBox) {
                JComboBox<?> combo = (JComboBox<?>) comp;
                combo.setBackground(Color.WHITE);
                combo.setForeground(Color.BLACK);
            } else if (comp instanceof JButton) {
                JButton btn = (JButton) comp;
                btn.setBackground(Color.WHITE);
                btn.setForeground(Color.BLACK);
            }
            // NOTE: JTextArea is intentionally NOT handled here - AI dialog uses dark background
            
            if (comp instanceof Container) {
                enforceBlackTextOnWhiteBackground((Container) comp);
            }
        }
    }

    // ==================== HELPER METHODS ====================
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showErrorDialog(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Use default
        }

        SwingUtilities.invokeLater(() -> {
            MainGUI gui = new MainGUI();
            gui.setVisible(true);
        });
    }
}