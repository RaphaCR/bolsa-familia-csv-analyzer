import javax.swing.*;
import java.awt.*;

// Campos e botões para escolher o arquivo e montar a busca.
final class FiltersPanel extends BaseCardPanel {
    private final JTextField txtFilePath;
    private final JTextField txtMunicipality;
    private final JCheckBox chkFilterByName;
    private final JTextField txtBeneficiaryName;

    FiltersPanel(AppLanguage language, Runnable onBrowse, Runnable onSearch, Runnable onClear) {
        super(language);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridwidth = 3;
        add(createSectionTitle("Filtros", "Filters"), gbc);

        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        add(createFieldLabel("Arquivo CSV", "CSV file"), gbc);

        txtFilePath = createTextField();
        txtFilePath.setEditable(false);
        gbc.gridx = 1;
        gbc.weightx = 1;
        add(txtFilePath, gbc);

        JButton btnBrowse = createButton("Selecionar arquivo", "Select file",
                AppTheme.PRIMARY, AppTheme.PRIMARY_HOVER);
        btnBrowse.addActionListener(event -> onBrowse.run());
        gbc.gridx = 2;
        gbc.weightx = 0;
        add(btnBrowse, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        add(createFieldLabel("Município", "Municipality"), gbc);

        txtMunicipality = createTextField();
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        add(txtMunicipality, gbc);

        chkFilterByName = new JCheckBox(text("Filtrar por beneficiário", "Filter by beneficiary"));
        chkFilterByName.setFont(new Font(AppTheme.FONT_FAMILY, Font.PLAIN, 14));
        chkFilterByName.setForeground(AppTheme.TEXT);
        chkFilterByName.setOpaque(false);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 3;
        add(chkFilterByName, gbc);

        gbc.gridy = 4;
        gbc.gridwidth = 1;
        add(createFieldLabel("Nome do beneficiário", "Beneficiary name"), gbc);

        txtBeneficiaryName = createTextField();
        txtBeneficiaryName.setEnabled(false);
        chkFilterByName.addActionListener(event -> txtBeneficiaryName.setEnabled(chkFilterByName.isSelected()));
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        add(txtBeneficiaryName, gbc);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);
        JButton btnSearch = createButton("Pesquisar", "Search", AppTheme.SUCCESS, AppTheme.SUCCESS_HOVER);
        JButton btnClear = createButton("Limpar", "Clear", AppTheme.DANGER, AppTheme.DANGER_HOVER);
        btnSearch.addActionListener(event -> onSearch.run());
        btnClear.addActionListener(event -> onClear.run());
        actions.add(btnSearch);
        actions.add(btnClear);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.EAST;
        add(actions, gbc);
    }

    String getFilePath() {
        return txtFilePath.getText().trim();
    }

    void setFilePath(String filePath) {
        txtFilePath.setText(filePath);
    }

    String getMunicipality() {
        return txtMunicipality.getText().trim();
    }

    boolean isNameFilterSelected() {
        return chkFilterByName.isSelected();
    }

    String getBeneficiaryName() {
        return txtBeneficiaryName.getText().trim();
    }

    void clear() {
        txtFilePath.setText("");
        txtMunicipality.setText("");
        txtBeneficiaryName.setText("");
        chkFilterByName.setSelected(false);
        txtBeneficiaryName.setEnabled(false);
    }
}
