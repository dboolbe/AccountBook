package com.thesyndicate.android.dboolbe.AccountBook;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.thesyndicate.android.dboolbe.AccountBook.AccountDAO.Account;
import com.thesyndicate.android.dboolbe.AccountBook.AccountDAO.AccountDataSource;
import com.thesyndicate.android.dboolbe.AccountBook.TransactionDAO.Transaction;
import com.thesyndicate.android.dboolbe.AccountBook.TransactionDAO.TransactionDataSource;

/**
 * Created by dboolbe on 3/30/14.
 */
public class ConfirmDeleteActivity extends Activity implements View.OnClickListener {

    AccountDataSource accountDB;
    TransactionDataSource transactionDB;

    String elementType;
    long elementId;
    Account account;
    Transaction transaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirm_delete);

        Intent intent = getIntent();
        elementType = intent.getStringExtra("element_type");
        elementId = intent.getLongExtra("element_id", -1);

        if (elementId < 0) {
            setResult(RESULT_CANCELED);
            finish();
        }

        accountDB = new AccountDataSource(this);
        transactionDB = new TransactionDataSource(this);

        accountDB.open();
        transactionDB.open();

        if (elementType.equals("ACCOUNT")) {
            account = accountDB.getAccount(elementId);
            ((TextView) findViewById(R.id.confirm_delete_title)).setText(R.string.confirm_delete_account_title);

            String message = getResources().getString(R.string.confirm_delete_account_message)
                    + "\n\n" + account.getNickname();
            ((TextView) findViewById(R.id.confirm_delete_message)).setText(message);
        } else {
            transaction = transactionDB.getTransaction(elementId);
            ((TextView) findViewById(R.id.confirm_delete_title)).setText(R.string.confirm_delete_transaction_title);

            String message = getResources().getString(R.string.confirm_delete_transaction_message)
                    + "\n\n" + transaction.getPayee();
            ((TextView) findViewById(R.id.confirm_delete_message)).setText(message);
        }

        findViewById(R.id.confirm_delete_delete_button).setOnClickListener(this);
        findViewById(R.id.confirm_delete_cancel_button).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.confirm_delete_delete_button:
                if (elementType.equals("ACCOUNT")) {
                    deleteAccount(account);
                } else {
                    transactionDB.deleteTransaction(transaction);
                }
                setResult(RESULT_OK);
                finish();
                break;
            case R.id.confirm_delete_cancel_button:
                setResult(RESULT_CANCELED);
                finish();
                break;
            default:
                Toast.makeText(this, "Function Pending Implementation", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    protected void onResume() {
        accountDB.open();
        transactionDB.open();
        super.onResume();
    }

    @Override
    protected void onPause() {
        accountDB.close();
        transactionDB.close();
        super.onPause();
    }

    private void deleteAccount(Account account) {
        // Delete all transactions associated with the account.
        transactionDB.getAllAccountTransactions(account.getId());

        // Delete the supplied account.
        accountDB.deleteAccount(account);
    }
}
