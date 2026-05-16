package kz.talgat.javacore.equalshashcode;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class EqualsHashCodeTest {

    /**
     * Если класс не переопределяет equals, логическое сравнение работает как сравнение ссылок.
     */
    @Test
    void objectEqualsUsesIdentityWhenClassDoesNotOverrideEquals() {
        UserWithoutEquals first = new UserWithoutEquals("Talgat");
        UserWithoutEquals second = new UserWithoutEquals("Talgat");

        assertThat(first).isNotSameAs(second);
        assertThat(first).isNotEqualTo(second);
    }

    /**
     * Два разных объекта могут быть логически равны, если equals реализован по business key.
     */
    @Test
    void clientByIinHasLogicalEqualityAndSameHashCode() {
        ClientByIin first = new ClientByIin("123456789012", "Talgat Lukpanov");
        ClientByIin second = new ClientByIin("123456789012", "Talgat L.");

        assertThat(first).isNotSameAs(second);
        assertThat(first).isEqualTo(second);
        assertThat(first.hashCode()).isEqualTo(second.hashCode());
    }

    /**
     * Базовые правила контракта equals: reflexive, symmetric, transitive, consistent и null-safe.
     */
    @Test
    void clientByIinFollowsEqualsContract() {
        ClientByIin first = new ClientByIin("123456789012", "Talgat");
        ClientByIin second = new ClientByIin("123456789012", "Talgat L.");
        ClientByIin third = new ClientByIin("123456789012", "T. Lukpanov");

        assertThat(first.equals(first)).isTrue();
        assertThat(first.equals(second)).isTrue();
        assertThat(second.equals(first)).isTrue();
        assertThat(second.equals(third)).isTrue();
        assertThat(first.equals(third)).isTrue();
        assertThat(first.equals(second)).isTrue();
        assertThat(first.equals(null)).isFalse();
    }

    /**
     * Если equals говорит, что объекты равны, а hashCode разный, HashSet может хранить дубликаты.
     */
    @Test
    void brokenHashCodeBreaksHashSetUniqueness() {
        ClientWithBrokenHashCode first = new ClientWithBrokenHashCode("123456789012");
        ClientWithBrokenHashCode second = new ClientWithBrokenHashCode("123456789012");
        Set<ClientWithBrokenHashCode> clients = new HashSet<>();

        clients.add(first);
        clients.add(second);

        assertThat(first).isEqualTo(second);
        assertThat(first.hashCode()).isNotEqualTo(second.hashCode());
        assertThat(clients).hasSize(2);
    }

    /**
     * HashMap находит значение по новому равному ключу, когда equals и hashCode согласованы.
     */
    @Test
    void hashMapFindsValueWhenEqualsAndHashCodeMatch() {
        Map<ClientByIin, String> clients = new HashMap<>();
        clients.put(new ClientByIin("123456789012", "Talgat"), "saved");

        String result = clients.get(new ClientByIin("123456789012", "Other name"));

        assertThat(result).isEqualTo("saved");
    }

    /**
     * Одинаковый hashCode не означает равенство: при коллизии HashMap дополнительно вызывает equals.
     */
    @Test
    void hashCollisionStillUsesEquals() {
        ConstantHashClient first = new ConstantHashClient("123456789012");
        ConstantHashClient second = new ConstantHashClient("999999999999");
        Map<ConstantHashClient, String> clients = new HashMap<>();

        clients.put(first, "first");
        clients.put(second, "second");

        assertThat(first.hashCode()).isEqualTo(second.hashCode());
        assertThat(first).isNotEqualTo(second);
        assertThat(clients.get(new ConstantHashClient("123456789012"))).isEqualTo("first");
        assertThat(clients.get(new ConstantHashClient("999999999999"))).isEqualTo("second");
    }

    /**
     * Mutable key опасен: после изменения поля из hashCode объект ищется уже в другом bucket.
     */
    @Test
    void mutableKeyBecomesLostAfterChangingHashField() {
        MutableClientKey key = new MutableClientKey("123456789012");
        Map<MutableClientKey, String> clients = new HashMap<>();
        clients.put(key, "Talgat");

        key.setIin("999999999999");

        assertThat(clients.get(key)).isNull();
        assertThat(clients.containsKey(key)).isFalse();
        assertThat(clients).hasSize(1);
    }

    /**
     * Immutable value object удобно использовать как ключ, потому что его hashCode стабилен.
     */
    @Test
    void valueObjectCanBeUsedAsMapKey() {
        Map<AccountNumber, String> accounts = new HashMap<>();
        accounts.put(new AccountNumber("KZ123"), "main account");

        String result = accounts.get(new AccountNumber("KZ123"));

        assertThat(result).isEqualTo("main account");
    }

    /**
     * Snapshot DTO сравнивает все значимые поля, поэтому разное ФИО делает объекты неравными.
     */
    @Test
    void snapshotDtoComparesAllFields() {
        ClientSnapshotDto first = new ClientSnapshotDto("123456789012", "Talgat Lukpanov");
        ClientSnapshotDto second = new ClientSnapshotDto("123456789012", "Talgat L.");

        assertThat(first).isNotEqualTo(second);
    }

    /**
     * Business-key DTO игнорирует ФИО в equality и сравнивает только ИИН.
     */
    @Test
    void businessKeyDtoIgnoresFullNameInEquality() {
        ClientBusinessKeyDto first = new ClientBusinessKeyDto("123456789012", "Talgat Lukpanov");
        ClientBusinessKeyDto second = new ClientBusinessKeyDto("123456789012", "Talgat L.");

        assertThat(first).isEqualTo(second);
    }

    /**
     * Две новые entity с null id не должны автоматически считаться равными.
     */
    @Test
    void entityByIdDoesNotTreatNewEntitiesAsEqual() {
        ClientEntityById first = new ClientEntityById(null, "123456789012");
        ClientEntityById second = new ClientEntityById(null, "123456789012");

        assertThat(first).isNotEqualTo(second);
    }

    /**
     * Persisted entity можно сравнивать по id, если id уже есть у обеих сторон.
     */
    @Test
    void entityByIdComparesOnlyNonNullId() {
        ClientEntityById first = new ClientEntityById(10L, "123456789012");
        ClientEntityById second = new ClientEntityById(10L, "999999999999");
        Set<ClientEntityById> clients = new HashSet<>();

        clients.add(first);
        clients.add(second);

        assertThat(first).isEqualTo(second);
        assertThat(clients).hasSize(1);
    }

    /**
     * Record автоматически сравнивает все компоненты, а не один business key.
     */
    @Test
    void recordComparesAllComponents() {
        ClientRecordDto first = new ClientRecordDto("123456789012", "Talgat Lukpanov");
        ClientRecordDto same = new ClientRecordDto("123456789012", "Talgat Lukpanov");
        ClientRecordDto differentName = new ClientRecordDto("123456789012", "Talgat L.");

        assertThat(first).isEqualTo(same);
        assertThat(first).isNotEqualTo(differentName);
    }

    /**
     * Наследование может нарушить symmetry, если parent и child сравниваются по разным правилам.
     */
    @Test
    void inheritanceCanBreakEqualsSymmetry() {
        PersonByIin person = new PersonByIin("123456789012");
        EmployeeByIinAndNumber employee = new EmployeeByIinAndNumber("123456789012", "EMP-1");

        assertThat(person.equals(employee)).isTrue();
        assertThat(employee.equals(person)).isFalse();
    }
}
