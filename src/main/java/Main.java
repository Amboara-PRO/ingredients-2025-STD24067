import java.time.Instant;
import java.util.List;

public class Main {
    public static void main(String[] args) {
      DataRetriever dataRetriever = new DataRetriever();
//      Dish dish = dataRetriever.findDishById(1);
//        System.out.println(dish);
//
//        List<DishIngredient> i = dataRetriever.findDishIngredientByDishId(4);
//        System.out.println(i);

//        List<Ingredient> createdIngredients = dataRetriever.createIngredients(List.of(new Ingredient(null, "Pain", CategoryEnum.OTHER, 600.0)));
//        System.out.println(createdIngredients);
//
//        Dish dish = new Dish();
//        dish.setId(2);
//        dish.setDishType(DishTypeEnum.MAIN);
//        dish.setName("Poulet grillé");
//        dish.setPrice(12000.00);

//        Ingredient poulet = new Ingredient();
//        poulet.setId(3);
//        poulet.setName("Poulet");
//
//
//        DishIngredient di1 = new DishIngredient();
//        di1.setIngredient(poulet);
//        di1.setDish(dish);
//        di1.setQuantity_required(1.00);
//        di1.setUnit(UnitEnum.KG);
//
//        dish.setIngredients(List.of(di1));
//
//        dataRetriever.saveDish(dish);
//
//        System.out.println(dish);

//      Dish dish = dataRetriever.findDishById(1);
//        System.out.println(dish.getDishCost());
//
//        System.out.println(dish);
//        System.out.println("Coût du plat = " + dish.getGrossMargin());

//        Ingredient laitue = new Ingredient();
//        laitue.setId(1);
//        laitue.setName("Laitue");
//        laitue.setPrice(800.0);
//        laitue.setCategory(CategoryEnum.VEGETABLE);
//
//        StockMovement in1 = new StockMovement();
//        in1.setId(11);
//        in1.setValue(new StockValue(10.0, UnitEnum.KG));
//        in1.setType(MouvementTypeEnum.IN);
//        in1.setCreationDatetime(Instant.parse("2024-01-05T09:00:00Z"));
//
//        laitue.setStockMovementList(List.of(in1));
//        dataRetriever.saveIngredient(laitue);

        Instant t = Instant.parse("2024-01-06T12:00:00Z");
        List<Ingredient> ingredients = dataRetriever.findAllIngredients();
        for (Ingredient ingredient : ingredients) {
            StockValue stockValue = ingredient.getStockValueAt(t);

            if (Instant t >= Instant.parse("2024-01-06T12:00:00Z").equals(t)) {
                System.out.println(
                        ingredient.getId() + " " +
                                ingredient.getName()
                                + " → Stock = "
                                + stockValue.getQuantity()
                                + " " + stockValue.getUnit()
                                + " " + ingredient.getStockValueAt(t)
                );
            }

        }
//
//      System.out.println(ingredients.get(0).getStockValueAt(t));
    }
}
