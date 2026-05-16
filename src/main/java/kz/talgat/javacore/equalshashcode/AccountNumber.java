package kz.talgat.javacore.equalshashcode;

import java.util.Objects;

/**
 * Пример value object.
 * <p>
 * Номер счёта определяется своим значением, поэтому класс immutable и
 * реализует {@code equals()} / {@code hashCode()} по полю {@code value}.
 */
public final class AccountNumber {

    private final String value;

    public AccountNumber(String value) {
        this.value = Objects.requireNonNull(value, "value must not be null");
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AccountNumber that)) {
            return false;
        }
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
