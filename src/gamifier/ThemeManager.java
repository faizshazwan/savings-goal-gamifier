package gamifier;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ThemeManager handles switching between Light and Dark themes.
 * Manages color constants and provides methods to apply themes to components.
 */
public class ThemeManager {
    // Singleton instance
    private static ThemeManager instance;
    
    // Current theme state
    private boolean isDarkMode = false;
    
    // Listeners for theme changes
    private final List<ThemeChangeListener> listeners = new ArrayList<>();
    
    // Color constants for Light Theme
    public static final Color LIGHT_BG = new Color(0xF5F5F5);
    public static final Color LIGHT_CARD = Color.WHITE;
    public static final Color LIGHT_TEXT = Color.BLACK;
    public static final Color LIGHT_TEXT_SECONDARY = new Color(0x666666);
    public static final Color LIGHT_ACCENT = new Color(0x2E7D32);
    public static final Color LIGHT_ACCENT_HOVER = new Color(0x388E3C);
    public static final Color LIGHT_DANGER = new Color(0xC62828);
    public static final Color LIGHT_BORDER = new Color(0xCCCCCC);
    public static final Color LIGHT_INPUT_BG = Color.WHITE;
    public static final Color LIGHT_TABLE_HEADER_BG = new Color(0xE8F5E9);
    public static final Color LIGHT_TABLE_SELECTION_BG = new Color(0x2E7D32);
    public static final Color LIGHT_TABLE_SELECTION_FG = Color.WHITE;
    public static final Color LIGHT_SCROLL_BG = new Color(0xF5F5F5);
    public static final Color LIGHT_BUTTON_TEXT = Color.BLACK;
    
    // Color constants for Dark Theme
    public static final Color DARK_BG = new Color(0x2C2C2C);
    public static final Color DARK_CARD = new Color(0x363636);
    public static final Color DARK_TEXT = Color.WHITE;
    public static final Color DARK_TEXT_SECONDARY = new Color(0xBBBBBB);
    public static final Color DARK_ACCENT = new Color(0x4CAF50);
    public static final Color DARK_ACCENT_HOVER = new Color(0x66BB6A);
    public static final Color DARK_DANGER = new Color(0xF44336);
    public static final Color DARK_BORDER = new Color(0x444444);
    public static final Color DARK_INPUT_BG = new Color(0x404040);
    public static final Color DARK_TABLE_HEADER_BG = new Color(0x1E1E1E);
    public static final Color DARK_TABLE_SELECTION_BG = new Color(0x4CAF50);
    public static final Color DARK_TABLE_SELECTION_FG = Color.WHITE;
    public static final Color DARK_SCROLL_BG = new Color(0x2C2C2C);
    public static final Color DARK_BUTTON_TEXT = Color.BLACK; // Black text on green buttons
    
    // Current theme colors (updated when theme changes)
    private Color currentBg;
    private Color currentCard;
    private Color currentText;
    private Color currentTextSecondary;
    private Color currentAccent;
    private Color currentAccentHover;
    private Color currentDanger;
    private Color currentBorder;
    private Color currentInputBg;
    private Color currentTableHeaderBg;
    private Color currentTableSelectionBg;
    private Color currentTableSelectionFg;
    private Color currentScrollBg;
    private Color currentButtonText;
    
    // UIManager defaults key constants
    private static final String[] UI_KEYS = {
        "Panel.background", "Label.foreground", "Button.background", "Button.foreground",
        "Button.select", "TextField.background", "TextField.foreground", "TextField.caretForeground",
        "PasswordField.background", "PasswordField.foreground", "PasswordField.caretForeground",
        "ComboBox.background", "ComboBox.foreground", "Table.background", "Table.foreground",
        "Table.gridColor", "Table.selectionBackground", "Table.selectionForeground",
        "TableHeader.background", "TableHeader.foreground", "ScrollPane.background",
        "Viewport.background", "Dialog.background", "OptionPane.background",
        "OptionPane.messageForeground", "OptionPane.buttonBackground", "OptionPane.buttonForeground",
        "List.background", "List.foreground", "List.selectionBackground", "List.selectionForeground"
    };
    
    private ThemeManager() {
        applyThemeColors(false); // Start with light mode
    }
    
    public static ThemeManager getInstance() {
        if (instance == null) {
            instance = new ThemeManager();
        }
        return instance;
    }
    
    public boolean isDarkMode() {
        return isDarkMode;
    }
    
