import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

// Reúne o visual e os elementos que os cartões têm em comum.
abstract class BaseCardPanel extends JPanel {
    protected final AppLanguage language;

    BaseCardPanel(AppLanguage language) {
        this.language = language;
        AppTheme.applyCardStyle(this);
    }

    protected String text(String portuguese, String english) {
        return language.text(portuguese, english);
    }

    protected JLabel createSectionTitle(String portuguese, String english) {
        JLabel label = new JLabel(text(portuguese, english));
        label.setFont(new Font(AppTheme.FONT_FAMILY, Font.BOLD, 18));
        label.setForeground(AppTheme.TEXT);
        return label;
    }

    protected JLabel createFieldLabel(String portuguese, String english) {
        JLabel label = new JLabel(text(portuguese, english));
        label.setFont(new Font(AppTheme.FONT_FAMILY, Font.PLAIN, 14));
        label.setForeground(AppTheme.TEXT);
        return label;
    }

    protected JTextField createTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font(AppTheme.FONT_FAMILY, Font.PLAIN, 14));
        field.setForeground(AppTheme.TEXT);
        field.setBackground(AppTheme.CARD);
        field.setPreferredSize(new Dimension(250, 38));
        field.setBorder(BorderFactory.createCompoundBorder(
                new RoundedLineBorder(new Color(203, 213, 225), 1, 14),
                new EmptyBorder(8, 12, 8, 12)));
        return field;
    }

    protected JButton createButton(String portuguese, String english, Color baseColor, Color hoverColor) {
        return AppTheme.createButton(text(portuguese, english), baseColor, hoverColor);
    }
}
