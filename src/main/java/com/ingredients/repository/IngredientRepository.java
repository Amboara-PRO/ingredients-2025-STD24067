package com.ingredients.repository;

import com.ingredients.datasource.DataSource;
import com.ingredients.entity.*;

import java.sql.*;
import java.util.*;

public class IngredientRepository {

    private final DataSource dataSource = new DataSource();

    public List<Ingredient> findAllIngredients() {

        List<Ingredient> list = new ArrayList<>();

        try (Connection conn = dataSource.getConnection()) {

            PreparedStatement ps = conn.prepareStatement("""
                SELECT id, name, price, category FROM ingredient
            """);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Ingredient i = new Ingredient();
                i.setId(rs.getInt("id"));
                i.setName(rs.getString("name"));
                i.setPrice(rs.getDouble("price"));
                i.setCategory(
                        CategoryEnum.valueOf(rs.getString("category"))
                );
                list.add(i);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return list;
    }

    public Optional<Ingredient> findIngredientById(int id) {

        try (Connection conn = dataSource.getConnection()) {

            PreparedStatement ps = conn.prepareStatement("""
                SELECT id, name, price, category FROM ingredient WHERE id = ?
            """);

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Ingredient i = new Ingredient();
                i.setId(rs.getInt("id"));
                i.setName(rs.getString("name"));
                i.setPrice(rs.getDouble("price"));
                i.setCategory(
                        CategoryEnum.valueOf(rs.getString("category"))
                );
                return Optional.of(i);
            }

            return Optional.empty();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}