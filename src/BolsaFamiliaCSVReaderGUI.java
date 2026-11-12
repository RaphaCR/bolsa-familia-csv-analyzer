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

/* Esta janela reúne as opções para carregar e consultar os dados do Bolsa Família.
 * Aqui ficam os filtros, a lista de resultados e o resumo dos valores encontrados.
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

    // Cores usadas para manter o mesmo visual em toda a janela
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

    // Ao abrir a janela, define o título e o tamanho e prepara o conteúdo
    public BolsaFamiliaCSVReaderGUI() {
        setTitle("Bolsa Família CSV");
        setSize(1080, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        initUI();
    }

    // Retira os acentos para facilitar a comparação dos textos
    public static String removeDiacritics(String text) {
        return Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "");
    }

    // Organiza o cabeçalho, os filtros, os resultados e o resumo
    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(16, 16));
        // Aqui ficam reunidas as partes principais da janela.
        mainPanel.setBackground(BACKGROUND);
        mainPanel.setBorder(new EmptyBorder(18, 18, 18, 18));

        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        mainPanel.add(createCenterPanel(), BorderLayout.CENTER);
        mainPanel.add(createFooterPanel(), BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    // Mostra o nome do programa e uma breve explicação
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        // O título e a explicação ficam juntos no topo da tela.
        panel.setOpaque(false);

        JLabel title = new JLabel("Bolsa Família CSV");
        title.setFont(new Font("Work Sans", Font.BOLD, 26));
        title.setForeground(TEXT);

        JLabel subtitle = new JLabel("Load a CSV file, filter by municipality and beneficiary, and view the results.");
        subtitle.setFont(new Font("Work Sans", Font.PLAIN, 14));
        subtitle.setForeground(MUTED);

        JPanel textPanel = new JPanel();
        // Organiza o título acima do texto de orientação.
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        textPanel.add(title);
        textPanel.add(Box.createVerticalStrut(4));
        textPanel.add(subtitle);

        panel.add(textPanel, BorderLayout.WEST);
        return panel;
    }

    // Reúne os filtros e a tabela com os resultados
    private JPanel createCenterPanel() {
        JPanel center = new JPanel(new BorderLayout(16, 16));
        // Os filtros ficam acima da tabela com os resultados.
        center.setOpaque(false);
        center.add(createFiltersCard(), BorderLayout.NORTH);
        center.add(createTableCard(), BorderLayout.CENTER);
        return center;
    }

    // Nesta parte, o usuário escolhe o arquivo e informa o que deseja pesquisar
    // Os botões permitem selecionar o arquivo, buscar os dados e limpar os campos
    private JPanel createFiltersCard() {
        JPanel card = createCardPanel();
        // Este painel reúne os campos e botões usados na pesquisa.
        card.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        JLabel lblSection = new JLabel("Filters");
        lblSection.setFont(new Font("Work Sans", Font.BOLD, 18));
        lblSection.setForeground(TEXT);

        JLabel lblFile = createLabel("CSV file");
        txtFilePath = createTextField();
        txtFilePath.setEditable(false);

        JButton btnBrowse = createStyledButton("Select file", PRIMARY, PRIMARY_HOVER);

        JLabel lblMunicipality = createLabel("Municipality");
        txtMunicipality = createTextField();

        chkFilterByName = new JCheckBox("Filter by beneficiary");
        chkFilterByName.setFont(new Font("Work Sans", Font.PLAIN, 14));
        chkFilterByName.setForeground(TEXT);
        chkFilterByName.setOpaque(false);

        JLabel lblName = createLabel("Beneficiary name");
        txtBeneficiaryName = createTextField();
        txtBeneficiaryName.setEnabled(false);

        JButton btnSearch = createStyledButton("Search", SUCCESS, SUCCESS_HOVER);
        JButton btnClear = createStyledButton("Clear", DANGER, DANGER_HOVER);

        chkFilterByName.addActionListener(e -> txtBeneficiaryName.setEnabled(chkFilterByName.isSelected()));

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
        // Mantém as ações juntas e alinhadas à direita.
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

    // Exibe os dados encontrados em uma tabela com beneficiário, CPF e valor
    private JPanel createTableCard() {
        JPanel card = createCardPanel();
        // A tabela ocupa o espaço principal deste painel.
        card.setLayout(new BorderLayout(10, 10));

        JLabel lblSection = new JLabel("Results");
        lblSection.setFont(new Font("Work Sans", Font.BOLD, 18));
        lblSection.setForeground(TEXT);

        tableModel = new DefaultTableModel(new Object[] { "Beneficiary", "CPF", "Amount" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        styleTable(table);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new RoundedLineBorder(BORDER, 1, 16));
        scrollPane.getViewport().setBackground(CARD);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        card.add(lblSection, BorderLayout.NORTH);
        card.add(scrollPane, BorderLayout.CENTER);

        return card;
    }

    // Mostra quantos registros foram encontrados e a soma dos valores
    private JPanel createFooterPanel() {
        JPanel panel = createCardPanel();
        // O resumo mostra a quantidade de registros e o valor total.
        panel.setLayout(new GridLayout(1, 2, 10, 0));

        lblTotalRecords = new JLabel("Total records: 0");
        lblTotalRecords.setFont(new Font("Work Sans", Font.BOLD, 15));
        lblTotalRecords.setForeground(TEXT);

        lblTotalAmount = new JLabel("Total amount: R$ 0,00");
        lblTotalAmount.setFont(new Font("Work Sans", Font.BOLD, 15));
        lblTotalAmount.setForeground(SUCCESS);

        panel.add(lblTotalRecords);
        panel.add(lblTotalAmount);

        return panel;
    }

    // Cria um painel com borda e espaço interno para separar cada parte da tela
    private JPanel createCardPanel() {
        JPanel panel = new JPanel();
        // Usa o mesmo fundo e a mesma borda nas áreas da tela.
        panel.setBackground(CARD);
        panel.setBorder(new CompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(16, 16, 16, 16)));
        return panel;
    }

    private static class RoundedLineBorder extends AbstractBorder {
        private final Color color;
        private final int thickness;
        private final int arcSize;

        private RoundedLineBorder(Color color, int thickness, int arcSize) {
            this.color = color;
            this.thickness = thickness;
            this.arcSize = arcSize;
        }

        @Override
        public void paintBorder(Component component, Graphics graphics, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) graphics.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(thickness));
            g2.drawRoundRect(x, y, width - 1, height - 1, arcSize, arcSize);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component component) {
            return new Insets(thickness, thickness, thickness, thickness);
        }
    }

    // Prepara os textos que aparecem ao lado dos campos
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Work Sans", Font.PLAIN, 14));
        label.setForeground(TEXT);
        return label;
    }

    // Deixa os campos de texto com o mesmo tamanho e aparência
    private JTextField createTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Work Sans", Font.PLAIN, 14));
        field.setForeground(TEXT);
        field.setBackground(CARD);
        field.setPreferredSize(new Dimension(250, 38));
        field.setBorder(new CompoundBorder(
                new RoundedLineBorder(new Color(203, 213, 225), 1, 14),
                new EmptyBorder(8, 12, 8, 12)));
        return field;
    }

    // Deixa os botões com as cores do programa e destaca quando o mouse passa por
    // cima
    private JButton createStyledButton(String text, Color baseColor, Color hoverColor) {
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
        button.setFont(new Font("Work Sans", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(baseColor);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(10, 18, 10, 18));
        button.setContentAreaFilled(false);
        button.setOpaque(false);

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

    // Ajusta a aparência da tabela para facilitar a leitura
    private void styleTable(JTable table) {
        Font tableFont = new Font("Work Sans", Font.PLAIN, 14);
        table.setFont(tableFont);
        table.setBackground(CARD);
        table.setForeground(TEXT);
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
        header.setFont(new Font("Work Sans", Font.BOLD, 14));
        header.setBackground(TABLE_HEADER);
        header.setForeground(TEXT);
        header.setBorder(new MatteBorder(0, 0, 1, 0, BORDER));
        header.setPreferredSize(new Dimension(header.getWidth(), 34));

        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        leftRenderer.setFont(tableFont);
        leftRenderer.setBackground(CARD);
        leftRenderer.setForeground(TEXT);
        leftRenderer.setHorizontalAlignment(SwingConstants.LEFT);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setFont(tableFont);
        centerRenderer.setBackground(CARD);
        centerRenderer.setForeground(TEXT);
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        table.getColumnModel().getColumn(0).setCellRenderer(leftRenderer);
        table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);

        table.getColumnModel().getColumn(0).setPreferredWidth(420);
        table.getColumnModel().getColumn(1).setPreferredWidth(180);
        table.getColumnModel().getColumn(2).setPreferredWidth(140);
    }

    // Abre a janela de arquivos e preenche o campo com o arquivo escolhido
    private void chooseFile() {
        JFileChooser chooser = new JFileChooser();
        int option = chooser.showOpenDialog(this);

        if (option == JFileChooser.APPROVE_OPTION) {
            txtFilePath.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }

    // Limpa os campos e volta a tabela e o resumo ao estado inicial
    private void clearFields() {
        txtFilePath.setText("");
        txtMunicipality.setText("");
        txtBeneficiaryName.setText("");
        chkFilterByName.setSelected(false);
        txtBeneficiaryName.setEnabled(false);

        tableModel.setRowCount(0);

        lblTotalRecords.setText("Total records: 0");
        lblTotalAmount.setText("Total amount: R$ 0,00");
    }

    // Confere os dados informados, lê o arquivo e separa os registros da busca
    // Depois, mostra os resultados e calcula o total encontrado
    private void processCSV() {
        tableModel.setRowCount(0);

        String filePath = txtFilePath.getText().trim();
        String municipalityInput = txtMunicipality.getText().trim();

        if (filePath.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select a CSV file.");
            return;
        }

        if (municipalityInput.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter a municipality.");
            return;
        }

        String municipalitySearch = removeDiacritics(municipalityInput.toUpperCase());
        String nameSearch = null;

        if (chkFilterByName.isSelected()) {
            nameSearch = removeDiacritics(txtBeneficiaryName.getText().trim().toUpperCase());
            if (nameSearch.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Enter the beneficiary's name.");
                return;
            }
        }

        List<String[]> filtered = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(filePath), Charset.forName("ISO-8859-1")))) {

            String header = br.readLine();
            if (header == null || header.isBlank()) {
                JOptionPane.showMessageDialog(this, "The CSV file is empty.");
                return;
            }

            String[] columnsHeader = header.split(";");

            int municipalityIndex = 4;
            int valueIndex = 8;
            int nameIndex = 7;
            int cpfIndex = 1;

            for (int i = 0; i < columnsHeader.length; i++) {
                String col = columnsHeader[i].toUpperCase();

                if (col.contains("NOME MUNIC"))
                    municipalityIndex = i;
                if (col.contains("VALOR"))
                    valueIndex = i;
                if (col.contains("NOME FAVORECIDO"))
                    nameIndex = i;
                if (col.contains("CPF"))
                    cpfIndex = i;
            }

            String line;

            while ((line = br.readLine()) != null) {
                String[] cols = line.split(";");

                if (cols.length <= Math.max(valueIndex, Math.max(nameIndex, municipalityIndex))) {
                    continue;
                }

                String municipality = removeDiacritics(
                        cols[municipalityIndex].replace("\"", "").trim().toUpperCase());

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

                tableModel.addRow(new Object[] {
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
            lblTotalRecords.setText("Total records: " + total);
            lblTotalAmount.setText("Total amount: " + format.format(sum));

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error reading the file: " + e.getMessage());
        }
    }

    // Inicia o programa e mostra a janela
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(() -> new BolsaFamiliaCSVReaderGUI().setVisible(true));
    }
}