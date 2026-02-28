import java.util.*;
import java.io.*;
import java.nio.charset.Charset;
import java.text.Normalizer;
import java.text.NumberFormat;
import java.util.Locale;

public class BolsaFamiliaCSVReader {

    public static String removeDiacritics(String text) {
        return Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "");
    }

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        System.out.println("Check Bolsa Familia CSV file");

        System.out.print("\nType the full path to the CSV file: ");
        String filePath = scanner.nextLine();

        System.out.print("Enter the municipality name to apply the filter: ");
        String municipalitySearch = removeDiacritics(
                scanner.nextLine().trim().toUpperCase()
        );

        List<String[]> filteredByMunicipality = new ArrayList<>();
        boolean municipalityExists = false;

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(filePath), Charset.forName("ISO-8859-1")))) {

            String header = br.readLine();
            String[] columnHeaders = header.split(";");

            int municipalityIndex = 4;
            int valueIndex = 8;
            int beneficiaryNameIndex = 7;

            for (int i = 0; i < columnHeaders.length; i++) {

                String column = columnHeaders[i].toUpperCase();

                if (column.contains("NOME MUNIC")) {
                    municipalityIndex = i;
                }

                if (column.contains("VALOR")) {
                    valueIndex = i;
                }

                if (column.contains("NOME FAVORECIDO")) {
                    beneficiaryNameIndex = i;
                }
            }

            String line;

            while ((line = br.readLine()) != null) {

                String[] columns = line.split(";");

                if (columns.length <= valueIndex) continue;

                String municipality = removeDiacritics(
                        columns[municipalityIndex]
                                .replace("\"", "")
                                .trim()
                                .toUpperCase()
                );

                if (municipality.equals(municipalitySearch)) {
                    municipalityExists = true;
                    filteredByMunicipality.add(columns);
                }
            }

            if (!municipalityExists) {
                throw new IllegalArgumentException(
                        "Municipality not found: " + municipalitySearch
                );
            }

            System.out.print("Do you want to filter by beneficiary name? (yes/no): ");
            String filterByNameOption = scanner.nextLine().trim().toLowerCase();

            String nameSearch = null;

            if (filterByNameOption.equals("yes")) {
                System.out.print("Enter the beneficiary name to filter: ");
                nameSearch = removeDiacritics(
                        scanner.nextLine().trim().toUpperCase()
                );
            }

            int totalRecords = 0;
            double sumValues = 0.0;

            for (String[] columns : filteredByMunicipality) {

                String beneficiaryName = removeDiacritics(
                        columns[beneficiaryNameIndex]   // ✅ AQUI ESTÁ O FILTRO CERTO
                                .replace("\"", "")
                                .trim()
                                .toUpperCase()
                );

                boolean nameMatches = true;

                if (nameSearch != null) {
                    nameMatches = beneficiaryName.contains(nameSearch);
                }

                if (nameMatches) {

                    String strValue = columns[valueIndex]
                            .replace("\"", "")
                            .replace(",", ".");

                    System.out.println(Arrays.toString(columns));
                    totalRecords++;

                    try {
                        double value = Double.parseDouble(strValue);
                        sumValues += value;
                    } catch (NumberFormatException ignored) {}

                    System.out.println("Beneficiary: " + columns[beneficiaryNameIndex] +
                            " | CPF: " + columns[1] +
                            " | Value: R$ " + strValue);
                }
            }

            System.out.println("\n======================================");
            System.out.println("Total records found: " + totalRecords);
            NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
            System.out.println("Total amount paid: " + format.format(sumValues));
            System.out.println("======================================");

        } catch (IOException e) {
            System.out.println("File read error: " + e.getMessage());
        }

        scanner.close();
    }
}