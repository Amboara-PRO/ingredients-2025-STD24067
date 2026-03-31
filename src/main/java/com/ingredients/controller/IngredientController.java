package com.ingredients.controller;

import com.ingredients.entity.Ingredient;
import com.ingredients.service.IngredientService;
import com.ingredients.validator.IngredientValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class IngredientController {
    private final IngredientService service;
    private final IngredientValidator validator;

    public IngredientController(IngredientService service, IngredientValidator validator) {
        this.service = service;
        this.validator = validator;
    }

    @GetMapping("/ingredients")
    public List<Ingredient> getAllIngredients() {
        return service.getAllIngredients();
    }
    @GetMapping("/ingredients/{id}")
    public ResponseEntity<?> getIngredient(@PathVariable int id) {
        try {
            return ResponseEntity.ok(service.getIngredientById(id));
        } catch (Exception e) {
            return ResponseEntity.status(404)
                    .body("Ingredient.id=" + id + " is not found");
        }
    }
    @GetMapping("/ingredients/{id}/stock")
    public ResponseEntity<?> getStock(
            @PathVariable int id,
            @RequestParam(required = false) String at,
            @RequestParam(required = false) String unit) {

        try {
            validator.validateStockParams(at, unit);

            return ResponseEntity.ok(
                    service.getStock(id, at, unit)
            );

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(e.getMessage());

        } catch (Exception e) {
            return ResponseEntity.status(404)
                    .body("Ingredient.id=" + id + " is not found");
        }
    }
}
