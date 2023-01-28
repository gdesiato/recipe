package com.example.recipe.config;

import com.example.recipe.models.CustomUserDetails;
import com.example.recipe.models.Recipe;
import com.example.recipe.models.Review;
import com.example.recipe.models.Role;
import com.example.recipe.repositories.RecipeRepo;
import com.example.recipe.repositories.ReviewRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.persistence.EntityNotFoundException;
import java.io.Serializable;
import java.util.Collection;
import java.util.Optional;

@Component
public class CustomPermissionEvaluator implements PermissionEvaluator {

    @Autowired
    RecipeRepo recipeRepo;

    @Autowired
    ReviewRepo reviewRepo;

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        //this method will not be used. But if used by accident, should always block access for good measure.
        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        if (!permission.getClass().equals("".getClass())) {
            throw new SecurityException("Cannot execute hasPermission() calls where permission is not in String form");
        }

        //if the user is an admin they should be allowed to proceed
        if (userIsAdmin(authentication)) {
            return true;
        } else {
            //otherwise, the user must be the owner of the object to edit it.
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

            if (targetType.equalsIgnoreCase("recipe")) {
                Optional<Recipe> recipe = recipeRepo.findById(Long.parseLong(targetId.toString()));
                if (recipe.isEmpty()) {
                    // no recipe with id exists, return true so the method can continue ultimately throwing an exception
                    return true;
                }

                //if the author of the entity matches the current user they are the owner of the recipe and should be allowed access
                return recipe.get().getAuthor().getId()==userDetails.getId();

            } else if (targetType.equalsIgnoreCase("review")) {
                Optional<Review> review = reviewRepo.findById(Long.parseLong(targetId.toString()));
                if (review.isEmpty()) {
                    throw new EntityNotFoundException("The review you are trying to access does not exist");
                }

                //if the author of the entity matches the current user they are the owner of the review and should be allowed access
                return review.get().getAuthor().equals(userDetails.getUsername());
            }
        }
        return true;
    }

    public boolean userIsAdmin(Authentication authentication) {
        Collection<Role> grantedAuthorities = (Collection<Role>) authentication.getAuthorities();

        for (Role r : grantedAuthorities) {
            if (r.getAuthority().equals("ROLE_ADMIN")) {
                return true;
            }
        }
        return false;
    }
}