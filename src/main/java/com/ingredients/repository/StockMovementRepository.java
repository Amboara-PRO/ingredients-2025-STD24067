package com.ingredients.repository;

import com.ingredients.datasource.DataSource;
import com.ingredients.entity.*;

import java.sql.*;
import java.time.Instant;
import java.util.*;

public class StockMovementRepository {

    private final DataSource dataSource = new DataSource();

    public List<StockMovement> findStockMovementByIngredientId(int id) {

        List<StockMovement> list = new ArrayList<>();

        try (Connection conn = dataSource.getConnection()) {

            PreparedStatement ps = conn.prepareStatement("""
                SELECT id, quantity, unit, type, creation_datetime
                FROM stock_movement
                WHERE ingredient_id = ?
            """);

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                StockValue value = new StockValue(
                        rs.getDouble("quantity"),
                        UnitEnum.valueOf(rs.getString("unit"))
                );

                StockMovement sm = new StockMovement();
                sm.setId(rs.getInt("id"));
                sm.setValue(value);
                sm.setType(
                        MouvementTypeEnum.valueOf(rs.getString("type"))
                );
                sm.setCreationDatetime(
                        rs.getTimestamp("creation_datetime").toInstant()
                );

                list.add(sm);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return list;
    }
}