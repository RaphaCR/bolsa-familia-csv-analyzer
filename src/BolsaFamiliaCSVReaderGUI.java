import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.nio.charset.Charset;
import java.text.Normalizer;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/* BolsaFamiliaCSVReaderGUI class
 * Main graphical interface to load, filter and display Bolsa Família CSV data.
 * Provides input components (file, municipality, beneficiary name), results table and summary.
 */
public class BolsaFamiliaCSVReaderGUI extends JFrame {

    private JTextField txtFilePath;
    private JTextField txtMunicipality;
    private JCheckBox chkFilterByName;
    private JTextField txtBeneficiaryName;

    private JTable table;
    private DefaultTableModel tableModel;

    private JLabel lblTotalRecords;
    private JLabel lblTotalAmount;

    // Color palette used by the UI (visual consistency)
    private final Color BACKGROUND = new Color(243, 246, 251);
    private final Color CARD = Color.WHITE;
    private final Color PRIMARY = new Color(37, 99, 235);
    private final Color PRIMARY_HOVER = new Color(29, 78, 216);
    private final Color SUCCESS = new Color(22, 163, 74);
    private final Color SUCCESS_HOVER = new Color(21, 128, 61);
    private final Color DANGER = new Color(220, 38, 38);
    private final Color DANGER_HOVER = new Color(185, 28, 28);
    private final Color TEXT = new Color(31, 41, 55);
    private final Color MUTED = new Color(107, 114, 128);
    private final Color BORDER = new Color(220, 226, 234);
    private final Color TABLE_HEADER = new Color(230, 238, 250);

    // Constructor: sets title, size and triggers UI creation
    public BolsaFamiliaCSVReaderGUI() {
        setTitle("Bolsa Família CSV");
        setSize(1080, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        initUI();
    }

    // Normalize text by removing diacritics/accents for safe comparisons
    public static String removeDiacritics(String text) {
        return Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "");
    }

