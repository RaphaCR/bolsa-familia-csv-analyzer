
import java.util.Arrays;
import java.io.*;
import java.nio.charset.Charset;
import java.util.Scanner;
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

        int lineCounter = 0;

        System.out.println("Check Bolsa Familia CSV file");

        System.out.print("\nType de full path to the CSV file: ");
        String filePath = scanner.nextLine();

        System.out.print("Enter the municipatily name to apply the filter: ");
        String municipalitySearch = removeDiacritics(
                scanner.nextLine().trim().toUpperCase()
        );

        int totalRecords = 0;
        double sumValues = 0.0;

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(filePath), Charset.forName("ISO-8859-1")))) {

            String line;

            String header = br.readLine();
            String[] columnHeaders = header.split(";");

            System.out.println("Full header:");
            System.out.println(header);

            for (int i = 0; i < columnHeaders.length; i++) {
                System.out.println(i + " -> " + columnHeaders[i]);
            }

            int municipalityIndex = 4;
            int valueIndex = 8;

            for (int i = 0; i < columnHeaders.length; i++) {
                if (columnHeaders[i].toUpperCase().contains("MUNICIPALITY NAME")) {
                    municipalityIndex = i;
                }
                if (columnHeaders[i].toUpperCase().contains("Payment amount")) {
                    valueIndex = i;
                }
            }

            while ((line = br.readLine()) != null) {

                String[] columns = line.split(";");

                lineCounter++;

                if (lineCounter % 500000 == 0) {
                    System.out.println("Processed: " + lineCounter + " lines...");
                }

                String strValue = columns[valueIndex]
                        .replace("\"", "")
                        .replace(",", ".");
                String municipality = removeDiacritics(
                        columns[municipalityIndex]
                                .replace("\"", "")
                                .trim()
                                .toUpperCase()
                );

                if (columns.length > 3) {


                    if (municipality.equals(municipalitySearch)) {

                        System.out.println(Arrays.toString(columns));
                        totalRecords++;

                        try {
                            double value = Double.parseDouble(strValue);
                            sumValues += value;
                        } catch (NumberFormatException e) {

                        }

                        System.out.println("Beneficiary: " + columns[0] +
                                " | CPF: " + columns[1] +
                                " | Value: R$ " + strValue);
                    }
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

