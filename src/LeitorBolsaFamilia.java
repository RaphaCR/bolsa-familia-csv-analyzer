
import java.util.Arrays;
import java.io.*;
import java.nio.charset.Charset;
import java.util.Scanner;
import java.text.Normalizer;
import java.text.NumberFormat;
import java.util.Locale;

    public class LeitorBolsaFamilia {

        public static String removerAcentos(String texto) {
            return Normalizer.normalize(texto, Normalizer.Form.NFD)
                    .replaceAll("[^\\p{ASCII}]", "");
        }

        public static void main(String[] args) {

            Scanner scanner = new Scanner(System.in);

            int contadorLinhas = 0;

            System.out.println("======================================");
            System.out.println("  CONSULTA CSV - BOLSA FAMÍLIA");
            System.out.println("======================================");

            System.out.print("Digite o caminho completo do CSV: ");
            String caminho = scanner.nextLine();

            System.out.print("Digite o nome do município para filtrar: ");
            String municipioBusca = removerAcentos(
                    scanner.nextLine().trim().toUpperCase()
            );

            int totalRegistros = 0;
            double somaValores = 0.0;

            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(new FileInputStream(caminho), Charset.forName("ISO-8859-1")))) {

                String linha;

                String cabecalho = br.readLine();
                String[] nomesColunas = cabecalho.split(";");

                System.out.println("CABEÇALHO COMPLETO:");
                System.out.println(cabecalho);

                for (int i = 0; i < nomesColunas.length; i++) {
                    System.out.println(i + " -> " + nomesColunas[i]);
                }

                int indiceMunicipio = 4;
                int indiceValor = 8;

                for (int i = 0; i < nomesColunas.length; i++) {
                    if (nomesColunas[i].toUpperCase().contains("NOME MUNICIPIO")) {
                        indiceMunicipio = i;
                    }
                    if (nomesColunas[i].toUpperCase().contains("VALOR PARCELA")) {
                        indiceValor = i;
                    }
                }

                while ((linha = br.readLine()) != null) {

                    String[] colunas = linha.split(";");

                    contadorLinhas++;

                    if (contadorLinhas % 500000 == 0) {
                        System.out.println("Processadas: " + contadorLinhas + " linhas...");
                    }

                    String valorStr = colunas[indiceValor]
                            .replace("\"", "")
                            .replace(",", ".");
                    String municipio = removerAcentos(
                            colunas[indiceMunicipio]
                                    .replace("\"", "")   // remove aspas
                                    .trim()
                                    .toUpperCase()
                    );

                    if (colunas.length > 3) {


                        if (municipio.equals(municipioBusca)) {

                            System.out.println(Arrays.toString(colunas));
                            totalRegistros++;

                            try {
                                double valor = Double.parseDouble(valorStr);
                                somaValores += valor;
                            } catch (NumberFormatException e) {
                                // Ignora linha com valor inválido
                            }

                            System.out.println("Beneficiário: " + colunas[0] +
                                    " | CPF: " + colunas[1] +
                                    " | Valor: R$ " + valorStr);
                        }
                    }
                }

                System.out.println("\n======================================");
                System.out.println("Total de registros encontrados: " + totalRegistros);
                NumberFormat formato = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
                System.out.println("Soma total paga: " + formato.format(somaValores));
                System.out.println("======================================");

            } catch (IOException e) {
                System.out.println("Erro ao ler o arquivo: " + e.getMessage());
            }

            scanner.close();
        }
    }

