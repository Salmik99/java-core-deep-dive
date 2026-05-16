package kz.talgat.javacore.equalshashcode;

/**
 * Пример класса без переопределённых {@code equals()} и {@code hashCode()}.
 * <p>
 * Такой класс наследует поведение {@link Object}: два разных объекта не равны,
 * даже если внутри у них одинаковые данные.
 */
public final class UserWithoutEquals {

    private final String name;

    public UserWithoutEquals(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
