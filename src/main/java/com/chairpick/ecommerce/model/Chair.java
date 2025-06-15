package com.chairpick.ecommerce.model;

import com.chairpick.ecommerce.utils.ErrorCode;
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

        if (name == null || name.isBlank()) {
            getErrors().add(ErrorCode.CHAIR_NAME_REQUIRED);
        }
        if (description == null || description.isBlank()) {
            getErrors().add(ErrorCode.CHAIR_DESCRIPTION_REQUIRED);
        }

        if (width <= 0) {
            getErrors().add(ErrorCode.CHAIR_INVALID_WIDTH);
        }

        if (height <= 0) {
            getErrors().add(ErrorCode.CHAIR_INVALID_HEIGHT);
        }

        if (length <= 0) {
            getErrors().add(ErrorCode.CHAIR_INVALID_LENGTH);
        }

        if (weight <= 0) {
            getErrors().add(ErrorCode.CHAIR_INVALID_WEIGHT);
        }

        if (averageRating < 0 || averageRating > 5) {
            getErrors().add(ErrorCode.CHAIR_INVALID_AVERAGE_RATING);
        }

        if (pricingGroup == null) {
            getErrors().add(ErrorCode.CHAIR_PRICING_GROUP_REQUIRED);
        }

        if (categories == null || categories.isEmpty()) {
            getErrors().add(ErrorCode.CHAIR_CATEGORIES_REQUIRED);
        }

        verifyIfHasErrors();
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
