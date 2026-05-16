package kz.talgat.javacore.equalshashcode;

import java.util.Objects;

/**
 * Намеренно рискованный пример базового класса с {@code equals()} через
 * {@code instanceof}.
 * <p>
 * Если наследник добавит свои поля в сравнение, можно нарушить symmetry:
 * {@code parent.equals(child)} и {@code child.equals(parent)} дадут разные
 * результаты.
 */
public class PersonByIin {

    private final String iin;

    public PersonByIin(String iin) {
        this.iin = iin;
    }

    public String getIin() {
        return iin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PersonByIin person)) {
            return false;
        }
        return Objects.equals(iin, person.iin);
    }

    @Override
    public int hashCode() {
        return Objects.hash(iin);
    }
}
