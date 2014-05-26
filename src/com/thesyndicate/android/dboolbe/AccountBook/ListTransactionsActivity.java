package com.thesyndicate.android.dboolbe.AccountBook;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.thesyndicate.android.dboolbe.AccountBook.AccountDAO.Account;
import com.thesyndicate.android.dboolbe.AccountBook.AccountDAO.AccountDataSource;
import com.thesyndicate.android.dboolbe.AccountBook.TransactionDAO.Transaction;
import com.thesyndicate.android.dboolbe.AccountBook.TransactionDAO.TransactionDataSource;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by dboolbe on 3/28/14.
 */
public class ListTransactionsActivity extends ListActivity implements View.OnClickListener, AdapterView.OnItemLongClickListener {

    AccountDataSource accountDB;
    TransactionDataSource transactionDB;

    Account account;

    final int ADD_TRANSACTION = 12110;
    final int EDIT_TRANSACTION = 12111;
    final int DELETE_TRANSACTION = 12112;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_transactions);

        accountDB = new AccountDataSource(this);
        transactionDB = new TransactionDataSource(this);

        accountDB.open();
        transactionDB.open();

        Intent intent = getIntent();
        long accountId = intent.getLongExtra("account_id", -1);

        if (accountId < 0) {
            Toast.makeText(this, "Error: No 'account_id' passed " + accountId, Toast.LENGTH_LONG).show();
            finish();
        }

        account = accountDB.getAccount(accountId);

        if (account == null) {
            Toast.makeText(this, "Error: Account does not exists", Toast.LENGTH_LONG).show();
            finish();
        }

        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(getResources().getConfiguration().locale);
        setTitle(account.getNickname() + " := " + currencyFormatter.format(transactionDB.getBalance(accountId)));

        findViewById(R.id.list_transactions_header).setOnClickListener(this);

