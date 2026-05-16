package kz.talgat.javacore.equalshashcode;

import java.util.Objects;

/**
 * Пример business equality: клиент считается тем же самым клиентом по ИИН.
 * <p>
 * {@code fullName} хранится как обычное поле, но не участвует в сравнении.
 * Это осознанное бизнес-правило, а не универсальное правило для всех DTO/entity.
 */
public final class ClientByIin {

    private final String iin;
    private final String fullName;

    public ClientByIin(String iin, String fullName) {
        this.iin = Objects.requireNonNull(iin, "iin must not be null");
        this.fullName = fullName;
    }

    public String getIin() {
        return iin;
    }

    public String getFullName() {
        return fullName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ClientByIin client)) {
            return false;
        }
        return Objects.equals(iin, client.iin);
    }

    @Override
    public int hashCode() {
        return Objects.hash(iin);
    }
}
