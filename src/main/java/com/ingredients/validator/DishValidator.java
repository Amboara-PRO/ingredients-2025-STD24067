package com.ingredients.validator;

import com.ingredients.entity.Ingredient;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DishValidator {

    public void validateIngredientList(List<Ingredient> ingredients) {
        if (ingredients == null) {
            throw new IllegalArgumentException("Request body is required.");
        }
    }
}