package com.thesyndicate.android.dboolbe.AccountBook.TransactionDAO;

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
public class TransactionDataSource {

    // Database fields
    private SQLiteDatabase database;
    private TransactionSQLiteHelper dbHelper;
    private String[] allColumns = {TransactionSQLiteHelper.COLUMN_ID, TransactionSQLiteHelper.COLUMN_ACCOUNT_ID,
            TransactionSQLiteHelper.COLUMN_DATE, TransactionSQLiteHelper.COLUMN_TYPE,
            TransactionSQLiteHelper.COLUMN_PAYEE, TransactionSQLiteHelper.COLUMN_AMOUNT,
            TransactionSQLiteHelper.COLUMN_MEMO, TransactionSQLiteHelper.COLUMN_TRANSACTION_ID};

    public TransactionDataSource(Context context) {
        dbHelper = new TransactionSQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Transaction createTransaction(long account, long date, String type, String payee, float amount, String memo, long transferId) {
        ContentValues values = new ContentValues();
        values.put(TransactionSQLiteHelper.COLUMN_ACCOUNT_ID, account);
        values.put(TransactionSQLiteHelper.COLUMN_DATE, date);
        values.put(TransactionSQLiteHelper.COLUMN_TYPE, type);
        values.put(TransactionSQLiteHelper.COLUMN_PAYEE, payee);
        values.put(TransactionSQLiteHelper.COLUMN_AMOUNT, amount);
        values.put(TransactionSQLiteHelper.COLUMN_MEMO, memo);
        values.put(TransactionSQLiteHelper.COLUMN_TRANSACTION_ID, transferId);
        long insertId = database.insert(TransactionSQLiteHelper.TABLE_TRANSACTION, null,
                values);
        Cursor cursor = database.query(TransactionSQLiteHelper.TABLE_TRANSACTION,
                allColumns, TransactionSQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Transaction transaction = cursorToTransaction(cursor);
        cursor.close();
        return transaction;
    }

    public void updateTransaction(long id, long account, long date, String type, String payee, float amount, String memo, long transferId) {
        ContentValues values = new ContentValues();
        values.put(TransactionSQLiteHelper.COLUMN_ACCOUNT_ID, account);
        values.put(TransactionSQLiteHelper.COLUMN_DATE, date);
        values.put(TransactionSQLiteHelper.COLUMN_TYPE, type);
        values.put(TransactionSQLiteHelper.COLUMN_PAYEE, payee);
        values.put(TransactionSQLiteHelper.COLUMN_AMOUNT, amount);
        values.put(TransactionSQLiteHelper.COLUMN_MEMO, memo);
        values.put(TransactionSQLiteHelper.COLUMN_TRANSACTION_ID, transferId);

        database.update(TransactionSQLiteHelper.TABLE_TRANSACTION, values, TransactionSQLiteHelper.COLUMN_ID + " = " + id, null);
    }

    public void deleteTransaction(Transaction transaction) {
        if (transaction.getTransferId() >= 0) {
            Log.d(TransactionDataSource.class.getName(), "Associated transfer transaction deleted with id: "
                    + transaction.getTransferId());
            database.delete(TransactionSQLiteHelper.TABLE_TRANSACTION, TransactionSQLiteHelper.COLUMN_ID
                    + " = " + transaction.getTransferId(), null);
        }
        Log.d(TransactionDataSource.class.getName(), "Transaction deleted with id: " + transaction.getId());
        database.delete(TransactionSQLiteHelper.TABLE_TRANSACTION, TransactionSQLiteHelper.COLUMN_ID
                + " = " + transaction.getId(), null);
    }

    public void deleteAllTransactions(long accountId) {
        Log.d(TransactionDataSource.class.getName(), "All Transactions deleted with account_id: " + accountId);
        for (Transaction transaction : getAllAccountTransactions(accountId))
            deleteTransaction(transaction);
    }

    public List<Transaction> getAllTransactions() {
        List<Transaction> transactions = new ArrayList<Transaction>();

        Cursor cursor = database.query(TransactionSQLiteHelper.TABLE_TRANSACTION,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Transaction transaction = cursorToTransaction(cursor);
            transactions.add(transaction);
            cursor.moveToNext();
        }
        // Make sure to close the cursor
        cursor.close();
        return transactions;
    }

    public List<Transaction> getAllAccountTransactions(long accountId) {
        List<Transaction> transactions = new ArrayList<Transaction>();

        Cursor cursor = database.query(TransactionSQLiteHelper.TABLE_TRANSACTION,
                allColumns, TransactionSQLiteHelper.COLUMN_ACCOUNT_ID + " = ?",
                new String[]{String.valueOf(accountId)}, null, null, TransactionSQLiteHelper.COLUMN_DATE + " DESC");

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Transaction transaction = cursorToTransaction(cursor);
            transactions.add(transaction);
            cursor.moveToNext();
        }
        // Make sure to close the cursor
        cursor.close();
        return transactions;
    }

    public Transaction getInitialTransaction(long accountId) {
        Cursor cursor = database.query(TransactionSQLiteHelper.TABLE_TRANSACTION,
                allColumns, TransactionSQLiteHelper.COLUMN_ACCOUNT_ID + " = ? AND " +
                TransactionSQLiteHelper.COLUMN_DATE + " < ?", new String[]{String.valueOf(accountId),
                String.valueOf(5)}, null, null, null);

        cursor.moveToFirst();
        Transaction transaction = cursorToTransaction(cursor);
        // make sure to close the cursor
        cursor.close();
        return transaction;
    }

    public Transaction getTransaction(long id) {
        Cursor cursor = database.query(TransactionSQLiteHelper.TABLE_TRANSACTION,
                allColumns, TransactionSQLiteHelper.COLUMN_ID + " = ?", new String[]{String.valueOf(id)}, null, null, null);

        cursor.moveToFirst();
        Transaction transaction = cursorToTransaction(cursor);
        // Make sure to close the cursor
        cursor.close();
        return transaction;
    }

    public float getBalance(long accountId) {
        float balance = 0;

        // Credit the balance
        Cursor cursor = database.rawQuery(
                "SELECT SUM(" + TransactionSQLiteHelper.COLUMN_AMOUNT + ") FROM "
                        + TransactionSQLiteHelper.TABLE_TRANSACTION
                        + " WHERE " + TransactionSQLiteHelper.COLUMN_ACCOUNT_ID
                        + " = " + accountId + " AND " + TransactionSQLiteHelper.COLUMN_TYPE
                        + " = 'CREDIT';", null);

        cursor.moveToFirst();
        balance += cursor.getFloat(0);
        // Make sure to close the cursor
        cursor.close();

        // Debit the balance
        cursor = database.rawQuery(
                "SELECT SUM(" + TransactionSQLiteHelper.COLUMN_AMOUNT + ") FROM "
                        + TransactionSQLiteHelper.TABLE_TRANSACTION
                        + " WHERE " + TransactionSQLiteHelper.COLUMN_ACCOUNT_ID
                        + " = " + accountId + " AND " + TransactionSQLiteHelper.COLUMN_TYPE
                        + " = 'DEBIT';", null);

        cursor.moveToFirst();
        balance -= cursor.getFloat(0);
        // Make sure to close the cursor
        cursor.close();

        return balance;
    }

    public float getBalanceAcrossAllAccounts() {
        float balance = 0;

        // Credit the balance
        Cursor cursor = database.rawQuery(
                "SELECT SUM(" + TransactionSQLiteHelper.COLUMN_AMOUNT + ") FROM "
                        + TransactionSQLiteHelper.TABLE_TRANSACTION
                        + " WHERE " + TransactionSQLiteHelper.COLUMN_TYPE
                        + " = 'CREDIT';", null);

        cursor.moveToFirst();
        balance += cursor.getFloat(0);
        // Make sure to close the cursor
        cursor.close();

        // Debit the balance
        cursor = database.rawQuery(
                "SELECT SUM(" + TransactionSQLiteHelper.COLUMN_AMOUNT + ") FROM "
                        + TransactionSQLiteHelper.TABLE_TRANSACTION
                        + " WHERE " + TransactionSQLiteHelper.COLUMN_TYPE
                        + " = 'DEBIT';", null);

        cursor.moveToFirst();
        balance -= cursor.getFloat(0);
        // Make sure to close the cursor
        cursor.close();

        return balance;
    }

    private Transaction cursorToTransaction(Cursor cursor) {
        Transaction transaction = new Transaction();
        transaction.setId(cursor.getLong(0));
        transaction.setAccountId(cursor.getLong(1));
        transaction.setDate(cursor.getLong(2));
        transaction.setType(cursor.getString(3));
        transaction.setPayee(cursor.getString(4));
        transaction.setAmount(cursor.getFloat(5));
        transaction.setMemo(cursor.getString(6));
        transaction.setTransferId(cursor.getLong(7));
        return transaction;
    }
}
