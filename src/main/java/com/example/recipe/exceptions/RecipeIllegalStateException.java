package com.example.recipe.exceptions;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Recipe should have at least one ingredient and step.")
public class RecipeIllegalStateException extends RuntimeException{

    public RecipeIllegalStateException() {
        super();
    }

    public RecipeIllegalStateException(String s) {
        super(s);
    }

}
