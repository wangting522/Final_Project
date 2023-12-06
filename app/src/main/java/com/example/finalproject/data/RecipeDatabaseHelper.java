package com.example.finalproject.data;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
/**
 * Helper class for managing the Recipe Database.
 */
public class RecipeDatabaseHelper extends SQLiteOpenHelper {

    /** Database name */
    public static final String DB_NAME = "RecipeDB04";
    /** Database version */
    public static final int VERSION_NUM = 2;
    /** Table name for favorite recipes */
    public static final String FAV_TABLE_NAME = "favorite_recipe";
    /** Column name for recipe ID */
    public static final String COL_RECIPE_ID = "id";
    /** Column name for recipe title */
    public static final String COL_TITLE = "title";
    /** Column name for recipe detail */
    public static final String COL_DETAIL = "detail";
    /** Column name for recipe image */
    public static final String COL_IMAGE = "image";
    /** SQL statement for creating the table */
    private static final String CREATE_TABLE_STATEMENT = "CREATE TABLE " + FAV_TABLE_NAME + "( "
            + COL_RECIPE_ID + " INTEGER PRIMARY KEY, "
            + COL_TITLE + " TEXT, "
            + COL_IMAGE + " TEXT, "
            + COL_DETAIL + " TEXT)";

    /**
     * Constructor for the RecipeDatabaseHelper.
     * @param context The application context
     */
    public RecipeDatabaseHelper(Context context) {
        super(context, DB_NAME, null, VERSION_NUM);
    }
    /**
     * Called when the database is created for the first time.
     * @param db The database
     */
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_STATEMENT);
    }
    /**
     * Called when the database needs to be upgraded.
     * @param db The database
     * @param oldVersion The old database version
     * @param newVersion The new database version
     */
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + FAV_TABLE_NAME);
        onCreate(db);
    }


}
