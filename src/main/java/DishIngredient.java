import java.util.List;
import java.util.Objects;

public class DishIngredient {
    private Integer id;
    private Dish dish;
    private Ingredient ingredient;
    private Double quantity_required;
    private UnitEnum unit;

    public DishIngredient(Integer id,Dish dish, Ingredient ingredient, Double quantity_required, UnitEnum unit) {
        this.id = id;
        this.dish = dish;
        this.ingredient = ingredient;
        this.quantity_required = quantity_required;
        this.unit = unit;
    }

    public DishIngredient() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Dish getDish() {
        return dish;
    }

    public void setDish(Dish dish) {
        this.dish = dish;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    public Double getQuantity_required() {
        return quantity_required;
    }

    public void setQuantity_required(Double quantity_required) {
        this.quantity_required = quantity_required;
    }

    public UnitEnum getUnit() {
        return unit;
    }

    public void setUnit(UnitEnum unit) {
        this.unit = unit;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        DishIngredient that = (DishIngredient) o;
        return Objects.equals(id, that.id) && Objects.equals(dish, that.dish) && Objects.equals(ingredient, that.ingredient) && Objects.equals(quantity_required, that.quantity_required) && unit == that.unit;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, dish, ingredient, quantity_required, unit);
    }

    @Override
    public String toString() {
        return "DishIngredient{" +
                "ingredient=" + ingredient.getName() +
                ", quantity_required=" + quantity_required +
                ", unit=" + unit +
                '}';
    }
}
