package kz.talgat.javacore;

import kz.talgat.javacore.objectmodel.ImmutableUser;
import kz.talgat.javacore.objectmodel.MutableUser;
import kz.talgat.javacore.objectmodel.UserService;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ObjectModelTest {

    private final UserService userService = new UserService();

    /**
     * Присваивание объектной переменной копирует ссылку, а не создаёт новый объект.
     */
    @Test
    void assignmentCopiesReferenceNotObject() {
        MutableUser user1 = new MutableUser("Talgat", List.of("USER"));
        MutableUser user2 = user1;

        user2.setName("Arman");

        assertThat(user2).isSameAs(user1);
        assertThat(user1.getName()).isEqualTo("Arman");
    }

    /**
     * Два объекта с одинаковыми полями всё равно остаются разными объектами в памяти.
     */
    @Test
    void sameStateDoesNotMeanSameReference() {
        MutableUser user1 = new MutableUser("Talgat", List.of("USER"));
        MutableUser user2 = new MutableUser("Talgat", List.of("USER"));

        assertThat(user2).isNotSameAs(user1);
    }

    /**
     * Метод получает копию ссылки и может через неё изменить mutable-объект.
     */
    @Test
    void methodCanMutateObjectThroughCopiedReference() {
        MutableUser user = new MutableUser("Talgat", List.of("USER"));

        userService.changeMutableUserName(user, "Arman");

        assertThat(user.getName()).isEqualTo("Arman");
    }

    /**
     * Переприсваивание параметра внутри метода не меняет ссылку у вызывающего кода.
     */
    @Test
    void methodCannotReplaceCallerReference() {
        MutableUser user = new MutableUser("Talgat", List.of("USER"));

        userService.reassignMutableUser(user);

        assertThat(user.getName()).isEqualTo("Talgat");
        assertThat(user.getRoles()).containsExactly("USER");
    }

    /**
     * MutableUser сохраняет переданный список как есть, поэтому внешний список остаётся общим состоянием.
     */
    @Test
    void mutableUserKeepsSharedRolesReference() {
        List<String> roles = new ArrayList<>(List.of("USER"));
        MutableUser user = new MutableUser("Talgat", roles);

        roles.add("ADMIN");

        assertThat(user.getRoles()).containsExactly("USER", "ADMIN");
    }

    /**
     * Getter mutable-объекта отдаёт внутренний список, и внешний код может изменить состояние объекта.
     */
    @Test
    void mutableUserExposesInternalRolesCollection() {
        MutableUser user = new MutableUser("Talgat", new ArrayList<>(List.of("USER")));

        user.getRoles().add("ADMIN");

        assertThat(user.getRoles()).containsExactly("USER", "ADMIN");
    }

    /**
     * ImmutableUser делает defensive copy, поэтому изменение исходного списка не влияет на объект.
     */
    @Test
    void immutableUserCopiesRolesInConstructor() {
        List<String> roles = new ArrayList<>(List.of("USER"));
        ImmutableUser user = new ImmutableUser("Talgat", roles);

        roles.add("ADMIN");

        assertThat(user.getRoles()).containsExactly("USER");
    }

    /**
     * ImmutableUser возвращает неизменяемый список ролей.
     */
    @Test
    void immutableUserReturnsUnmodifiableRoles() {
        ImmutableUser user = new ImmutableUser("Talgat", List.of("USER"));

        assertThatThrownBy(() -> user.getRoles().add("ADMIN"))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    /**
     * Добавление роли к immutable-объекту создаёт новый объект и не меняет старый.
     */
    @Test
    void addRoleReturnsNewImmutableUserWithoutChangingOriginal() {
        ImmutableUser user = new ImmutableUser("Talgat", List.of("USER"));

        ImmutableUser updatedUser = userService.addRole(user, "ADMIN");

        assertThat(updatedUser).isNotSameAs(user);
        assertThat(user.getRoles()).containsExactly("USER");
        assertThat(updatedUser.getRoles()).containsExactly("USER", "ADMIN");
    }

    /**
     * final запрещает переприсвоить переменную, но не делает сам mutable-объект неизменяемым.
     */
    @Test
    void finalVariableCanStillPointToMutableObject() {
        final MutableUser user = new MutableUser("Talgat", List.of("USER"));

        user.setName("Arman");

        assertThat(user.getName()).isEqualTo("Arman");
    }
}
