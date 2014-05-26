package com.thesyndicate.android.dboolbe.AccountBook.TransactionDAO;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by dboolbe on 3/27/14.
 */
public class TransactionSQLiteHelper extends SQLiteOpenHelper {

    public static final String TABLE_TRANSACTION = "_transaction";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_DATE = "_date";
    public static final String COLUMN_TYPE = "_type";
    public static final String COLUMN_PAYEE = "_payee";
    public static final String COLUMN_MEMO = "_memo";
    public static final String COLUMN_AMOUNT = "_amount";
    public static final String COLUMN_ACCOUNT_ID = "_account";
    public static final String COLUMN_TRANSACTION_ID = "_transaction";

    private static final String DATABASE_NAME = TABLE_TRANSACTION + ".db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_TRANSACTION + "(" + COLUMN_ID
            + " integer primary key autoincrement, " + COLUMN_ACCOUNT_ID
            + " integer not null, " + COLUMN_DATE
            + " integer not null, " + COLUMN_TYPE
            + " text not null, " + COLUMN_PAYEE
            + " text not null, " + COLUMN_AMOUNT
            + " real not null, " + COLUMN_MEMO
            + " text not null, " + COLUMN_TRANSACTION_ID
            + ");";

    public TransactionSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Log.w(TransactionSQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXIST " + TABLE_TRANSACTION);
        onCreate(database);
    }
}
