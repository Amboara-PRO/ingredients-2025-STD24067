import java.util.List;

public class Main {
    public static void main(String[] args) {
      DataRetriever dataRetriever = new DataRetriever();
//
//      Dish dish = dataRetriever.findDishById(1);
//        System.out.println(dish);

//        List<DishIngredient> i = dataRetriever.findDishIngredientByDishId(4);
//        System.out.println(i);

//        List<Ingredient> createdIngredients = dataRetriever.createIngredients(List.of(new Ingredient(null, "Pain", CategoryEnum.OTHER, 600.0)));
//        System.out.println(createdIngredients);

        Dish dish = new Dish();
        dish.setId(2);
        dish.setDishType(DishTypeEnum.MAIN);
        dish.setName("Poulet grillé");
        dish.setPrice(12000.00);

        Ingredient poulet = new Ingredient();
        poulet.setId(3);


        DishIngredient di1 = new DishIngredient();
        di1.setIngredient(poulet);
        di1.setQuantity_required(1.00);
        di1.setUnit(UnitEnum.KG);

        dish.setIngredients(List.of(di1));

        dataRetriever.saveDish(dish);

        System.out.println(dish);
//        System.out.println(dish.getDishCost());
//
//        Dish dish = dataRetriever.findDishById(1);
//        System.out.println(dish);
//        System.out.println("Coût du plat = " + dish.getGrossMargin());
    }
}
