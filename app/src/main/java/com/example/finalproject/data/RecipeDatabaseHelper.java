package com.example.finalproject.data;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class RecipeDatabaseHelper extends SQLiteOpenHelper {


    public static final String DB_NAME = "RecipeDB04";

    public static final int VERSION_NUM = 2;

    public static final String FAV_TABLE_NAME = "favorite_recipe";

    public static final String COL_RECIPE_ID = "id";

    public static final String COL_TITLE = "title";

    public static final String COL_DETAIL = "detail";

    public static final String COL_IMAGE = "image";

    private static final String CREATE_TABLE_STATEMENT = "CREATE TABLE " + FAV_TABLE_NAME + "( "
            + COL_RECIPE_ID + " INTEGER PRIMARY KEY, "
            + COL_TITLE + " TEXT, "
            + COL_IMAGE + " TEXT, "
            + COL_DETAIL + " TEXT)";


    public RecipeDatabaseHelper(Context context) {
        super(context, DB_NAME, null, VERSION_NUM);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_STATEMENT);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + FAV_TABLE_NAME);
        onCreate(db);
    }


}
