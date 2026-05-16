package kz.talgat.javacore.equalshashcode;

import java.util.Objects;

/**
 * Упрощённый пример entity equality без зависимости от JPA.
 * <p>
 * Две entity считаются равными только если у обеих уже есть ненулевой id и эти
 * id совпадают. Две новые entity с {@code id == null} не считаются равными.
 */
public class ClientEntityById {

    private Long id;
    private String iin;

    public ClientEntityById(Long id, String iin) {
        this.id = id;
        this.iin = iin;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
        if (!(o instanceof ClientEntityById that)) {
            return false;
        }
        if (id == null || that.id == null) {
            return false;
        }
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
