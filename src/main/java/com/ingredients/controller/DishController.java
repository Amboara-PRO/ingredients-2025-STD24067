package com.ingredients.controller;

import com.ingredients.entity.Dish;
import com.ingredients.entity.Ingredient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.ingredients.service.DishService;
import com.ingredients.validator.DishValidator;

import java.util.List;

@RestController
@RequestMapping("/dishes")
public class DishController {

    private final DishService service;
    private final DishValidator validator;

    public DishController(DishService service,
                          DishValidator validator) {
        this.service = service;
        this.validator = validator;
    }

    // ✅ GET /dishes
    @GetMapping
    public List<Dish> getAllDishes() {
        return service.getAllDishes();
    }

    // ✅ PUT /dishes/{id}/ingredients
    @PutMapping("/{id}/ingredients")
    public ResponseEntity<?> updateDishIngredients(
            @PathVariable Long id,
            @RequestBody(required = false) List<Ingredient> ingredients) {

        try {
            validator.validateIngredientList(ingredients);

            service.updateDishIngredients(id, ingredients);

            return ResponseEntity.ok("Updated successfully");

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(e.getMessage());

        } catch (Exception e) {
            return ResponseEntity.status(404)
                    .body("Dish.id=" + id + " is not found");
        }
    }
}