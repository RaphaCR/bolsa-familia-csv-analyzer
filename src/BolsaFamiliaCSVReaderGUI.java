import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.IOException;

/* Janela principal, com filtros, resultados e resumo da busca. */
public class BolsaFamiliaCSVReaderGUI extends JFrame {
    private final AppLanguage language;
    private final CsvBeneficiaryService csvService = new CsvBeneficiaryService();
    private final FiltersPanel filtersPanel;
    private final ResultsPanel resultsPanel;
    private final SummaryPanel summaryPanel;

    BolsaFamiliaCSVReaderGUI(AppLanguage language) {
        this.language = language;
        filtersPanel = new FiltersPanel(language, this::chooseFile, this::processCSV, this::clearFields);
        resultsPanel = new ResultsPanel(language);
        summaryPanel = new SummaryPanel(language);

        setTitle("Bolsa Família CSV");
        setSize(1080, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setContentPane(createMainPanel());
    }

    // Organiza as áreas que compõem a tela principal.
    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(16, 16));
        mainPanel.setBackground(AppTheme.BACKGROUND);
        mainPanel.setBorder(new EmptyBorder(18, 18, 18, 18));
        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        mainPanel.add(createCenterPanel(), BorderLayout.CENTER);
        mainPanel.add(summaryPanel, BorderLayout.SOUTH);
        return mainPanel;
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JLabel title = new JLabel("Bolsa Família CSV");
        title.setFont(new Font(AppTheme.FONT_FAMILY, Font.BOLD, 26));
        title.setForeground(AppTheme.TEXT);

        JLabel subtitle = new JLabel(text(
                "Carregue um arquivo CSV, filtre por município e beneficiário e veja os resultados.",
                "Load a CSV file, filter by municipality and beneficiary, and view the results."));
        subtitle.setFont(new Font(AppTheme.FONT_FAMILY, Font.PLAIN, 14));
        subtitle.setForeground(AppTheme.MUTED);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        textPanel.add(title);
        textPanel.add(Box.createVerticalStrut(4));
        textPanel.add(subtitle);
        panel.add(textPanel, BorderLayout.WEST);
        return panel;
    }

    private JPanel createCenterPanel() {
        JPanel center = new JPanel(new BorderLayout(16, 16));
        center.setOpaque(false);
        center.add(filtersPanel, BorderLayout.NORTH);
        center.add(resultsPanel, BorderLayout.CENTER);
        return center;
    }

    private String text(String portuguese, String english) {
        return language.text(portuguese, english);
    }

    private void chooseFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle(text("Selecionar arquivo CSV", "Select CSV file"));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            filtersPanel.setFilePath(chooser.getSelectedFile().getAbsolutePath());
        }
    }

    private void clearFields() {
        filtersPanel.clear();
        resultsPanel.clear();
        summaryPanel.reset();
    }

    // Confere os campos, faz a busca no CSV e atualiza os resultados.
    private void processCSV() {
        resultsPanel.clear();

        String filePath = filtersPanel.getFilePath();
        String municipality = filtersPanel.getMunicipality();
        if (filePath.isEmpty()) {
            showMessage("Selecione um arquivo CSV.", "Select a CSV file.");
            return;
        }
        if (municipality.isEmpty()) {
            showMessage("Informe um município.", "Enter a municipality.");
            return;
        }

        String beneficiaryName = null;
        if (filtersPanel.isNameFilterSelected()) {
            beneficiaryName = filtersPanel.getBeneficiaryName();
            if (beneficiaryName.isEmpty()) {
                showMessage("Informe o nome do beneficiário.", "Enter the beneficiary's name.");
                return;
            }
        }

        try {
            CsvBeneficiaryService.Result result = csvService.search(filePath, municipality, beneficiaryName);
            for (CsvBeneficiaryService.Beneficiary beneficiary : result.getBeneficiaries()) {
                resultsPanel.addRecord(beneficiary);
            }
            summaryPanel.showTotals(result.getBeneficiaries().size(), result.getTotalAmount());
        } catch (CsvBeneficiaryService.EmptyCsvException exception) {
            showMessage("O arquivo CSV está vazio.", "The CSV file is empty.");
        } catch (IOException exception) {
            JOptionPane.showMessageDialog(this,
                    text("Erro ao ler o arquivo: ", "Error reading the file: ") + exception.getMessage());
        }
    }

    private void showMessage(String portuguese, String english) {
        JOptionPane.showMessageDialog(this, text(portuguese, english));
    }

    public static String removeDiacritics(String value) {
        return CsvBeneficiaryService.removeDiacritics(value);
    }

    public static void main(String[] args) {
        BolsaFamiliaCSVReaderApp.main(args);
    }
}
