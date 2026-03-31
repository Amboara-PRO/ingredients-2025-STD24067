package com.ingredients.repository;

import com.ingredients.entity.CategoryEnum;
import com.ingredients.entity.Ingredient;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class IngredientRepository {

    private final JdbcTemplate jdbcTemplate;

    public IngredientRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Ingredient> findAllIngredients() {

        String sql = "SELECT id, name, price, category FROM ingredient";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {

            Ingredient ingredient = new Ingredient();
            ingredient.setId(rs.getInt("id"));
            ingredient.setName(rs.getString("name"));
            ingredient.setPrice(rs.getDouble("price"));
            ingredient.setCategory(
                    CategoryEnum.valueOf(rs.getString("category"))
            );
            return ingredient;
        });
    }

    public Optional<Ingredient> findIngredientById(int id) {

        String sql = """
            SELECT id, name, price, category
            FROM ingredient
            WHERE id = ?
        """;

        List<Ingredient> result = jdbcTemplate.query(sql, (rs, rowNum) -> {

            Ingredient ingredient = new Ingredient();
            ingredient.setId(rs.getInt("id"));
            ingredient.setName(rs.getString("name"));
            ingredient.setPrice(rs.getDouble("price"));
            ingredient.setCategory(
                    CategoryEnum.valueOf(rs.getString("category"))
            );

            return ingredient;

        }, id);

        return result.stream().findFirst();
    }
}