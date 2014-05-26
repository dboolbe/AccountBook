package com.thesyndicate.android.dboolbe.AccountBook.AccountDAO;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by dboolbe on 3/27/14.
 */
public class AccountSQLiteHelper extends SQLiteOpenHelper {

    public static final String TABLE_ACCOUNT = "account";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TYPE_ID = "type_id";
    public static final String COLUMN_NICKNAME = "nickname";

    private static final String DATABASE_NAME = TABLE_ACCOUNT + ".db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_ACCOUNT + "(" + COLUMN_ID
            + " integer primary key autoincrement, " + COLUMN_TYPE_ID
            + " integer not null, " + COLUMN_NICKNAME + " text not null);";

    public AccountSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Log.w(AccountSQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXIST " + TABLE_ACCOUNT);
        onCreate(database);
    }
}
