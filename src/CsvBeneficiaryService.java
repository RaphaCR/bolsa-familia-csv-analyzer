import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

// Lê o CSV e busca os beneficiários sem depender da tela.
final class CsvBeneficiaryService {
    // Procura pelo município e, se informado, também pelo nome.
    Result search(String filePath, String municipalityInput, String beneficiaryNameInput) throws IOException {
        String municipalitySearch = normalize(municipalityInput);
        String nameSearch = beneficiaryNameInput == null ? null : normalize(beneficiaryNameInput);
        List<String[]> municipalityRecords = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(filePath), Charset.forName("ISO-8859-1")))) {
            String header = reader.readLine();
            if (header == null || header.isBlank()) {
                throw new EmptyCsvException();
            }

            String[] columnsHeader = header.split(";");
            int municipalityIndex = 4;
            int valueIndex = 8;
            int nameIndex = 7;
            int cpfIndex = 1;

            for (int i = 0; i < columnsHeader.length; i++) {
                String column = columnsHeader[i].toUpperCase();
                if (column.contains("NOME MUNIC")) {
                    municipalityIndex = i;
                }
                if (column.contains("VALOR")) {
                    valueIndex = i;
                }
                if (column.contains("NOME FAVORECIDO")) {
                    nameIndex = i;
                }
                if (column.contains("CPF")) {
                    cpfIndex = i;
                }
            }

            String line;
            while ((line = reader.readLine()) != null) {
                String[] columns = line.split(";");
                if (columns.length <= Math.max(valueIndex, Math.max(nameIndex, municipalityIndex))) {
                    continue;
                }

                String municipality = normalize(columns[municipalityIndex].replace("\"", "").trim());
                if (municipality.equals(municipalitySearch)) {
                    municipalityRecords.add(columns);
                }
            }

            return createResult(municipalityRecords, nameIndex, cpfIndex, valueIndex, nameSearch);
        }
    }

    private Result createResult(
            List<String[]> rows,
            int nameIndex,
            int cpfIndex,
            int valueIndex,
            String nameSearch) {
        List<Beneficiary> beneficiaries = new ArrayList<>();
        double totalAmount = 0;

        for (String[] columns : rows) {
            String name = columns[nameIndex].replace("\"", "").trim();
            String cpf = cpfIndex < columns.length ? columns[cpfIndex].replace("\"", "").trim() : "";
            String value = columns[valueIndex].replace("\"", "").replace(",", ".").trim();

            if (nameSearch != null && !normalize(name).contains(nameSearch)) {
                continue;
            }

            beneficiaries.add(new Beneficiary(name, cpf, "R$ " + value));
            try {
                totalAmount += Double.parseDouble(value);
            } catch (NumberFormatException ignored) {
            }
        }

        return new Result(beneficiaries, totalAmount);
    }

    private static String normalize(String text) {
        return removeDiacritics(text.toUpperCase());
    }

    static String removeDiacritics(String text) {
        return Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "");
    }

    static final class Beneficiary {
        private final String name;
        private final String cpf;
        private final String amountLabel;

        private Beneficiary(String name, String cpf, String amountLabel) {
            this.name = name;
            this.cpf = cpf;
            this.amountLabel = amountLabel;
        }

        String getName() {
            return name;
        }

        String getCpf() {
            return cpf;
        }

        String getAmountLabel() {
            return amountLabel;
        }
    }

    static final class Result {
        private final List<Beneficiary> beneficiaries;
        private final double totalAmount;

        private Result(List<Beneficiary> beneficiaries, double totalAmount) {
            this.beneficiaries = beneficiaries;
            this.totalAmount = totalAmount;
        }

        List<Beneficiary> getBeneficiaries() {
            return beneficiaries;
        }

        double getTotalAmount() {
            return totalAmount;
        }
    }

    static final class EmptyCsvException extends IOException {
        private EmptyCsvException() {
            super("CSV file is empty");
        }
    }
}
