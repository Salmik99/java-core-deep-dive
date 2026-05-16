package kz.talgat.javacore.objectmodel;

import java.util.List;

public class UserService {

    public void changeMutableUserName(MutableUser user, String newName) {
        user.setName(newName);
    }

    public void reassignMutableUser(MutableUser user) {
        user = new MutableUser("NewName", List.of());
    }

    public ImmutableUser addRole(ImmutableUser user, String role) {
        List<String> roles = user.getRoles();
        roles.add(role);
        return new ImmutableUser(user.getName(), roles);
    }
}