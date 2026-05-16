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

    @Test
    void assignmentCopiesReferenceNotObject() {
        MutableUser user1 = new MutableUser("Talgat", List.of("USER"));
        MutableUser user2 = user1;

        user2.setName("Arman");

        assertThat(user2).isSameAs(user1);
        assertThat(user1.getName()).isEqualTo("Arman");
    }

    @Test
    void sameStateDoesNotMeanSameReference() {
        MutableUser user1 = new MutableUser("Talgat", List.of("USER"));
        MutableUser user2 = new MutableUser("Talgat", List.of("USER"));

        assertThat(user2).isNotSameAs(user1);
    }

    @Test
    void methodCanMutateObjectThroughCopiedReference() {
        MutableUser user = new MutableUser("Talgat", List.of("USER"));

        userService.changeMutableUserName(user, "Arman");

        assertThat(user.getName()).isEqualTo("Arman");
    }

    @Test
    void methodCannotReplaceCallerReference() {
        MutableUser user = new MutableUser("Talgat", List.of("USER"));

        userService.reassignMutableUser(user);

        assertThat(user.getName()).isEqualTo("Talgat");
        assertThat(user.getRoles()).containsExactly("USER");
    }

    @Test
    void mutableUserKeepsSharedRolesReference() {
        List<String> roles = new ArrayList<>(List.of("USER"));
        MutableUser user = new MutableUser("Talgat", roles);

        roles.add("ADMIN");

        assertThat(user.getRoles()).containsExactly("USER", "ADMIN");
    }

    @Test
    void mutableUserExposesInternalRolesCollection() {
        MutableUser user = new MutableUser("Talgat", new ArrayList<>(List.of("USER")));

        user.getRoles().add("ADMIN");

        assertThat(user.getRoles()).containsExactly("USER", "ADMIN");
    }

    @Test
    void immutableUserCopiesRolesInConstructor() {
        List<String> roles = new ArrayList<>(List.of("USER"));
        ImmutableUser user = new ImmutableUser("Talgat", roles);

        roles.add("ADMIN");

        assertThat(user.getRoles()).containsExactly("USER");
    }

    @Test
    void immutableUserReturnsUnmodifiableRoles() {
        ImmutableUser user = new ImmutableUser("Talgat", List.of("USER"));

        assertThatThrownBy(() -> user.getRoles().add("ADMIN"))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void addRoleReturnsNewImmutableUserWithoutChangingOriginal() {
        ImmutableUser user = new ImmutableUser("Talgat", List.of("USER"));

        ImmutableUser updatedUser = userService.addRole(user, "ADMIN");

        assertThat(updatedUser).isNotSameAs(user);
        assertThat(user.getRoles()).containsExactly("USER");
        assertThat(updatedUser.getRoles()).containsExactly("USER", "ADMIN");
    }

    @Test
    void finalVariableCanStillPointToMutableObject() {
        final MutableUser user = new MutableUser("Talgat", List.of("USER"));

        user.setName("Arman");

        assertThat(user.getName()).isEqualTo("Arman");
    }
}
