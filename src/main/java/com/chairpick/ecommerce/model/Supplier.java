package com.chairpick.ecommerce.model;

import java.util.Objects;

public class Supplier extends DomainEntity {
    private String name;

    @Override
    public void validate() {

    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Supplier supplier = (Supplier) o;
        return Objects.equals(name, supplier.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }
}
