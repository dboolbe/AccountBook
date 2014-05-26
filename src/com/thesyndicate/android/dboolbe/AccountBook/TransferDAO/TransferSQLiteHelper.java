package com.thesyndicate.android.dboolbe.AccountBook.TransferDAO;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by dboolbe on 4/7/14.
 */
public class TransferSQLiteHelper extends SQLiteOpenHelper {

    public static final String TABLE_TRANSFER = "_transfer";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_DEBIT_TRANSACTION_ID = "_debit_id";
    public static final String COLUMN_CREDIT_TRANSACTION_ID = "_credit_id";

    private static final String DATABASE_NAME = TABLE_TRANSFER + ".db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_TRANSFER + "(" + COLUMN_ID
            + " integer primary key autoincrement, " + COLUMN_DEBIT_TRANSACTION_ID
            + " integer not null, " + COLUMN_CREDIT_TRANSACTION_ID
            + " integer not null);";

    public TransferSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Log.w(TransferSQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXIST " + TABLE_TRANSFER);
        onCreate(database);
    }
}
