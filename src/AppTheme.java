import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

// Cores e estilos compartilhados pelas telas.
final class AppTheme {
    static final Color BACKGROUND = new Color(243, 246, 251);
    static final Color CARD = Color.WHITE;
    static final Color PRIMARY = new Color(37, 99, 235);
    static final Color PRIMARY_HOVER = new Color(29, 78, 216);
    static final Color SUCCESS = new Color(22, 163, 74);
    static final Color SUCCESS_HOVER = new Color(21, 128, 61);
    static final Color DANGER = new Color(220, 38, 38);
    static final Color DANGER_HOVER = new Color(185, 28, 28);
    static final Color TEXT = new Color(31, 41, 55);
    static final Color MUTED = new Color(107, 114, 128);
    static final Color BORDER = new Color(220, 226, 234);
    static final Color TABLE_HEADER = new Color(230, 238, 250);
    static final String FONT_FAMILY = "Work Sans";

    private AppTheme() {
    }

    static void applyCardStyle(JPanel panel) {
        panel.setBackground(CARD);
        panel.setBorder(new CompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(16, 16, 16, 16)));
    }

    static JPanel createCardPanel() {
        JPanel panel = new JPanel();
        applyCardStyle(panel);
        return panel;
    }

    static JButton createButton(String text, Color baseColor, Color hoverColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics graphics) {
                Graphics2D g2 = (Graphics2D) graphics.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                g2.dispose();
                super.paintComponent(graphics);
            }
        };
        button.setFont(new Font(FONT_FAMILY, Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(baseColor);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(10, 18, 10, 18));
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent event) {
                button.setBackground(hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent event) {
                button.setBackground(baseColor);
            }
        });
        return button;
    }
}
