import javax.swing.*;

// Inicia o programa e abre a janela no idioma escolhido.
public final class BolsaFamiliaCSVReaderApp {
    private BolsaFamiliaCSVReaderApp() {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            setSystemLookAndFeel();
            AppLanguage language = LanguageSelectionDialog.chooseLanguage();
            if (language == null) {
                return;
            }

            JComponent.setDefaultLocale(language.getLocale());
            new BolsaFamiliaCSVReaderGUI(language).setVisible(true);
        });
    }

    private static void setSystemLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
    }
}
