package com.example.finalproject.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;


public class RecipeDAO {


    public static long addFavRecipe(RecipeDatabaseHelper helper, RecipeEntry recipe) {
        ContentValues content = new ContentValues();
        content.put(helper.COL_TITLE, recipe.title);
        content.put(helper.COL_DETAIL, recipe.details);
        content.put(helper.COL_RECIPE_ID, recipe.id);
        content.put(helper.COL_IMAGE, recipe.imageUrl);
        SQLiteDatabase db = helper.getWritableDatabase();
        return db.insertWithOnConflict(helper.FAV_TABLE_NAME, null, content, SQLiteDatabase.CONFLICT_IGNORE);
    }

    public static int deleteFavRecipe(RecipeDatabaseHelper helper, Long id) {
        SQLiteDatabase db = helper.getWritableDatabase();
        return db.delete(helper.FAV_TABLE_NAME, helper.COL_RECIPE_ID + " = ?", new String[] {id.toString()});
    }

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
