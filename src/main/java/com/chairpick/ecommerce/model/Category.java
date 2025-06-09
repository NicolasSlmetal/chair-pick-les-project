package com.chairpick.ecommerce.model;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.Objects;

@SuperBuilder
@Getter
public class Category extends DomainEntity {

    private String name;

    @Override
    public void validate() {

    }

    @Override
    public String toString() {
        return "Category{" +
                "name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return Objects.equals(name, category.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }
}
