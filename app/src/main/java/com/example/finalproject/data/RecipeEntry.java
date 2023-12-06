package com.example.finalproject.data;

/**
 * Represents a single recipe entry.
 */
public class RecipeEntry {
    /** The unique identifier for the recipe. */
    public Long id;
    /** The title of the recipe. */
    public String title;
    /** The URL of the image associated with the recipe. */
    public String imageUrl;
    /** Details or description of the recipe. */
    public String details;
    /**
     * Returns the title of the recipe.
     *
     * @return The title of the recipe.
     */
    public String toString() {
        return title;
    }
}
