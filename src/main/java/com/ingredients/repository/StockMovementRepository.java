package com.ingredients.repository;

import com.ingredients.entity.StockMovement;
import com.ingredients.entity.StockValue;
import com.ingredients.entity.MouvementTypeEnum;

import com.ingredients.entity.UnitEnum;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public class StockMovementRepository {

    private final JdbcTemplate jdbcTemplate;

    public StockMovementRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // 🔥 nom exact + adapté à ton entity
    public List<StockMovement> findStockMovementByIngredientId(int ingredientId) {

        String sql = """
            SELECT id, quantity, unit, type, creation_datetime
            FROM stock_movement
            WHERE ingredient_id = ?
        """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {

            StockMovement sm = new StockMovement();

            sm.setId(rs.getInt("id"));

            StockValue value = new StockValue(
                    rs.getDouble("quantity"),
                    UnitEnum.valueOf(rs.getString("unit"))
            );
            sm.setValue(value);

            sm.setType(
                    MouvementTypeEnum.valueOf(rs.getString("type"))
            );

            sm.setCreationDatetime(
                    rs.getTimestamp("creation_datetime").toInstant()
            );

            return sm;

        }, ingredientId);
    }
}