package com.ingredients.repository;

import com.ingredients.datasource.DataSource;
import com.ingredients.entity.*;

import java.sql.*;
import java.util.*;

public class DishRepository {

    private final DataSource dataSource = new DataSource();

    public Optional<Dish> findDishById(int id) {
        try (Connection conn = dataSource.getConnection()) {

            PreparedStatement ps = conn.prepareStatement("""
                SELECT id, name, dish_type, selling_price
                FROM dish
                WHERE id = ?
            """);

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Dish dish = new Dish();
                dish.setId(rs.getInt("id"));
                dish.setName(rs.getString("name"));
                dish.setDishType(
                        DishTypeEnum.valueOf(rs.getString("dish_type"))
                );
                dish.setPrice(rs.getDouble("selling_price"));

                dish.setDishIngredients(
                        findDishIngredientByDishId(id)
                );

                return Optional.of(dish);
            }

            return Optional.empty();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<Dish> findAllDishes() {
        List<Dish> list = new ArrayList<>();

        try (Connection conn = dataSource.getConnection()) {

            PreparedStatement ps = conn.prepareStatement("""
                SELECT id, name, dish_type, selling_price FROM dish
            """);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Dish dish = new Dish();
                dish.setId(rs.getInt("id"));
                dish.setName(rs.getString("name"));
                dish.setDishType(
                        DishTypeEnum.valueOf(rs.getString("dish_type"))
                );
                dish.setPrice(rs.getDouble("selling_price"));

                dish.setDishIngredients(
                        findDishIngredientByDishId(dish.getId())
                );

                list.add(dish);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return list;
    }

    public List<DishIngredient> findDishIngredientByDishId(int dishId) {

        List<DishIngredient> list = new ArrayList<>();

        try (Connection conn = dataSource.getConnection()) {

            PreparedStatement ps = conn.prepareStatement("""
                SELECT di.id, di.quantity_required,
                       i.id as ingredient_id, i.name, i.category, i.price
                FROM dish_ingredient di
                JOIN ingredient i ON di.id_ingredient = i.id
                WHERE di.id_dish = ?
            """);

            ps.setInt(1, dishId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                Ingredient ing = new Ingredient();
                ing.setId(rs.getInt("id_ingredient"));
                ing.setName(rs.getString("name"));
                ing.setCategory(
                        CategoryEnum.valueOf(rs.getString("category"))
                );
                ing.setPrice(rs.getDouble("price"));

                DishIngredient di = new DishIngredient();
                di.setId(rs.getInt("id"));
                di.setQuantity_required(rs.getDouble("quantity_required"));
                di.setIngredient(ing);

                list.add(di);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return list;
    }

    public void updateDishIngredients(int dishId, List<Integer> ingredientIds) {

        try (Connection conn = dataSource.getConnection()) {

            PreparedStatement delete = conn.prepareStatement(
                    "DELETE FROM dishingredient WHERE id_dish = ?");
            delete.setInt(1, dishId);
            delete.executeUpdate();

            for (Integer ingId : ingredientIds) {
                PreparedStatement insert = conn.prepareStatement(
                        "INSERT INTO dishingredient(id_ingredient, id_ingredient, quantity_required) VALUES (?, ?, 1)"
                );
                insert.setInt(1, dishId);
                insert.setInt(2, ingId);
                insert.executeUpdate();
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}