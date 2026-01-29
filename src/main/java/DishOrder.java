import java.util.Objects;

public class DishOrder {
    private Integer id;
    private Dish dish;
    private Order order;
    private Integer quantity;

    public DishOrder(Integer id, Dish dish, Order order, Integer quantity) {
        this.id = id;
        this.dish = dish;
        this.order = order;
        this.quantity = quantity;
    }

    public DishOrder() {

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

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        DishOrder dishOrder = (DishOrder) o;
        return Objects.equals(id, dishOrder.id) && Objects.equals(dish, dishOrder.dish) && Objects.equals(order, dishOrder.order) && Objects.equals(quantity, dishOrder.quantity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, dish, order, quantity);
    }

    @Override
    public String toString() {
        return "DishOrder{" +
                "id=" + id +
                ", dish=" + dish +
                ", order=" + order +
                ", quantity=" + quantity +
                '}';
    }
}
