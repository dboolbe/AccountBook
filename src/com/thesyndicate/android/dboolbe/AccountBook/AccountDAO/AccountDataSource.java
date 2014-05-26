package com.thesyndicate.android.dboolbe.AccountBook.AccountDAO;

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
public class AccountDataSource {

    // Database fields
    private SQLiteDatabase database;
    private AccountSQLiteHelper dbHelper;
    private String[] allColumns = {AccountSQLiteHelper.COLUMN_ID,
            AccountSQLiteHelper.COLUMN_TYPE_ID, AccountSQLiteHelper.COLUMN_NICKNAME};

    public AccountDataSource(Context context) {
        dbHelper = new AccountSQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Account createAccount(long typeId, String nickname) {
        ContentValues values = new ContentValues();
        values.put(AccountSQLiteHelper.COLUMN_TYPE_ID, typeId);
        values.put(AccountSQLiteHelper.COLUMN_NICKNAME, nickname);
        long insertId = database.insert(AccountSQLiteHelper.TABLE_ACCOUNT, null,
                values);
        Cursor cursor = database.query(AccountSQLiteHelper.TABLE_ACCOUNT,
                allColumns, AccountSQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Account account = cursorToAccount(cursor);
        cursor.close();
        return account;
    }

    public void updateAccount(long id, long typeId, String nickname) {
        ContentValues values = new ContentValues();
        values.put(AccountSQLiteHelper.COLUMN_TYPE_ID, typeId);
        values.put(AccountSQLiteHelper.COLUMN_NICKNAME, nickname);

        database.update(AccountSQLiteHelper.TABLE_ACCOUNT, values,
                AccountSQLiteHelper.COLUMN_ID + " = " + id, null);
    }

    public void deleteAccount(Account account) {
        long id = account.getId();
        Log.d(AccountDataSource.class.getName(), "Account deleted with id: " + id);
        database.delete(AccountSQLiteHelper.TABLE_ACCOUNT, AccountSQLiteHelper.COLUMN_ID
                + " = " + id, null);
    }

    public Account getAccount(long id) {
        Cursor cursor = database.query(AccountSQLiteHelper.TABLE_ACCOUNT,
                allColumns, AccountSQLiteHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)}, null, null, null);

        cursor.moveToFirst();
        Account account = cursorToAccount(cursor);
        // make sure to close the cursor
        cursor.close();
        return account;
    }

    public List<Account> getAllAccount() {
        List<Account> accounts = new ArrayList<Account>();

        Cursor cursor = database.query(AccountSQLiteHelper.TABLE_ACCOUNT,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Account account = cursorToAccount(cursor);
            accounts.add(account);
            cursor.moveToNext();
        }
        // Make sure to close the cursor
        cursor.close();
        return accounts;
    }

    private Account cursorToAccount(Cursor cursor) {
        Account account = new Account();
        account.setId(cursor.getLong(0));
        account.setTypeId(cursor.getLong(1));
        account.setNickname(cursor.getString(2));
        return account;
    }
}