//        ArrayAdapter<Transaction> adapter = new ArrayAdapter<Transaction>(this,
//                android.R.layout.simple_list_item_1, transactionDB.getAllAccountTransactions(account.getId()));
        ListTransactionsAdapter adapter = new ListTransactionsAdapter(this,
                R.layout.list_transactions_row, transactionDB.getAllAccountTransactions(account.getId()));
        setListAdapter(adapter);

        getListView().setOnItemLongClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.list_transactions_header:
                gotoAddTransactionPage();
                break;
            default:
                Toast.makeText(this, "Function Pending Implementation", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public boolean onItemLongClick(final AdapterView<?> parent, View view, final int position, long id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.list_transactions_dialog_message);
        builder.setTitle(R.string.list_transactions_dialog_title);
        builder.setPositiveButton(R.string.edit, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                gotoEditAccountPage((Transaction) parent.getItemAtPosition(position));
            }
        });
        builder.setNegativeButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                gotoDeleteTransactionConfirmationPage((Transaction) parent.getItemAtPosition(position));
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ADD_TRANSACTION:
                if (resultCode == RESULT_OK) {
                    Log.d(ListAccountsActivity.class.getName(), "Returning From Adding Transaction");
                    refreshTransactionList();

                    // Update the application title
                    updateTitle();
                }
                break;
            case DELETE_TRANSACTION:
                if (resultCode == RESULT_OK) {
                    Log.d(ListAccountsActivity.class.getName(), "Returning From Deleting Transaction");
                    refreshTransactionList();

                    // Update the application title
                    updateTitle();
                }
                break;
            case EDIT_TRANSACTION:
                if (resultCode == RESULT_OK) {
                    Log.d(ListAccountsActivity.class.getName(), "Returning From Editing Transaction");
                    refreshTransactionList();

                    // Update the application title
                    updateTitle();
                }
                break;
            default:
                Toast.makeText(this, "Function Pending Implementation", Toast.LENGTH_SHORT).show();
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onListItemClick(ListView parent, View view, int position, long id) {
//        Toast.makeText(this, "Function Pending Implementation", Toast.LENGTH_SHORT).show();
        super.onListItemClick(parent, view, position, id);
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        finish();
//        super.onBackPressed();
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

    private void gotoAddTransactionPage() {
        Intent intent = new Intent(this, AddEditTransactionActivity.class);
        intent.putExtra("account_id", account.getId());
        intent.putExtra("add_transaction", "Y");
        startActivityForResult(intent, ADD_TRANSACTION);
    }

    private void gotoEditAccountPage(Transaction transaction) {
        Intent intent = new Intent(this, AddEditTransactionActivity.class);
        intent.putExtra("account_id", account.getId());
        intent.putExtra("add_transaction", "N");
        intent.putExtra("transaction_id", transaction.getId());
        startActivityForResult(intent, EDIT_TRANSACTION);
    }

    private void gotoDeleteTransactionConfirmationPage(Transaction transaction) {
        Intent intent = new Intent(this, ConfirmDeleteActivity.class);
        intent.putExtra("element_type", "TRANSACTION");
        intent.putExtra("element_id", transaction.getId());
        startActivityForResult(intent, DELETE_TRANSACTION);
    }

    private void refreshTransactionList() {
        // Open connection to the database.
        transactionDB.open();

        ArrayAdapter<Transaction> adapter = (ArrayAdapter<Transaction>) getListAdapter();
        adapter.clear();
        transactionDB.open();
        for (Transaction tmpTransaction : transactionDB.getAllAccountTransactions(account.getId()))
            adapter.add(tmpTransaction);
        adapter.notifyDataSetChanged();

        // Close database connection.
        transactionDB.close();
    }

    private void updateTitle() {
        // Open connection to the database.
        transactionDB.open();

        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(getResources().getConfiguration().locale);
        setTitle(account.getNickname() + " := " + currencyFormatter.format(transactionDB.getBalance(account.getId())));

        // Close database connection.
        transactionDB.close();
    }

    private class ListTransactionsAdapter extends ArrayAdapter<Transaction> {

        Context context;
        int layoutResourceId;
        List<Transaction> data = null;
        Calendar calendar = Calendar.getInstance();

        String myFormat = getResources().getString(R.string.date_format);
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        public ListTransactionsAdapter(Context context, int layoutResourceId, List<Transaction> data) {
            super(context, layoutResourceId, data);
            this.layoutResourceId = layoutResourceId;
            this.context = context;
            this.data = data;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            AccountHolder holder = null;

            if (row == null) {
                LayoutInflater inflater = ((ListActivity) context).getLayoutInflater();
                row = inflater.inflate(layoutResourceId, parent, false);

                holder = new AccountHolder();
                holder.listTransactionsDate = (TextView) row.findViewById(R.id.list_transactions_date);
                holder.listTransactionsNickname = (TextView) row.findViewById(R.id.list_transactions_nickname);
                holder.listTransactionsAmount = (TextView) row.findViewById(R.id.list_transactions_amount);
                holder.listTransactionsMemo = (TextView) row.findViewById(R.id.list_transactions_memo);

                row.setTag(holder);
            } else {
                holder = (AccountHolder) row.getTag();
            }

            Transaction transaction = data.get(position);
            NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(getResources().getConfiguration().locale);
            if (transaction.getDate() < 5) {
                holder.listTransactionsDate.setText("");
                holder.listTransactionsMemo.setText("");
            } else {
                calendar.setTimeInMillis(transaction.getDate());
                holder.listTransactionsDate.setText(sdf.format(calendar.getTime()));
                holder.listTransactionsMemo.setText(transaction.getMemo());
            }
            holder.listTransactionsNickname.setText(transaction.getPayee());
            holder.listTransactionsAmount.setText(currencyFormatter.format(transaction.getAmount()));

            if (transaction.getType().equals("DEBIT"))
                holder.listTransactionsAmount.setTextColor(Color.RED);
            else
                holder.listTransactionsAmount.setTextColor(Color.WHITE);

            return row;
        }

        class AccountHolder {
            TextView listTransactionsDate;
            TextView listTransactionsNickname;
            TextView listTransactionsAmount;
            TextView listTransactionsMemo;
        }
    }
}
