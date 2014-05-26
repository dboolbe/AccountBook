package com.thesyndicate.android.dboolbe.AccountBook.AccountTypeDAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dboolbe on 3/27/14.
 */
public class AccountTypeDataSource {

    // Database fields
    private SQLiteDatabase database;
    private AccountTypeSQLiteHelper dbHelper;
    private String[] allColumns = {AccountTypeSQLiteHelper.COLUMN_ID, AccountTypeSQLiteHelper.COLUMN_TYPE};

    public AccountTypeDataSource(Context context) {
        dbHelper = new AccountTypeSQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public AccountType createAccountType(String type) {
        ContentValues values = new ContentValues();
        values.put(AccountTypeSQLiteHelper.COLUMN_TYPE, type);
        long insertId = database.insert(AccountTypeSQLiteHelper.TABLE_ACCOUNT_TYPE, null,
                values);
        Cursor cursor = database.query(AccountTypeSQLiteHelper.TABLE_ACCOUNT_TYPE,
                allColumns, AccountTypeSQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        AccountType accountType = cursorToAccountType(cursor);
        cursor.close();
        return accountType;
    }

    public void deleteAccountType(AccountType accountType) {
        long id = accountType.getId();
        Log.d(AccountTypeDataSource.class.getName(), "AccountType deleted with id: " + id);
        database.delete(AccountTypeSQLiteHelper.TABLE_ACCOUNT_TYPE, AccountTypeSQLiteHelper.COLUMN_ID
                + " = " + id, null);
    }

    public List<AccountType> getAllAccountTypes() {
        List<AccountType> accountTypes = new ArrayList<AccountType>();

        Cursor cursor = database.query(AccountTypeSQLiteHelper.TABLE_ACCOUNT_TYPE,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            AccountType accountType = cursorToAccountType(cursor);
            accountTypes.add(accountType);
            cursor.moveToNext();
        }
        // Make sure to close the cursor
        cursor.close();
        return accountTypes;
    }

    public AccountType getAccountType(long id) {
        Cursor cursor = database.rawQuery("select " + AccountTypeSQLiteHelper.COLUMN_ID
                + ", " + AccountTypeSQLiteHelper.COLUMN_TYPE
                + " FROM " + AccountTypeSQLiteHelper.TABLE_ACCOUNT_TYPE
                + " WHERE " + AccountTypeSQLiteHelper.COLUMN_ID
                + " = " + id + ";", null);
        cursor.moveToFirst();
        AccountType accountType = cursorToAccountType(cursor);
        // Make sure to close the cursor
        cursor.close();
        return accountType;
    }

    private AccountType cursorToAccountType(Cursor cursor) {
        AccountType accountType = new AccountType();
        accountType.setId(cursor.getLong(0));
        accountType.setType(cursor.getString(1));
        return accountType;
    }
}
