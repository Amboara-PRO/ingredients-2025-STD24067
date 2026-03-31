package com.ingredients.service;

import com.ingredients.entity.Ingredient;
import com.ingredients.repository.IngredientRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class IngredientService {

    private final IngredientRepository repo;

    public IngredientService(IngredientRepository repo) {
        this.repo = repo;
    }

    public List<Ingredient> getAllIngredients() {
        return repo.findAllIngredients();
    }

    public Ingredient getIngredientById(int id) {
        return repo.findIngredientById(id)
                .orElseThrow(() ->
                        new RuntimeException("Ingredient.id=" + id + " is not found")
                );
    }
    public Map<String, Object> getStock(int id, String at, String unit) {

        repo.findIngredientById(id)
                .orElseThrow(() ->
                        new RuntimeException("Ingredient.id=" + id + " is not found")
                );

        double stockValue = 0;

        Map<String, Object> result = new HashMap<>();
        result.put("unit", unit);
        result.put("value", stockValue);

        return result;
    }
}