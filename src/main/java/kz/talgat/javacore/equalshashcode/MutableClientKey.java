package kz.talgat.javacore.equalshashcode;

import java.util.Objects;

/**
 * Пример опасного mutable key для {@code HashMap} или {@code HashSet}.
 * <p>
 * Поле {@code iin} участвует в {@code equals()} и {@code hashCode()}, но его
 * можно изменить через setter. Если изменить его после добавления объекта в
 * hash-based коллекцию, объект может стать недоступным для поиска.
 */
public final class MutableClientKey {

    private String iin;

    public MutableClientKey(String iin) {
        this.iin = iin;
    }

    public String getIin() {
        return iin;
    }

    public void setIin(String iin) {
        this.iin = iin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MutableClientKey clientKey)) {
            return false;
        }
        return Objects.equals(iin, clientKey.iin);
    }

    @Override
    public int hashCode() {
        return Objects.hash(iin);
    }
}
