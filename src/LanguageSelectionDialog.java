import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

// Tela inicial para escolher em qual idioma o programa será exibido.
final class LanguageSelectionDialog extends JDialog {
    private AppLanguage selection;

    private LanguageSelectionDialog() {
        super((Frame) null, "Bolsa Família CSV", true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setContentPane(createContentPanel());
        pack();
        setResizable(false);
        setLocationRelativeTo(null);
    }

    // Abre a tela e devolve a opção escolhida; ao fechar, nenhuma opção é selecionada.
    static AppLanguage chooseLanguage() {
        LanguageSelectionDialog dialog = new LanguageSelectionDialog();
        dialog.setVisible(true);
        return dialog.selection;
    }

    private JPanel createContentPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(16, 16));
        mainPanel.setBackground(AppTheme.BACKGROUND);
        mainPanel.setBorder(new EmptyBorder(18, 18, 18, 18));
        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        mainPanel.add(createLanguageCard(), BorderLayout.CENTER);
        return mainPanel;
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        JLabel title = new JLabel("Bolsa Família CSV");
        title.setFont(new Font(AppTheme.FONT_FAMILY, Font.BOLD, 26));
        title.setForeground(AppTheme.TEXT);

        JLabel subtitle = new JLabel("Selecione o idioma / Choose your language");
        subtitle.setFont(new Font(AppTheme.FONT_FAMILY, Font.PLAIN, 14));
        subtitle.setForeground(AppTheme.MUTED);

        panel.add(title);
        panel.add(Box.createVerticalStrut(4));
        panel.add(subtitle);
        return panel;
    }

    private JPanel createLanguageCard() {
        JPanel card = AppTheme.createCardPanel();
        card.setLayout(new BorderLayout(10, 10));

        JLabel section = new JLabel("Idioma / Language");
        section.setFont(new Font(AppTheme.FONT_FAMILY, Font.BOLD, 18));
        section.setForeground(AppTheme.TEXT);

        JPanel buttons = new JPanel(new GridLayout(1, 2, 10, 0));
        buttons.setOpaque(false);
        buttons.add(createLanguageButton("Português", AppLanguage.PORTUGUESE,
                AppTheme.PRIMARY, AppTheme.PRIMARY_HOVER));
        buttons.add(createLanguageButton("English", AppLanguage.ENGLISH,
                AppTheme.SUCCESS, AppTheme.SUCCESS_HOVER));

        card.add(section, BorderLayout.NORTH);
        card.add(buttons, BorderLayout.CENTER);
        return card;
    }

    private JButton createLanguageButton(
            String label,
            AppLanguage language,
            Color baseColor,
            Color hoverColor) {
        JButton button = AppTheme.createButton(label, baseColor, hoverColor);
        button.addActionListener(event -> {
            selection = language;
            dispose();
        });
        return button;
    }
}