    public void toggleTheme() {
        setTheme(!isDarkMode);
    }
    
    public void setTheme(boolean darkMode) {
        if (this.isDarkMode != darkMode) {
            this.isDarkMode = darkMode;
            applyThemeColors(darkMode);
            notifyListeners();
        }
    }
    
    public void applyThemeColors(boolean darkMode) {
        if (darkMode) {
            currentBg = DARK_BG;
            currentCard = DARK_CARD;
            currentText = DARK_TEXT;
            currentTextSecondary = DARK_TEXT_SECONDARY;
            currentAccent = DARK_ACCENT;
            currentAccentHover = DARK_ACCENT_HOVER;
            currentDanger = DARK_DANGER;
            currentBorder = DARK_BORDER;
            currentInputBg = DARK_INPUT_BG;
            currentTableHeaderBg = DARK_TABLE_HEADER_BG;
            currentTableSelectionBg = DARK_TABLE_SELECTION_BG;
            currentTableSelectionFg = DARK_TABLE_SELECTION_FG;
            currentScrollBg = DARK_SCROLL_BG;
            currentButtonText = DARK_BUTTON_TEXT;
        } else {
            currentBg = LIGHT_BG;
            currentCard = LIGHT_CARD;
            currentText = LIGHT_TEXT;
            currentTextSecondary = LIGHT_TEXT_SECONDARY;
            currentAccent = LIGHT_ACCENT;
            currentAccentHover = LIGHT_ACCENT_HOVER;
            currentDanger = LIGHT_DANGER;
            currentBorder = LIGHT_BORDER;
            currentInputBg = LIGHT_INPUT_BG;
            currentTableHeaderBg = LIGHT_TABLE_HEADER_BG;
            currentTableSelectionBg = LIGHT_TABLE_SELECTION_BG;
            currentTableSelectionFg = LIGHT_TABLE_SELECTION_FG;
            currentScrollBg = LIGHT_SCROLL_BG;
            currentButtonText = LIGHT_BUTTON_TEXT;
        }
        
        // Update UIManager defaults
        updateUIManagerDefaults();
    }
    
    private void updateUIManagerDefaults() {
        UIManager.put("Panel.background", currentBg);
        UIManager.put("Label.foreground", currentText);
        UIManager.put("Button.background", currentAccent);
        UIManager.put("Button.foreground", currentButtonText);
        UIManager.put("Button.select", currentAccentHover);
        UIManager.put("TextField.background", currentInputBg);
        UIManager.put("TextField.foreground", currentText);
        UIManager.put("TextField.caretForeground", currentText);
        UIManager.put("PasswordField.background", currentInputBg);
        UIManager.put("PasswordField.foreground", currentText);
        UIManager.put("PasswordField.caretForeground", currentText);
        UIManager.put("ComboBox.background", currentInputBg);
        UIManager.put("ComboBox.foreground", currentText);
        UIManager.put("Table.background", currentCard);
        UIManager.put("Table.foreground", currentText);
        UIManager.put("Table.gridColor", currentBorder);
        UIManager.put("Table.selectionBackground", currentTableSelectionBg);
        UIManager.put("Table.selectionForeground", currentTableSelectionFg);
        UIManager.put("TableHeader.background", currentTableHeaderBg);
        UIManager.put("TableHeader.foreground", currentText);
        UIManager.put("ScrollPane.background", currentBg);
        UIManager.put("Viewport.background", currentBg);
        UIManager.put("Dialog.background", currentBg);
        UIManager.put("OptionPane.background", currentBg);
        UIManager.put("OptionPane.messageForeground", currentText);
        UIManager.put("OptionPane.buttonBackground", currentAccent);
        UIManager.put("OptionPane.buttonForeground", currentButtonText);
        UIManager.put("List.background", currentCard);
        UIManager.put("List.foreground", currentText);
        UIManager.put("List.selectionBackground", currentTableSelectionBg);
        UIManager.put("List.selectionForeground", currentTableSelectionFg);
    }
    
    /**
     * Apply theme to a component and all its children recursively.
     * Call this after toggling theme to update existing components.
     */
    public void applyThemeToComponent(Container container) {
        if (container == null) return;
        
        // Apply to this container
        applyThemeToSingleComponent(container);
        
        // Recursively apply to children
        for (Component comp : container.getComponents()) {
            if (comp instanceof Container) {
                applyThemeToComponent((Container) comp);
            } else {
                applyThemeToSingleComponent(comp);
            }
        }
        
        // Repaint
        container.revalidate();
        container.repaint();
    }
    
