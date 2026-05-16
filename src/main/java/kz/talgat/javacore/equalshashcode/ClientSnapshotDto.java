package kz.talgat.javacore.equalshashcode;

import java.util.Objects;

/**
 * Пример DTO, который сравнивается как snapshot данных.
 * <p>
 * Два DTO равны только тогда, когда совпадают все значимые поля.
 */
public final class ClientSnapshotDto {

    private final String iin;
    private final String fullName;

    public ClientSnapshotDto(String iin, String fullName) {
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
        if (!(o instanceof ClientSnapshotDto that)) {
            return false;
        }
        return Objects.equals(iin, that.iin)
                && Objects.equals(fullName, that.fullName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(iin, fullName);
    }
}
