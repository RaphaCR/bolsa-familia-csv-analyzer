import javax.swing.*;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;

// Mostra os beneficiários encontrados na tabela.
final class ResultsPanel extends BaseCardPanel {
    private final DefaultTableModel tableModel;

    ResultsPanel(AppLanguage language) {
        super(language);
        setLayout(new BorderLayout(10, 10));

        tableModel = new DefaultTableModel(new Object[] {
                text("Beneficiário", "Beneficiary"),
                "CPF",
                text("Valor", "Amount")
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(tableModel);
        styleTable(table);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new RoundedLineBorder(AppTheme.BORDER, 1, 16));
        scrollPane.getViewport().setBackground(AppTheme.CARD);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(createSectionTitle("Resultados", "Results"), BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    void clear() {
        tableModel.setRowCount(0);
    }

    // Adiciona um beneficiário à lista de resultados.
    void addRecord(CsvBeneficiaryService.Beneficiary record) {
        tableModel.addRow(new Object[] {
                record.getName(),
                record.getCpf(),
                record.getAmountLabel()
        });
    }

    private void styleTable(JTable table) {
        Font tableFont = new Font(AppTheme.FONT_FAMILY, Font.PLAIN, 14);
        table.setFont(tableFont);
        table.setBackground(AppTheme.CARD);
        table.setForeground(AppTheme.TEXT);
        table.setRowHeight(30);
        table.setGridColor(new Color(229, 231, 235));
        table.setSelectionBackground(new Color(219, 234, 254));
        table.setSelectionForeground(AppTheme.TEXT);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setFillsViewportHeight(true);
        table.setAutoCreateRowSorter(true);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font(AppTheme.FONT_FAMILY, Font.BOLD, 14));
        header.setBackground(AppTheme.TABLE_HEADER);
        header.setForeground(AppTheme.TEXT);
        header.setBorder(new MatteBorder(0, 0, 1, 0, AppTheme.BORDER));
        header.setPreferredSize(new Dimension(header.getWidth(), 34));

        DefaultTableCellRenderer leftRenderer = createRenderer(tableFont, SwingConstants.LEFT);
        DefaultTableCellRenderer centerRenderer = createRenderer(tableFont, SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(leftRenderer);
        table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);

        table.getColumnModel().getColumn(0).setPreferredWidth(420);
        table.getColumnModel().getColumn(1).setPreferredWidth(180);
        table.getColumnModel().getColumn(2).setPreferredWidth(140);
    }

    private DefaultTableCellRenderer createRenderer(Font font, int alignment) {
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setFont(font);
        renderer.setBackground(AppTheme.CARD);
        renderer.setForeground(AppTheme.TEXT);
        renderer.setHorizontalAlignment(alignment);
        return renderer;
    }
}
