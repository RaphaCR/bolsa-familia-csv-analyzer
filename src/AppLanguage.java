import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

// Guarda o idioma escolhido e cuida dos textos e valores exibidos.
enum AppLanguage {
    PORTUGUESE(Locale.forLanguageTag("pt-BR")),
    ENGLISH(Locale.US);

    private final Locale locale;

    AppLanguage(Locale locale) {
        this.locale = locale;
    }

    String text(String portuguese, String english) {
        return this == PORTUGUESE ? portuguese : english;
    }

    Locale getLocale() {
        return locale;
    }

    String formatCurrency(double amount) {
        NumberFormat format = NumberFormat.getCurrencyInstance(locale);
        format.setCurrency(Currency.getInstance("BRL"));
        return format.format(amount);
    }
}
