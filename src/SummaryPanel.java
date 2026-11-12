import javax.swing.*;
import java.awt.*;

// Mostra quantos registros foram encontrados e o valor total.
final class SummaryPanel extends BaseCardPanel {
    private final JLabel lblTotalRecords;
    private final JLabel lblTotalAmount;

    SummaryPanel(AppLanguage language) {
        super(language);
        setLayout(new GridLayout(1, 2, 10, 0));

        lblTotalRecords = new JLabel(text("Total de registros: 0", "Total records: 0"));
        lblTotalRecords.setFont(new Font(AppTheme.FONT_FAMILY, Font.BOLD, 15));
        lblTotalRecords.setForeground(AppTheme.TEXT);

        lblTotalAmount = new JLabel(text("Valor total: ", "Total amount: ") + language.formatCurrency(0));
        lblTotalAmount.setFont(new Font(AppTheme.FONT_FAMILY, Font.BOLD, 15));
        lblTotalAmount.setForeground(AppTheme.SUCCESS);

        add(lblTotalRecords);
        add(lblTotalAmount);
    }

    // Atualiza os totais depois da busca.
    void showTotals(int totalRecords, double totalAmount) {
        lblTotalRecords.setText(text("Total de registros: ", "Total records: ") + totalRecords);
        lblTotalAmount.setText(text("Valor total: ", "Total amount: ") + language.formatCurrency(totalAmount));
    }

    void reset() {
        showTotals(0, 0);
    }
}
