import java.util.*;
import java.io.*;
import java.nio.charset.Charset;
import java.text.Normalizer;
import java.text.NumberFormat;
import java.util.Locale;

/* BolsaFamiliaCSVReader
 * Simple CLI utility to read Bolsa Família CSV files, filter by municipality and optionally by beneficiary name,
 * display matching rows and compute totals.
 */
public class BolsaFamiliaCSVReader {

    // Normalize text by removing diacritics/accents to allow reliable string comparisons
    public static String removeDiacritics(String text) {
        return Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "");
    }

    // CLI entry point: prompts user for CSV path and filters, reads file, filters rows, prints results and totals
    public static void main(String[] args) {

        // Read user input from stdin
        Scanner scanner = new Scanner(System.in);

        // Simple header message to indicate the program has started
        System.out.println("Check Bolsa Familia CSV file");

        // Prompt for CSV file path
        System.out.print("\nType the full path to the CSV file: ");
        String filePath = scanner.nextLine();

        // Prompt for municipality name, normalize (trim, uppercase) and remove diacritics for comparison
        System.out.print("Enter the municipality name to apply the filter: ");
        String municipalitySearch = removeDiacritics(
                scanner.nextLine().trim().toUpperCase()
        );

        // Collect rows that match the municipality to process later
        List<String[]> filteredByMunicipality = new ArrayList<>();
        // Flag to detect whether the municipality was present at all
        boolean municipalityExists = false;

        // Open the CSV file with ISO-8859-1 charset and iterate lines
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(filePath), Charset.forName("ISO-8859-1")))) {

            // Read header line and split to detect columns
            String header = br.readLine();
            String[] columnHeaders = header.split(";");

            // Default column indices (fallbacks) — will attempt to detect by header text
            int municipalityIndex = 4;
            int valueIndex = 8;
            int beneficiaryNameIndex = 7;

            // Detect column indices by scanning header tokens for known keywords
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

            // Iterate over each CSV line, split fields and select rows matching the municipality
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

            // If no rows matched the municipality, inform the user via exception
            if (!municipalityExists) {
                throw new IllegalArgumentException(
                        "Municipality not found: " + municipalitySearch
                );
            }

            // Ask the user whether to further filter results by beneficiary name
            System.out.print("Do you want to filter by beneficiary name? (yes/no): ");
            String filterByNameOption = scanner.nextLine().trim().toLowerCase();

            String nameSearch = null;

            if (filterByNameOption.equals("yes")) {
                // Prompt for beneficiary name and normalize for comparison
                System.out.print("Enter the beneficiary name to filter: ");
                nameSearch = removeDiacritics(
                        scanner.nextLine().trim().toUpperCase()
                );
            }

            // Counters for matched records and sum of values
            int totalRecords = 0;
            double sumValues = 0.0;

            // Process each previously collected row and apply optional name filter
            for (String[] columns : filteredByMunicipality) {

                // Normalize the beneficiary name for comparison (remove quotes, trim, uppercase, remove diacritics)
                String beneficiaryName = removeDiacritics(
                        columns[beneficiaryNameIndex]
                                .replace("\"", "")
                                .trim()
                                .toUpperCase()
                );

                // Determine whether the current row matches the optional name filter
                boolean nameMatches = true;

                if (nameSearch != null) {
                    nameMatches = beneficiaryName.contains(nameSearch);
                }

                if (nameMatches) {

                    // Extract and normalize the value column (replace decimal comma with dot)
                    String strValue = columns[valueIndex]
                            .replace("\"", "")
                            .replace(",", ".");

                    // Print raw row (for debugging/inspection) and update counters
                    System.out.println(Arrays.toString(columns));
                    totalRecords++;

                    try {
                        double value = Double.parseDouble(strValue);
                        sumValues += value;
                    } catch (NumberFormatException ignored) {}

                    // Print a human-readable line for the matched record
                    System.out.println("Beneficiary: " + columns[beneficiaryNameIndex] +
                            " | CPF: " + columns[1] +
                            " | Value: R$ " + strValue);
                }
            }

            // Print summary totals (count and formatted currency sum)
            System.out.println("\n======================================");
            System.out.println("Total records found: " + totalRecords);
            NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
            System.out.println("Total amount paid: " + format.format(sumValues));
            System.out.println("======================================");

        } catch (IOException e) {
            // Handle file I/O errors by printing the message
            System.out.println("File read error: " + e.getMessage());
        }

        // Close the scanner resource used for user input
        scanner.close();
    }
}