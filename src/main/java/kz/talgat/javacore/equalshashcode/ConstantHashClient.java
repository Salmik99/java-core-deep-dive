package kz.talgat.javacore.equalshashcode;

import java.util.Objects;

/**
 * Пример hash collision.
 * <p>
 * У всех объектов одинаковый {@code hashCode()}, но {@code equals()} всё равно
 * различает клиентов по ИИН. Hash-based коллекции умеют работать с такими
 * коллизиями, хотя большое количество коллизий ухудшает производительность.
 */
public final class ConstantHashClient {

    private final String iin;

    public ConstantHashClient(String iin) {
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
        if (!(o instanceof ConstantHashClient client)) {
            return false;
        }
        return Objects.equals(iin, client.iin);
    }

    @Override
    public int hashCode() {
        return 1;
    }
}