    // Initialize and arrange the main window panels (header, center, footer)
    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(16, 16));
        mainPanel.setBackground(BACKGROUND);
        mainPanel.setBorder(new EmptyBorder(18, 18, 18, 18));

        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        mainPanel.add(createCenterPanel(), BorderLayout.CENTER);
        mainPanel.add(createFooterPanel(), BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    // Create header panel with title and explanatory subtitle
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JLabel title = new JLabel("Bolsa Família CSV");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(TEXT);

        JLabel subtitle = new JLabel("Carregue um arquivo CSV, filtre por municipio e beneficiário, e veja os resultados.");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(MUTED);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        textPanel.add(title);
        textPanel.add(Box.createVerticalStrut(4));
        textPanel.add(subtitle);

        panel.add(textPanel, BorderLayout.WEST);
        return panel;
    }

    // Create center panel containing filters and the results table
    private JPanel createCenterPanel() {
        JPanel center = new JPanel(new BorderLayout(16, 16));
        center.setOpaque(false);
        center.add(createFiltersCard(), BorderLayout.NORTH);
        center.add(createTableCard(), BorderLayout.CENTER);
        return center;
    }

    // Build the filters card: file field, municipality and optional name filter
    // Also configures buttons (Select File, Search, Clear) and their listeners
    private JPanel createFiltersCard() {
        JPanel card = createCardPanel();
        card.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        JLabel lblSection = new JLabel("Filtros");
        lblSection.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblSection.setForeground(TEXT);

        JLabel lblFile = createLabel("Arquivo CSV");
        txtFilePath = createTextField();
        txtFilePath.setEditable(false);

        JButton btnBrowse = createStyledButton("Selecione o arquivo", PRIMARY, PRIMARY_HOVER);

        JLabel lblMunicipality = createLabel("Municipio");
        txtMunicipality = createTextField();

        chkFilterByName = new JCheckBox("Filtrar por beneficiário");
        chkFilterByName.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        chkFilterByName.setForeground(TEXT);
        chkFilterByName.setOpaque(false);

        JLabel lblName = createLabel("Nome do beneficiário");
        txtBeneficiaryName = createTextField();
        txtBeneficiaryName.setEnabled(false);

        JButton btnSearch = createStyledButton("Buscar", SUCCESS, SUCCESS_HOVER);
        JButton btnClear = createStyledButton("Limpar", DANGER, DANGER_HOVER);

        chkFilterByName.addActionListener(e ->
                txtBeneficiaryName.setEnabled(chkFilterByName.isSelected())
        );

        btnBrowse.addActionListener(e -> chooseFile());
        btnSearch.addActionListener(e -> processCSV());
        btnClear.addActionListener(e -> clearFields());

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        card.add(lblSection, gbc);

        gbc.gridy++;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        card.add(lblFile, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        card.add(txtFilePath, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0;
        card.add(btnBrowse, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        card.add(lblMunicipality, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        card.add(txtMunicipality, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 3;
        card.add(chkFilterByName, gbc);

        gbc.gridy++;
        gbc.gridwidth = 1;
        card.add(lblName, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        card.add(txtBeneficiaryName, gbc);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);
        actions.add(btnSearch);
        actions.add(btnClear);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.EAST;
        card.add(actions, gbc);

        return card;
    }

    // Create the card that holds the results table (columns: Beneficiary, CPF, Value)
    // Defines the table model and applies visual styling
    private JPanel createTableCard() {
        JPanel card = createCardPanel();
        card.setLayout(new BorderLayout(10, 10));

        JLabel lblSection = new JLabel("Resultado");
        lblSection.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblSection.setForeground(TEXT);

        tableModel = new DefaultTableModel(new Object[]{"Beneficiário", "CPF", "Valor"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        styleTable(table);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new LineBorder(BORDER, 1, true));
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        card.add(lblSection, BorderLayout.NORTH);
        card.add(scrollPane, BorderLayout.CENTER);

        return card;
    }

    // Create footer/summary with total records and total amount found
    private JPanel createFooterPanel() {
        JPanel panel = createCardPanel();
        panel.setLayout(new GridLayout(1, 2, 10, 0));

        lblTotalRecords = new JLabel("Total de registros: 0");
        lblTotalRecords.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTotalRecords.setForeground(TEXT);

        lblTotalAmount = new JLabel("Valor total: R$ 0,00");
        lblTotalAmount.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTotalAmount.setForeground(SUCCESS);

        panel.add(lblTotalRecords);
        panel.add(lblTotalAmount);

        return panel;
    }

    // Helper: creates a panel with border and padding used as a visual 'card'
    private JPanel createCardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(CARD);
        panel.setBorder(new CompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(16, 16, 16, 16)
        ));
        return panel;
    }

    // Helper: creates a styled JLabel used in forms
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(TEXT);
        return label;
    }

    // Helper: creates a JTextField with consistent styling (padding, font, size)
    private JTextField createTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setForeground(TEXT);
        field.setPreferredSize(new Dimension(250, 38));
        field.setBorder(new CompoundBorder(
                new LineBorder(new Color(203, 213, 225), 1, true),
                new EmptyBorder(8, 12, 8, 12)
        ));
        return field;
    }

    // Helper: creates a JButton with hover behavior and the UI's standard styling
    private JButton createStyledButton(String text, Color baseColor, Color hoverColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(baseColor);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(10, 18, 10, 18));
        button.setContentAreaFilled(false);
        button.setOpaque(true);

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(baseColor);
            }
        });

        return button;
    }

    // Apply styles and renderers to the table to make it more readable
    // Defines column alignment, heights, colors and header
    private void styleTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(30);
        table.setGridColor(new Color(229, 231, 235));
        table.setSelectionBackground(new Color(219, 234, 254));
        table.setSelectionForeground(TEXT);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setFillsViewportHeight(true);
        table.setAutoCreateRowSorter(true);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(TABLE_HEADER);
        header.setForeground(TEXT);
        header.setBorder(new MatteBorder(0, 0, 1, 0, BORDER));
        header.setPreferredSize(new Dimension(header.getWidth(), 34));

        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(SwingConstants.LEFT);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        table.getColumnModel().getColumn(0).setCellRenderer(leftRenderer);
        table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);

        table.getColumnModel().getColumn(0).setPreferredWidth(420);
        table.getColumnModel().getColumn(1).setPreferredWidth(180);
        table.getColumnModel().getColumn(2).setPreferredWidth(140);
    }

    // Opens a JFileChooser for the user to select the CSV file
    // Fills the file path field with the selected file
    private void chooseFile() {
        JFileChooser chooser = new JFileChooser();
        int option = chooser.showOpenDialog(this);

        if (option == JFileChooser.APPROVE_OPTION) {
            txtFilePath.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }

    // Clears all form fields, unchecks options and resets the table and summaries
    private void clearFields() {
        txtFilePath.setText("");
        txtMunicipality.setText("");
        txtBeneficiaryName.setText("");
        chkFilterByName.setSelected(false);
        txtBeneficiaryName.setEnabled(false);

        tableModel.setRowCount(0);

        lblTotalRecords.setText("Total de registros: 0");
        lblTotalAmount.setText("Valor total: R$ 0,00");
    }

    // Main CSV read/filter process:
    // 1) validate inputs (file and municipality);
    // 2) normalize search terms (remove diacritics, uppercase);
    // 3) detect column indices from the header;
    // 4) iterate lines, filter by municipality and (optionally) by name;
    // 5) populate the table and sum values to display totals
    private void processCSV() {
        tableModel.setRowCount(0);

        String filePath = txtFilePath.getText().trim();
        String municipalityInput = txtMunicipality.getText().trim();

        if (filePath.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecione um arquivo CSV.");
            return;
        }

        if (municipalityInput.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Insira um municipio.");
            return;
        }

        String municipalitySearch = removeDiacritics(municipalityInput.toUpperCase());
        String nameSearch = null;

        if (chkFilterByName.isSelected()) {
            nameSearch = removeDiacritics(txtBeneficiaryName.getText().trim().toUpperCase());
            if (nameSearch.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Insira o nome do beneficiário.");
                return;
            }
        }

        List<String[]> filtered = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(filePath), Charset.forName("ISO-8859-1")))) {

            String header = br.readLine();
            if (header == null || header.isBlank()) {
                JOptionPane.showMessageDialog(this, "O arquivo CSV está vazio.");
                return;
            }

            String[] columnsHeader = header.split(";");

            int municipalityIndex = 4;
            int valueIndex = 8;
            int nameIndex = 7;
            int cpfIndex = 1;

            for (int i = 0; i < columnsHeader.length; i++) {
                String col = columnsHeader[i].toUpperCase();

                if (col.contains("NOME MUNIC")) municipalityIndex = i;
                if (col.contains("VALOR")) valueIndex = i;
                if (col.contains("NOME FAVORECIDO")) nameIndex = i;
                if (col.contains("CPF")) cpfIndex = i;
            }

            String line;

            while ((line = br.readLine()) != null) {
                String[] cols = line.split(";");

                if (cols.length <= Math.max(valueIndex, Math.max(nameIndex, municipalityIndex))) {
                    continue;
                }

                String municipality = removeDiacritics(
                        cols[municipalityIndex].replace("\"", "").trim().toUpperCase()
                );

                if (municipality.equals(municipalitySearch)) {
                    filtered.add(cols);
                }
            }

            int total = 0;
            double sum = 0;

            for (String[] cols : filtered) {
                String beneficiary = cols[nameIndex].replace("\"", "").trim();
                String cpf = cpfIndex < cols.length ? cols[cpfIndex].replace("\"", "").trim() : "";
                String valueStr = cols[valueIndex].replace("\"", "").replace(",", ".").trim();

                String normalizedName = removeDiacritics(beneficiary.toUpperCase());

                if (nameSearch != null && !normalizedName.contains(nameSearch)) {
                    continue;
                }

                tableModel.addRow(new Object[]{
                        beneficiary,
                        cpf,
                        "R$ " + valueStr
                });

                total++;

                try {
                    sum += Double.parseDouble(valueStr);
                } catch (Exception ignored) {
                }
            }

            NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
            lblTotalRecords.setText("Total de registros: " + total);
            lblTotalAmount.setText("Valor total: " + format.format(sum));

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao ler o arquivo: " + e.getMessage());
        }
    }

    // Application entrypoint: sets L&F and shows the UI on the event thread
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(() ->
                new BolsaFamiliaCSVReaderGUI().setVisible(true)
        );
    }
}