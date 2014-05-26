package com.thesyndicate.android.dboolbe.AccountBook.TransferDAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by dboolbe on 4/7/14.
 */
public class TransferDataSource {

    // Database fields
    private SQLiteDatabase database;
    private TransferSQLiteHelper dbHelper;
    private String[] allColumns = {TransferSQLiteHelper.COLUMN_ID, TransferSQLiteHelper.COLUMN_DEBIT_TRANSACTION_ID,
            TransferSQLiteHelper.COLUMN_CREDIT_TRANSACTION_ID};

    public TransferDataSource(Context context) {
        dbHelper = new TransferSQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Transfer createTransfer(long debitTransactionId, long creditTransactionId) {
        ContentValues values = new ContentValues();
        values.put(TransferSQLiteHelper.COLUMN_DEBIT_TRANSACTION_ID, debitTransactionId);
        values.put(TransferSQLiteHelper.COLUMN_CREDIT_TRANSACTION_ID, creditTransactionId);
        long insertId = database.insert(TransferSQLiteHelper.TABLE_TRANSFER, null,
                values);
        Cursor cursor = database.query(TransferSQLiteHelper.TABLE_TRANSFER,
                allColumns, TransferSQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Transfer transfer = cursorToTransfer(cursor);
        cursor.close();
        return transfer;
    }

    public void updateTransfer(long id, long debitTransactionId, long creditTransactionId) {
        ContentValues values = new ContentValues();
        values.put(TransferSQLiteHelper.COLUMN_DEBIT_TRANSACTION_ID, debitTransactionId);
        values.put(TransferSQLiteHelper.COLUMN_CREDIT_TRANSACTION_ID, creditTransactionId);

        database.update(TransferSQLiteHelper.TABLE_TRANSFER, values, TransferSQLiteHelper.COLUMN_ID + " = " + id, null);
    }

    public void deleteTransfer(Transfer transfer) {
        long id = transfer.getId();
        Log.d(TransferDataSource.class.getName(), "Transfer deleted with id: " + id);
        database.delete(TransferSQLiteHelper.TABLE_TRANSFER, TransferSQLiteHelper.COLUMN_ID
                + " = " + id, null);
    }

    public Transfer getTransferFromDebitId(long debitTransactionId) {
        Cursor cursor = database.query(TransferSQLiteHelper.TABLE_TRANSFER,
                allColumns, TransferSQLiteHelper.COLUMN_DEBIT_TRANSACTION_ID + " = ?",
                new String[]{String.valueOf(debitTransactionId)}, null, null, null);

        cursor.moveToFirst();
        Transfer transfer = cursorToTransfer(cursor);
        // Make sure to close the cursor
        cursor.close();
        return transfer;
    }

    public Transfer getTransferFromCreditId(long creditTransactionId) {
        Cursor cursor = database.query(TransferSQLiteHelper.TABLE_TRANSFER,
                allColumns, TransferSQLiteHelper.COLUMN_CREDIT_TRANSACTION_ID + " = ?",
                new String[]{String.valueOf(creditTransactionId)}, null, null, null);

        cursor.moveToFirst();
        Transfer transfer = cursorToTransfer(cursor);
        // Make sure to close the cursor
        cursor.close();
        return transfer;
    }

    private Transfer cursorToTransfer(Cursor cursor) {
        Transfer transfer = new Transfer();
        transfer.setId(cursor.getLong(0));
        transfer.setDebitTransactionId(cursor.getLong(1));
        transfer.setCreditTransactionId(cursor.getLong(2));
        return transfer;
    }
}
