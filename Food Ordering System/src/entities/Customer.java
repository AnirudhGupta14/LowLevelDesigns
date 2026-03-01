package entities;

import java.util.Objects;

public class Customer {

    private final String id;
    private String name;
    private String email;
    private String address;

    public Customer(String id, String name, String email, String address) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.address = address;
    }

    // ─── Getters ──────────────────────────────────────────────

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }

    // ─── Setters ──────────────────────────────────────────────

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    // ─── Display ──────────────────────────────────────────────

    @Override
    public String toString() {
        return String.format("%s (%s) — %s", name, email, address);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Customer c = (Customer) o;
        return Objects.equals(id, c.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
