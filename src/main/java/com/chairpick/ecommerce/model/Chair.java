package com.chairpick.ecommerce.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
@SuperBuilder
public class Chair extends DomainEntity{

    private String name;
    private String description;
    private double sellPrice;
    private double height;
    private double width;
    private double length;
    private double weight;
    private double averageRating;
    private PricingGroup pricingGroup;
    private List<Item> items;
    private boolean isActive;
    private List<Category> categories;

    public Chair() {
        super();
    }

    @Override
    public void validate() {

    }

    @Override
    public String toString() {
        return "Chair{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", sellPrice=" + sellPrice +
                ", height=" + height +
                ", width=" + width +
                ", length=" + length +
                ", weight=" + weight +
                ", averageRating=" + averageRating +
                ", pricingGroup=" + pricingGroup +
                ", items=" + items +
                ", categories=" + categories +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Chair chair = (Chair) o;
        return Double.compare(getHeight(), chair.getHeight()) == 0 && Double.compare(getWidth(), chair.getWidth()) == 0 && Double.compare(getLength(), chair.getLength()) == 0 && Double.compare(getWeight(), chair.getWeight()) == 0 && Objects.equals(getName(), chair.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getHeight(), getWidth(), getLength(), getWeight());
    }
}
