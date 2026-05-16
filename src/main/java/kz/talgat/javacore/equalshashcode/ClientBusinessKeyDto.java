package kz.talgat.javacore.equalshashcode;

import java.util.Objects;

/**
 * Пример DTO, который сравнивается по business key.
 * <p>
 * Здесь равенство определяется только ИИН. Это означает, что два объекта с
 * одинаковым ИИН и разным ФИО будут равны.
 */
public final class ClientBusinessKeyDto {

    private final String iin;
    private final String fullName;

    public ClientBusinessKeyDto(String iin, String fullName) {
        this.iin = iin;
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
        if (!(o instanceof ClientBusinessKeyDto that)) {
            return false;
        }
        return Objects.equals(iin, that.iin);
    }

    @Override
    public int hashCode() {
        return Objects.hash(iin);
    }
}
