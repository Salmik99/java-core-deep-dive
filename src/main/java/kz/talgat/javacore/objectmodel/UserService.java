package kz.talgat.javacore.objectmodel;

import java.util.ArrayList;
import java.util.List;

public class UserService {

    public void changeMutableUserName(MutableUser user, String newName) {
        user.setName(newName);
    }

    public void reassignMutableUser(MutableUser user) {
        user = new MutableUser("Dias", List.of("ADMIN"));
        user.setName("Nurlan");
    }

    public ImmutableUser addRole(ImmutableUser user, String role) {
        List<String> roles = new ArrayList<>(user.getRoles());
        roles.add(role);
        return new ImmutableUser(user.getName(), roles);
    }
}
