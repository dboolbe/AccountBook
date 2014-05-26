package com.thesyndicate.android.dboolbe.AccountBook.AccountTypeDAO;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by dboolbe on 3/27/14.
 */
public class AccountTypeSQLiteHelper extends SQLiteOpenHelper {

    public static final String TABLE_ACCOUNT_TYPE = "account_type";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TYPE = "_type";

    private static final String DATABASE_NAME = TABLE_ACCOUNT_TYPE + ".db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_ACCOUNT_TYPE + "(" + COLUMN_ID
            + " integer primary key autoincrement, "
            + COLUMN_TYPE + " text not null);";

    public AccountTypeSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
        database.execSQL("INSERT INTO " + TABLE_ACCOUNT_TYPE + "(" + COLUMN_TYPE + ") VALUES('Savings')");
        database.execSQL("INSERT INTO " + TABLE_ACCOUNT_TYPE + "(" + COLUMN_TYPE + ") VALUES('Checking')");
        database.execSQL("INSERT INTO " + TABLE_ACCOUNT_TYPE + "(" + COLUMN_TYPE + ") VALUES('Credit Card')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Log.w(AccountTypeSQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXIST " + TABLE_ACCOUNT_TYPE);
        onCreate(database);
    }
}
