package kz.talgat.javacore.equalshashcode;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Намеренно неправильный пример.
 * <p>
 * {@code equals()} сравнивает клиентов по ИИН, но {@code hashCode()} возвращает
 * разное значение для разных экземпляров. Это нарушает главный контракт:
 * если {@code a.equals(b) == true}, то {@code a.hashCode() == b.hashCode()}.
 */
public final class ClientWithBrokenHashCode {

    private static final AtomicInteger HASH_SEQUENCE = new AtomicInteger();

    private final String iin;
    private final int brokenHashCode = HASH_SEQUENCE.incrementAndGet();

    public ClientWithBrokenHashCode(String iin) {
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
        if (!(o instanceof ClientWithBrokenHashCode client)) {
            return false;
        }
        return Objects.equals(iin, client.iin);
    }

    @Override
    public int hashCode() {
        return brokenHashCode;
    }
}
