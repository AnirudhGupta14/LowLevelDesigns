package models;

import enums.FoodCategory;
import enums.FoodStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class Food {
    private final String foodId;
    private final String name;
    private final String description;
    private final double price;
    private FoodStatus status;
    private FoodCategory category;
    private final int preparationTime; // in minutes
    private int availableQuantity;

    public Food(String name, String description, double price, FoodCategory category, int preparationTime, int availableQuantity) {
        this.foodId = UUID.randomUUID().toString();
        this.name = name;
        this.description = description;
        this.price = price;
        this.status = FoodStatus.AVAILABLE;
        this.category = category;
        this.preparationTime = preparationTime;
        this.availableQuantity = availableQuantity;
    }

    public boolean isAvailable() {
        return status == FoodStatus.AVAILABLE && availableQuantity > 0;
    }

    public void reduceQuantity(int quantity) {
        if (quantity > 0 && availableQuantity >= quantity) {
            availableQuantity -= quantity;
            if (availableQuantity == 0) {
                status = FoodStatus.NOT_AVAILABLE;
            }
        }
    }

    public void addQuantity(int quantity) {
        if (quantity > 0) {
            availableQuantity += quantity;
            if (status == FoodStatus.NOT_AVAILABLE && availableQuantity > 0) {
                status = FoodStatus.AVAILABLE;
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Food food = (Food) obj;
        return foodId.equals(food.foodId);
    }

    @Override
    public int hashCode() {
        return foodId.hashCode();
    }

    @Override
    public String toString() {
        return "Food{" +
                "foodId='" + foodId + '\'' +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", status=" + status +
                ", category='" + category + '\'' +
                ", availableQuantity=" + availableQuantity +
                '}';
    }
}
