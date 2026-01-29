import java.util.List;
import java.util.Objects;

public class Dish {
    private Integer id;
    private Double price;
    private String name;
    private DishTypeEnum dishType;
    private List<DishIngredient> dishIngredients;

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getDishCost() {
        if (dishIngredients == null) return 0.0;

        double total = 0.0;

        for (DishIngredient di : dishIngredients) {
            if (di.getQuantity_required() == null) {
                throw new RuntimeException("Quantity is null for ingredient " + di.getIngredient().getId());
            }
            if (di.getIngredient() == null || di.getIngredient().getPrice() == null) {
                throw new RuntimeException("Ingredient price is null");
            }

            total += di.getIngredient().getPrice() * di.getQuantity_required();
        }
        return total;
    }

    public Double getGrossMargin() {
        if (price == null) {
            throw new RuntimeException("Selling price is null");
        }
        return price - getDishCost();
    }

    public Dish() {
    }

    public Dish(Integer id,Double price, String name, DishTypeEnum dishType, List<DishIngredient> dishIngredients) {
        this.id = id;
        this.price = price;
        this.name = name;
        this.dishType = dishType;
        this.dishIngredients = dishIngredients;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DishTypeEnum getDishType() {
        return dishType;
    }

    public void setDishType(DishTypeEnum dishType) {
        this.dishType = dishType;
    }

    public List<DishIngredient> getDishIngredients() {
        return dishIngredients;
    }

    public void setDishIngredients(List<DishIngredient> dishIngredients) {
        if (dishIngredients == null) {
            this.dishIngredients = null;
            return;
        }
        for (int i = 0; i < dishIngredients.size(); i++) {
            dishIngredients.get(i).setDish(this);
        }
        this.dishIngredients = dishIngredients;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Dish dish = (Dish) o;
        return Objects.equals(id, dish.id) && Objects.equals(name, dish.name) && dishType == dish.dishType && Objects.equals(dishIngredients, dish.dishIngredients);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, dishType, dishIngredients);
    }

    @Override
    public String toString() {
        return "Dish{" +
                "id=" + id +
                ", price=" + price +
                ", name='" + name + '\'' +
                ", dishType=" + dishType +
                ", dishIngredients=" + dishIngredients +
                '}';
    }

}