    private void applyThemeToSingleComponent(Component comp) {
        if (comp instanceof JPanel || comp instanceof JDialog || comp instanceof JFrame || comp instanceof JScrollPane || comp instanceof JViewport) {
            comp.setBackground(currentBg);
        } else if (comp instanceof JLabel) {
            JLabel label = (JLabel) comp;
            // Don't override if it's a title label with accent color
            if (label.getForeground() != currentAccent && label.getForeground() != currentDanger) {
                label.setForeground(currentText);
            }
        } else if (comp instanceof JButton) {
            JButton button = (JButton) comp;
            // Preserve danger button color
            if (button.getBackground() != currentDanger && button.getBackground() != currentAccentHover) {
                button.setBackground(currentAccent);
            }
            button.setForeground(currentButtonText);
        } else if (comp instanceof JTextField) {
            JTextField field = (JTextField) comp;
            field.setBackground(currentInputBg);
            field.setForeground(currentText);
            field.setCaretColor(currentText);
            // Update border
            Border border = field.getBorder();
            if (border instanceof CompoundBorder) {
                Border outer = ((CompoundBorder) border).getOutsideBorder();
                if (outer instanceof LineBorder) {
                    field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(currentBorder),
                        new EmptyBorder(8, 12, 8, 12)
                    ));
                }
            }
        } else if (comp instanceof JPasswordField) {
            JPasswordField field = (JPasswordField) comp;
            field.setBackground(currentInputBg);
            field.setForeground(currentText);
            field.setCaretColor(currentText);
            Border border = field.getBorder();
            if (border instanceof CompoundBorder) {
                Border outer = ((CompoundBorder) border).getOutsideBorder();
                if (outer instanceof LineBorder) {
                    field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(currentBorder),
                        new EmptyBorder(8, 12, 8, 12)
                    ));
                }
            }
        } else if (comp instanceof JComboBox) {
            JComboBox<?> combo = (JComboBox<?>) comp;
            combo.setBackground(currentInputBg);
            combo.setForeground(currentText);
        } else if (comp instanceof JTable) {
            JTable table = (JTable) comp;
            table.setBackground(currentCard);
            table.setForeground(currentText);
            table.setGridColor(currentBorder);
            table.setSelectionBackground(currentTableSelectionBg);
            table.setSelectionForeground(currentTableSelectionFg);
            JTableHeader header = table.getTableHeader();
            if (header != null) {
                header.setBackground(currentTableHeaderBg);
                header.setForeground(currentText);
            }
        } else if (comp instanceof JList) {
            JList<?> list = (JList<?>) comp;
            list.setBackground(currentCard);
            list.setForeground(currentText);
            list.setSelectionBackground(currentTableSelectionBg);
            list.setSelectionForeground(currentTableSelectionFg);
        } else if (comp instanceof JTextArea) {
            JTextArea area = (JTextArea) comp;
            area.setBackground(currentCard);
            area.setForeground(currentText);
            area.setCaretColor(currentText);
        } else if (comp instanceof JSeparator) {
            comp.setForeground(currentBorder);
        }
    }
    
    // Getter methods for current theme colors
    public Color getBg() { return currentBg; }
    public Color getCard() { return currentCard; }
    public Color getText() { return currentText; }
    public Color getTextSecondary() { return currentTextSecondary; }
    public Color getAccent() { return currentAccent; }
    public Color getAccentHover() { return currentAccentHover; }
    public Color getDanger() { return currentDanger; }
    public Color getBorder() { return currentBorder; }
    public Color getInputBg() { return currentInputBg; }
    public Color getTableHeaderBg() { return currentTableHeaderBg; }
    public Color getTableSelectionBg() { return currentTableSelectionBg; }
    public Color getTableSelectionFg() { return currentTableSelectionFg; }
    public Color getScrollBg() { return currentScrollBg; }
    public Color getButtonText() { return currentButtonText; }
    
    /**
     * Listener interface for theme changes.
     */
    public interface ThemeChangeListener {
        void onThemeChanged(boolean isDarkMode);
    }
    
    public void addThemeChangeListener(ThemeChangeListener listener) {
        listeners.add(listener);
    }
    
    public void removeThemeChangeListener(ThemeChangeListener listener) {
        listeners.remove(listener);
    }
    
    private void notifyListeners() {
        for (ThemeChangeListener listener : listeners) {
            listener.onThemeChanged(isDarkMode);
        }
    }
}