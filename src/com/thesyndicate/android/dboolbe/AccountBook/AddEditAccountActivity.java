package com.thesyndicate.android.dboolbe.AccountBook;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.thesyndicate.android.dboolbe.AccountBook.AccountDAO.Account;
import com.thesyndicate.android.dboolbe.AccountBook.AccountDAO.AccountDataSource;
import com.thesyndicate.android.dboolbe.AccountBook.TransactionDAO.Transaction;
import com.thesyndicate.android.dboolbe.AccountBook.TransactionDAO.TransactionDataSource;

import java.util.Calendar;

/**
 * Created by dboolbe on 3/27/14.
 */
public class AddEditAccountActivity extends Activity implements View.OnClickListener {

    AccountDataSource accountDB;
    TransactionDataSource transactionDB;

    Calendar calendar;

    String activityType = "ADD";
    Account account;
    Transaction transaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_edit_account);

        accountDB = new AccountDataSource(this);
        transactionDB = new TransactionDataSource(this);

        calendar = Calendar.getInstance();

        accountDB.open();
        transactionDB.open();

        Intent intent = getIntent();
        String add_account = intent.getStringExtra("add_account");

        if (add_account == null || add_account.equals("Y")) {
            ((TextView) findViewById(R.id.add_edit_account_title)).setText(R.string.add_account_title);
        } else {
            long id = intent.getLongExtra("account_id", -1);
            if (id > -1) {
                activityType = "EDIT";
                account = accountDB.getAccount(id);
                transaction = transactionDB.getInitialTransaction(id);

                ((TextView) findViewById(R.id.add_edit_account_title)).setText(R.string.edit_account_title);

                ((Spinner) findViewById(R.id.add_edit_account_type)).setSelection((int) account.getTypeId());

                ((EditText) findViewById(R.id.add_edit_account_nickname)).setText(account.getNickname());
                ((EditText) findViewById(R.id.add_edit_account_amount)).setText(String.valueOf(transaction.getAmount()));
            }
        }

        if (!activityType.equals("ADD"))
            ((Button) findViewById(R.id.submit_button)).setText(R.string.update);

        findViewById(R.id.submit_button).setOnClickListener(this);
        findViewById(R.id.cancel_button).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.submit_button:
                boolean noErrorsDetected = true;

                long accountTypeId = ((Spinner) findViewById(R.id.add_edit_account_type)).getSelectedItemId();
                String nickname = ((EditText) findViewById(R.id.add_edit_account_nickname)).getText().toString();
                if (nickname.length() == 0) {
                    Toast.makeText(this, "Nickname: Can't Be Left Blank", Toast.LENGTH_SHORT).show();
                    noErrorsDetected = false;
                }
                float amount = 0L;
                try {
                    amount = Float.parseFloat(((EditText) findViewById(R.id.add_edit_account_amount)).getText().toString());
                } catch (Exception e) {
                    Log.e(AddEditAccountActivity.class.getName(), "Error in supplied values.", e);
                    Toast.makeText(this, "Initial Balance: Must Be Valid Decimal", Toast.LENGTH_SHORT).show();
                    noErrorsDetected = false;
                }

                if (noErrorsDetected) {
                    if (activityType.equals("ADD")) {
                        Log.d(AddEditAccountActivity.class.getName(), "Creating account");
                        Account account = accountDB.createAccount(accountTypeId, nickname);
                        String[] transactionType = getResources().getStringArray(R.array.add_edit_transaction_type);
                        transactionDB.createTransaction(account.getId(), 1L, transactionType[1],
                                getResources().getString(R.string.add_edit_transaction_initial_balance),
                                amount, "-", -1);
                    } else {
                        Log.d(AddEditAccountActivity.class.getName(), "Updating account");
                        accountDB.updateAccount(account.getId(), accountTypeId, nickname);
                        transactionDB.updateTransaction(transaction.getId(), transaction.getAccountId(),
                                transaction.getDate(), transaction.getType(), transaction.getPayee(), amount,
                                transaction.getMemo(), transaction.getTransferId());
                    }

                    setResult(RESULT_OK);
                    finish();
                }
                break;
            case R.id.cancel_button:
                setResult(RESULT_CANCELED);
                finish();
                break;
            default:
                Toast.makeText(this, "Function Pending Implementation", Toast.LENGTH_SHORT);
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
}
