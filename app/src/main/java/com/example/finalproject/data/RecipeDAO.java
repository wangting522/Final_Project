package com.example.finalproject.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * This class handles operations related to favorite recipes in the database.
 */
public class RecipeDAO {

    /**
     * Adds a favorite recipe to the database.
     *
     * @param helper The RecipeDatabaseHelper instance
     * @param recipe The RecipeEntry object representing the recipe to be added
     * @return The row ID of the newly inserted row, or -1 if an error occurred
     */
    public static long addFavRecipe(RecipeDatabaseHelper helper, RecipeEntry recipe) {
        ContentValues content = new ContentValues();
        content.put(helper.COL_TITLE, recipe.title);
        content.put(helper.COL_DETAIL, recipe.details);
        content.put(helper.COL_RECIPE_ID, recipe.id);
        content.put(helper.COL_IMAGE, recipe.imageUrl);
        SQLiteDatabase db = helper.getWritableDatabase();
        return db.insertWithOnConflict(helper.FAV_TABLE_NAME, null, content, SQLiteDatabase.CONFLICT_IGNORE);
    }
    /**
     * Deletes a favorite recipe from the database.
     *
     * @param helper The RecipeDatabaseHelper instance
     * @param id     The ID of the recipe to be deleted
     * @return The number of rows affected if a row was deleted, 0 otherwise
     */
    public static int deleteFavRecipe(RecipeDatabaseHelper helper, Long id) {
        SQLiteDatabase db = helper.getWritableDatabase();
        return db.delete(helper.FAV_TABLE_NAME, helper.COL_RECIPE_ID + " = ?", new String[] {id.toString()});
    }
    /**
     * Retrieves a list of favorite recipes from the database based on a keyword.
     *
     * @param helper  The RecipeDatabaseHelper instance
     * @param keyword The keyword to search for in recipe titles
     * @return An ArrayList containing RecipeEntry objects matching the keyword
     */
    public static ArrayList<RecipeEntry> listFavRecipes(RecipeDatabaseHelper helper, String keyword) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor;
        if (keyword.isEmpty()) {
            cursor = db.query(helper.FAV_TABLE_NAME, null, null, null, null, null, null);
        } else {
            cursor = db.query(helper.FAV_TABLE_NAME, null, "title LIKE ?", new String[]{"%"+keyword+"%"}, null, null, null);
        }
        ArrayList<RecipeEntry> recipes = new ArrayList<>();
        while (cursor.moveToNext()) {
            RecipeEntry recipe = new RecipeEntry();
            recipe.id = cursor.getLong(cursor.getColumnIndex(helper.COL_RECIPE_ID));
            recipe.title = cursor.getString(cursor.getColumnIndex(helper.COL_TITLE));
            recipe.imageUrl = cursor.getString(cursor.getColumnIndex(helper.COL_IMAGE));
            recipe.details = cursor.getString(cursor.getColumnIndex(helper.COL_DETAIL));
            recipes.add(recipe);
        }
        return recipes;
    }
    /**
     * Checks if a recipe with the specified ID exists in the database.
     *
     * @param helper The RecipeDatabaseHelper instance
     * @param id     The ID of the recipe to check for existence
     * @return True if the recipe exists in the database, otherwise False
     */
    public static boolean isExist(RecipeDatabaseHelper helper, Long id) {
        SQLiteDatabase db = helper.getReadableDatabase();
        String stmt = String.format("SELECT count(*) FROM %s WHERE %s=%d", helper.FAV_TABLE_NAME, helper.COL_RECIPE_ID, id);
        Cursor cursor = db.rawQuery(stmt, null);
        cursor.moveToFirst();
        boolean found = cursor.getLong(0) > 0;
        cursor.close();
        return found;
    }
}
