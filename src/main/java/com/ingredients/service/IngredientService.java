package com.ingredients.service;

import com.ingredients.entity.*;
import com.ingredients.repository.*;

import java.util.*;

public class IngredientService {

    private final IngredientRepository repo = new IngredientRepository();
    private final StockMovementRepository stockRepo = new StockMovementRepository();

    public List<Ingredient> getAllIngredients() {
        return repo.findAllIngredients();
    }

    public Ingredient getIngredientById(int id) {
        return repo.findIngredientById(id)
                .orElseThrow(() ->
                        new RuntimeException("Ingredient.id=" + id + " is not found")
                );
    }

    public Map<String, Object> getStock(int id, String unit) {

        getIngredientById(id);

        List<StockMovement> movements =
                stockRepo.findStockMovementByIngredientId(id);

        double stock = 0;

        for (StockMovement sm : movements) {
            if (sm.getType() == MouvementTypeEnum.IN) {
                stock += sm.getValue().getQuantity();
            } else {
                stock -= sm.getValue().getQuantity();
            }
        }

        Map<String, Object> res = new HashMap<>();
        res.put("unit", unit);
        res.put("value", stock);

        return res;
    }
}