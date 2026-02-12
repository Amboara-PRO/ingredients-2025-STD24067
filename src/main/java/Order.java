//import java.time.Instant;
//import java.util.List;
//import java.util.Objects;
//
//public class Order {
//    private Integer id;
//    private String reference;
//    private Instant creationDatetime;
//    private List<DishOrder> dishOrderList;
//    private OrderTypeEnum type;
////    private OrderStatusEnum status;
//
//    public OrderStatusEnum getOrderStatus(){
//        return status;
//    }
//
//    public Order(Integer id, String reference, Instant creationDatetime, List<DishOrder> dishOrderList, OrderTypeEnum type, OrderStatusEnum status) {
//        this.id = id;
//        this.reference = reference;
//        this.creationDatetime = creationDatetime;
//        this.dishOrderList = dishOrderList;
//        this.type = type;
//        this.status = status;
//    }
//
//    public Order() {
//
//    }
//
//    public Integer getId() {
//        return id;
//    }
//
//    public void setId(Integer id) {
//        this.id = id;
//    }
//
//    public String getReference() {
//        return reference;
//    }
//
//    public void setReference(String reference) {
//        this.reference = reference;
//    }
//
//    public Instant getCreationDatetime() {
//        return creationDatetime;
//    }
//
//    public void setCreationDatetime(Instant creationDatetime) {
//        this.creationDatetime = creationDatetime;
//    }
//
//    public List<DishOrder> getDishOrderList() {
//        return dishOrderList;
//    }
//
//    public void setDishOrderList(List<DishOrder> dishOrderList) {
//        this.dishOrderList = dishOrderList;
//    }
//
//    public OrderTypeEnum getType() {
//        return type;
//    }
//
//    public void setType(OrderTypeEnum type) {
//        this.type = type;
//    }
//
//    public OrderStatusEnum getStatus() {
//        return status;
//    }
//
//    public void setStatus(OrderStatusEnum status) {
//        this.status = status;
//    }
//
//    @Override
//    public boolean equals(Object o) {
//        if (o == null || getClass() != o.getClass()) return false;
//        Order order = (Order) o;
//        return Objects.equals(id, order.id) && Objects.equals(reference, order.reference) && Objects.equals(creationDatetime, order.creationDatetime) && Objects.equals(dishOrderList, order.dishOrderList) && type == order.type && status == order.status;
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(id, reference, creationDatetime, dishOrderList, type, status);
//    }
//
//    @Override
//    public String toString() {
//        return "Order{" +
//                "id=" + id +
//                ", reference='" + reference + '\'' +
//                ", creationDatetime=" + creationDatetime +
//                ", dishOrderList=" + dishOrderList +
//                ", type=" + type +
//                ", status=" + status +
//                '}';
//    }
//
//    public Double getTotalAmountWithoutVAT(){
//        double totalAmount = 0.0;
//        for (DishOrder dishOrder : dishOrderList) {
//            totalAmount += dishOrder.getDish().getDishCost() * dishOrder.getQuantity();
//        }
//        return totalAmount;
//    }
//    public Double getTotalAmountWithVAT(){
//        return getTotalAmountWithoutVAT() * 1.2;
//    }
//}
import java.time.Instant;
import java.util.List;
import java.util.Objects;

public class Order {
    private Integer id;
    private String reference;
    private Instant creationDatetime;
    private List<DishOrder> dishOrderList;
    private OrderStatusEnum orderStatus;
    private OrderTypeEnum type;

    public Order() {

    }

    public OrderStatusEnum getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatusEnum orderStatus) {
        this.orderStatus = orderStatus;
    }

    public OrderTypeEnum getType() {
        return type;
    }

    public void setType(OrderTypeEnum type) {
        this.type = type;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public Instant getCreationDatetime() {
        return creationDatetime;
    }

    public void setCreationDatetime(Instant creationDatetime) {
        this.creationDatetime = creationDatetime;
    }

    public List<DishOrder> getDishOrderList() {
        return dishOrderList;
    }

    public void setDishOrderList(List<DishOrder> dishOrderList) {
        this.dishOrderList = dishOrderList;
    }

    public Order(Integer id, String reference, Instant creationDatetime, List<DishOrder> dishOrderList, OrderStatusEnum orderStatus, OrderTypeEnum type) {
        this.id = id;
        this.reference = reference;
        this.creationDatetime = creationDatetime;
        this.dishOrderList = dishOrderList;
        this.orderStatus = orderStatus;
        this.type = type;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id + "," +
                "status=" + orderStatus + "," +
                "type=" + type + "," +
                "totalAmountWithoutVat=" + getTotalAmountWithoutVat() + "," +
                "totalAmountWithVat=" + getTotalAmountWithVat() + "," +
                ", reference='" + reference + '\'' +
                ", creationDatetime=" + creationDatetime +
                ", dishOrderList=" + dishOrderList +
                '}';
    }

    Double getTotalAmountWithoutVat() {
        if(dishOrderList == null) return null;
        double amount = 0.0;
        for (DishOrder dishOrder : dishOrderList) {
            amount = amount + dishOrder.getQuantity() * dishOrder.getDish().getPrice();
        }
        return amount;
    }

    Double getTotalAmountWithVat() {
        return getTotalAmountWithoutVat() == null ? null : getTotalAmountWithoutVat() * 1.2;
    }


    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Order order)) return false;
        return Objects.equals(id, order.id) && Objects.equals(reference, order.reference) && Objects.equals(creationDatetime, order.creationDatetime) && Objects.equals(dishOrderList, order.dishOrderList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, reference, creationDatetime, dishOrderList);
    }
}