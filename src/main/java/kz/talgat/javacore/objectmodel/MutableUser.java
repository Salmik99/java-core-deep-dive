package kz.talgat.javacore.objectmodel;

import java.util.List;

public class MutableUser {
    private String name;
    private List<String> roles;

    public MutableUser(String name, List<String> roles) {
        this.name = name;
        this.roles = roles;
    }

    public String getName() {
        return name;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setName(String name) {
        this.name = name;
    }
}
