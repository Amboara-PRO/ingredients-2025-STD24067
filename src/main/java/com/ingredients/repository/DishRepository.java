package com.ingredients.repository;

import com.ingredients.entity.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class DishRepository {

    private final JdbcTemplate jdbcTemplate;

    public DishRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // 🔥 EXACT : findDishById
    public Optional<Dish> findDishById(int id) {

        String sql = """
            SELECT dish.id as dish_id,
                   dish.name as dish_name,
                   dish_type,
                   dish.selling_price as dish_price
            FROM dish
            WHERE dish.id = ?
        """;

        List<Dish> result = jdbcTemplate.query(sql, (rs, rowNum) -> {

            Dish dish = new Dish();
            dish.setId(rs.getInt("dish_id"));
            dish.setName(rs.getString("dish_name"));
            dish.setDishType(
                    DishTypeEnum.valueOf(rs.getString("dish_type"))
            );

            dish.setPrice(
                    rs.getObject("dish_price") == null
                            ? null
                            : rs.getDouble("dish_price")
            );

            // 🔥 EXACT logique DataRetriever
            dish.setDishIngredients(
                    findDishIngredientByDishId(dish.getId())
            );

            return dish;

        }, id);

        return result.stream().findFirst();
    }

    // 🔥 EXACT : findDishIngredientByDishId
    public List<DishIngredient> findDishIngredientByDishId(int dishId) {

        String sql = """
            SELECT di.id as di_id,
                   di.quantity,
                   i.id as ingredient_id,
                   i.name,
                   i.category,
                   i.price
            FROM dish_ingredient di
            JOIN ingredient i ON di.ingredient_id = i.id
            WHERE di.dish_id = ?
        """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {

            Ingredient ingredient = new Ingredient();
            ingredient.setId(rs.getInt("ingredient_id"));
            ingredient.setName(rs.getString("name"));
            ingredient.setCategory(
                    CategoryEnum.valueOf(rs.getString("category"))
            );
            ingredient.setPrice(rs.getDouble("price"));

            DishIngredient di = new DishIngredient();
            di.setId(rs.getInt("di_id"));
            di.setQuantity_required(rs.getDouble("quantity"));
            di.setIngredient(ingredient);

            return di;

        }, dishId);
    }

    // 🔥 EXACT : findAllDishes
    public List<Dish> findAllDishes() {

        String sql = """
            SELECT dish.id as dish_id,
                   dish.name as dish_name,
                   dish_type,
                   dish.selling_price as dish_price
            FROM dish
        """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {

            Dish dish = new Dish();
            dish.setId(rs.getInt("dish_id"));
            dish.setName(rs.getString("dish_name"));
            dish.setDishType(
                    DishTypeEnum.valueOf(rs.getString("dish_type"))
            );

            dish.setPrice(
                    rs.getObject("dish_price") == null
                            ? null
                            : rs.getDouble("dish_price")
            );

            dish.setDishIngredients(
                    findDishIngredientByDishId(dish.getId())
            );

            return dish;
        });
    }

    // 🔥 EXACT logique update
    public void updateDishIngredients(Long dishId, List<Long> ingredientIds) {

        jdbcTemplate.update(
                "DELETE FROM dish_ingredient WHERE dish_id = ?",
                dishId
        );

        for (Long ingredientId : ingredientIds) {
            jdbcTemplate.update(
                    "INSERT INTO dish_ingredient(dish_id, ingredient_id, quantity) VALUES (?, ?, 1)",
                    dishId,
                    ingredientId
            );
        }
    }
}