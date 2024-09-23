package fi.methics.laverca.rest.enums;


public enum Encoding {
    ENCODING_UTF8("UTF-8"),
    ENCODING_BASE64("Base64"),
    HEX("hex");

    private final String value;

    Encoding(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static Encoding fromValue(String value) {
        for (Encoding encoding : Encoding.values()) {
            if (encoding.value.equalsIgnoreCase(value)) {
                return encoding;
            }
        }
        throw new IllegalArgumentException("No enum constant for value: " + value);
    }
}
