package com.ingredients.service;

import com.ingredients.entity.*;
import com.ingredients.repository.*;

import java.util.*;
import java.util.stream.Collectors;

public class DishService {

    private final DishRepository dishRepository = new DishRepository();
    private final IngredientRepository ingredientRepository = new IngredientRepository();

    public List<Dish> getAllDishes() {
        return dishRepository.findAllDishes();
    }

    public Dish getDishById(int id) {
        return dishRepository.findDishById(id)
                .orElseThrow(() ->
                        new RuntimeException("Dish.id=" + id + " is not found")
                );
    }

    public void updateDishIngredients(int dishId, List<Ingredient> ingredients) {

        getDishById(dishId);

        List<Integer> validIds = ingredients.stream()
                .map(Ingredient::getId)
                .filter(id -> ingredientRepository.findIngredientById(id).isPresent())
                .collect(Collectors.toList());

        dishRepository.updateDishIngredients(dishId, validIds);
    }
}