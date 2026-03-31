package com.ingredients.service;

import com.ingredients.entity.Dish;
import com.ingredients.entity.Ingredient;
import org.springframework.stereotype.Service;
import com.ingredients.repository.DishRepository;
import com.ingredients.repository.IngredientRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishService {

    private final DishRepository dishRepository;
    private final IngredientRepository ingredientRepository;

    public DishService(DishRepository dishRepository,
                       IngredientRepository ingredientRepository) {
        this.dishRepository = dishRepository;
        this.ingredientRepository = ingredientRepository;
    }

    // 🔥 EXACT : getAllDishes
    public List<Dish> getAllDishes() {
        return dishRepository.findAllDishes();
    }

    // 🔥 EXACT : getDishById
    public Dish getDishById(int id) {
        return dishRepository.findDishById(id)
                .orElseThrow(() ->
                        new RuntimeException("Dish.id=" + id + " is not found")
                );
    }

    // 🔥 EXACT : updateDishIngredients
    public void updateDishIngredients(int dishId, List<Ingredient> ingredients) {

        getDishById(dishId);

        List<Long> validIds = ingredients.stream()
                .map(Ingredient::getId)
                .filter(id -> ingredientRepository.findIngredientById(id).isPresent())
                .collect(Collectors.toList());

        dishRepository.updateDishIngredients(dishId, validIds);
    }
}