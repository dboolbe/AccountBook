package com.thesyndicate.android.dboolbe.AccountBook;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.thesyndicate.android.dboolbe.AccountBook.AccountDAO.Account;
import com.thesyndicate.android.dboolbe.AccountBook.AccountDAO.AccountDataSource;
import com.thesyndicate.android.dboolbe.AccountBook.TransactionDAO.Transaction;
import com.thesyndicate.android.dboolbe.AccountBook.TransactionDAO.TransactionDataSource;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by dboolbe on 3/28/14.
 */
public class AddEditTransactionActivity extends Activity implements View.OnClickListener {

    AccountDataSource accountDB;
    TransactionDataSource transactionDB;

    String activityType = "ADD";
    Transaction transaction;

    Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_edit_transactions);

        accountDB = new AccountDataSource(this);
        transactionDB = new TransactionDataSource(this);

        accountDB.open();
        transactionDB.open();

        Intent intent = getIntent();
        String add_transaction = intent.getStringExtra("add_transaction");

        ArrayAdapter<Account> adapter = new ArrayAdapter<Account>(this,
                android.R.layout.simple_spinner_item, accountDB.getAllAccount());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ((Spinner) findViewById(R.id.add_edit_transaction_account)).setAdapter(adapter);

        ArrayAdapter<Account> transferAdapter = new ArrayAdapter<Account>(this,
                android.R.layout.simple_spinner_item, accountDB.getAllAccount());
        transferAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ((Spinner) findViewById(R.id.add_edit_transaction_transfer_account_spinner)).setAdapter(transferAdapter);

        if (add_transaction == null || add_transaction.equals("Y")) {
            ((TextView) findViewById(R.id.add_edit_transaction_title)).setText(R.string.add_transaction_title);
            long id = intent.getLongExtra("account_id", -1);
            if (id > -1) {
                Account account = accountDB.getAccount(id);
                for (int i = 0; i < adapter.getCount(); i++) {
                    if (account.getNickname().equals(adapter.getItem(i).getNickname())) {
                        ((Spinner) findViewById(R.id.add_edit_transaction_account)).setSelection(i);
                        break;
                    }
                }
            }
        } else {
            ((TextView) findViewById(R.id.add_edit_transaction_title)).setText(R.string.edit_transaction_title);
            long id = intent.getLongExtra("transaction_id", -1);
            if (id > -1) {
                activityType = "" + id;
                transaction = transactionDB.getTransaction(id);
                Account account = accountDB.getAccount(transaction.getAccountId());

                for (int i = 0; i < adapter.getCount(); i++) {
                    if (account.getNickname().equals(adapter.getItem(i).getNickname())) {
                        ((Spinner) findViewById(R.id.add_edit_transaction_account)).setSelection(i);
                        break;
                    }
                }
                ArrayAdapter<String> tmpAdapter = (ArrayAdapter<String>) ((Spinner) findViewById(R.id.add_edit_transaction_type)).getAdapter();
                for (int i = 0; i < tmpAdapter.getCount(); i++) {
                    if (transaction.getType().equals(tmpAdapter.getItem(i))) {
                        ((Spinner) findViewById(R.id.add_edit_transaction_type)).setSelection(i);
                        break;
                    }
                }
                calendar.setTimeInMillis(transaction.getDate());
                if (transaction.getTransferId() >= 0) {
                    ((Spinner) findViewById(R.id.add_edit_transaction_transaction_type_spinner)).setSelection(1);
                    for (int i = 0; i < transferAdapter.getCount(); i++) {
                        if (transferAdapter.getItem(i).getId() == transaction.getTransferId()) {
                            ((Spinner) findViewById(R.id.add_edit_transaction_transfer_account_spinner))
                                    .setSelection(i);
                            break;
                        }
                    }
                } else {
                    ((EditText) findViewById(R.id.add_edit_transaction_payee)).setText(transaction.getPayee());
                }
                ((EditText) findViewById(R.id.add_edit_transaction_amount)).setText(String.valueOf(transaction.getAmount()));
                ((EditText) findViewById(R.id.add_edit_transaction_memo)).setText(transaction.getMemo());
            }
        }


        final TextView dateText = ((TextView) findViewById(R.id.add_edit_transaction_date));

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                setDate(year, monthOfYear, dayOfMonth);
            }
        };


        dateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(view.getContext(), date, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        // Initial date with current date
        setDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        ((Spinner) findViewById(R.id.add_edit_transaction_transaction_type_spinner)).setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (((String) parent.getSelectedItem()).equals("STANDARD")) {
                            findViewById(R.id.add_edit_transaction_transfer_layout).setVisibility(View.GONE);
                            findViewById(R.id.add_edit_transaction_payee_payer_layout).setVisibility(View.VISIBLE);
                        } else {
                            findViewById(R.id.add_edit_transaction_payee_payer_layout).setVisibility(View.GONE);
                            findViewById(R.id.add_edit_transaction_transfer_layout).setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

        ((Spinner) findViewById(R.id.add_edit_transaction_type)).setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (((String) parent.getSelectedItem()).equals("DEBIT")) {
                            ((TextView) findViewById(R.id.add_edit_transaction_transfer_account)).setText(R.string.add_edit_transaction_account_payee);
                            ((TextView) findViewById(R.id.add_edit_transaction_payee_payer)).setText(R.string.add_edit_transaction_payee);
                            ((TextView) findViewById(R.id.add_edit_transaction_payee)).setHint(R.string.add_edit_transaction_payee_hint);
                        } else {
                            ((TextView) findViewById(R.id.add_edit_transaction_transfer_account)).setText(R.string.add_edit_transaction_account_payer);
                            ((TextView) findViewById(R.id.add_edit_transaction_payee_payer)).setText(R.string.add_edit_transaction_payer);
                            ((TextView) findViewById(R.id.add_edit_transaction_payee)).setHint(R.string.add_edit_transaction_payer_hint);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

        if (!activityType.equals("ADD"))
            ((Button) findViewById(R.id.submit_button)).setText(R.string.update);

        findViewById(R.id.submit_button).setOnClickListener(this);
        findViewById(R.id.cancel_button).setOnClickListener(this);
    }

    private void setDate(int year, int monthOfYear, int dayOfMonth) {
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, monthOfYear);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        String myFormat = getResources().getString(R.string.date_format);
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        ((TextView) findViewById(R.id.add_edit_transaction_date)).setText(sdf.format(calendar.getTime()));
    }

    @Override
    public void onClick(View view) {
        Log.e(AddEditAccountActivity.class.getName(), "Button Clicked");
        switch (view.getId()) {
            case R.id.submit_button:
                String transactionType = (String) ((Spinner) findViewById(R.id.add_edit_transaction_transaction_type_spinner)).getSelectedItem();
                Account account = (Account) ((Spinner) findViewById(R.id.add_edit_transaction_account)).getSelectedItem();
                long type = ((Spinner) findViewById(R.id.add_edit_transaction_type)).getSelectedItemId();
                long date = calendar.getTimeInMillis();
                String payee = ((EditText) findViewById(R.id.add_edit_transaction_payee)).getText().toString();
                Account otherAccount = (Account) ((Spinner) findViewById(R.id.add_edit_transaction_transfer_account_spinner)).getSelectedItem();
                float amount = Float.parseFloat(((EditText) findViewById(R.id.add_edit_transaction_amount)).getText().toString());
                String memo = ((EditText) findViewById(R.id.add_edit_transaction_memo)).getText().toString();

                String[] types = getResources().getStringArray(R.array.add_edit_transaction_type);
                if (transactionType.equals("STANDARD")) {
                    Log.d(AddEditTransactionActivity.class.getName(), "Submit transaction");
                    if (activityType.equals("ADD")) {
                        Log.d(AddEditTransactionActivity.class.getName(), "Creating transaction");
                        transactionDB.createTransaction(account.getId(), date, types[(int) type], payee, amount, memo, -1);
                    } else {
                        Log.d(AddEditTransactionActivity.class.getName(), "Updating transaction");
                        transactionDB.updateTransaction(transaction.getId(), account.getId(), date, types[(int) type],
                                payee, amount, memo, -1L);
                    }
                } else {
                    Log.d(AddEditTransactionActivity.class.getName(), "Submit transfer");
                    if (activityType.equals("ADD")) {
                        Log.d(AddEditTransactionActivity.class.getName(), "Creating transfer");
                        Transaction transaction = transactionDB.createTransaction(account.getId(), date,
                                types[(int) type], getResources()
                                .getString(R.string.add_edit_transaction_internal_transfer), amount, memo, -1);
                        Transaction otherTransaction = transactionDB.createTransaction(otherAccount.getId(), date,
                                types[(int) ((type + 1) % 2)], getResources()
                                .getString(R.string.add_edit_transaction_internal_transfer),
                                amount, memo, transaction.getId());
                        transactionDB.updateTransaction(transaction.getId(), transaction.getAccountId(),
                                transaction.getDate(), transaction.getType(), transaction.getPayee(),
                                transaction.getAmount(), transaction.getMemo(), otherTransaction.getId());
                    } else {
                        Log.d(AddEditTransactionActivity.class.getName(), "Updating transfer");
                        transactionDB.updateTransaction(transaction.getId(), account.getId(), date,
                                types[(int) type], payee, amount, memo, transaction.getTransferId());
                        transactionDB.updateTransaction(transaction.getTransferId(), otherAccount.getId(), date,
                                types[(int) ((type + 1) % 2)], payee, amount, memo, transaction.getId());
                    }
                }
                setResult(RESULT_OK);
                finish();
                break;
            case R.id.cancel_button:
                setResult(RESULT_CANCELED);
                finish();
                break;
            default:
                Toast.makeText(this, "WARN: Functionality Pending Implementation", Toast.LENGTH_LONG).show();
                finish();
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
