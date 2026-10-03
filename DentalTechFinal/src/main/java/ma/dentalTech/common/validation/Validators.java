package ma.dentalTech.common.validation;


import ma.dentalTech.common.exceptions.ValidationException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.regex.Pattern;


public final class Validators {
    private static final Pattern EMAIL = Pattern.compile("^[^\s@]+@[^\\s@]+\\.[^\\s@]+$");
    private static final Pattern PHONE = Pattern.compile("^[0-9+\\-\\s]{6,20}$");

    private Validators() {
    }

    public static void notBlank(String v, String field) throws ValidationException {
        if (v == null || v.trim().isEmpty()) throw new ValidationException(field + " est obligatoire");
    }

    public static void email(String v) throws ValidationException {
        if (v != null && !EMAIL.matcher(v).matches()) throw new ValidationException("Email invalide");
    }

    public static void phone(String v) throws ValidationException {
        if (v != null && !PHONE.matcher(v).matches()) throw new ValidationException("Téléphone invalide");
    }

    public static void minLen(String v, int n, String field) throws ValidationException {
        if (v == null || v.length() < n)
            throw new ValidationException(field + " doit contenir au moins " + n + " caractères");
    }

    public static void checkNotNull(Object object, String message) throws ValidationException {
        if (object == null) {
            throw new ValidationException(message);
        }
    }

    public static void checkNotEmpty(String str, String message) throws ValidationException {
        if (str == null || str.trim().isEmpty()) {
            throw new ValidationException(message);
        }
    }

    public static void checkDateFutureOrToday(LocalDate date, String message) throws ValidationException {
        if (date != null && date.isBefore(LocalDate.now())) {
            throw new ValidationException(message);
        }
    }

    public static void checkHeureCoherente(LocalTime debut, LocalTime fin, String message) throws ValidationException {
        if (debut != null && fin != null && !fin.isAfter(debut)) {
            throw new ValidationException(message);
        }
    }
}
