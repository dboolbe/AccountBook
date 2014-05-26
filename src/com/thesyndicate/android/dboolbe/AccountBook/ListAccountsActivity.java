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
import com.thesyndicate.android.dboolbe.AccountBook.TransactionDAO.TransactionDataSource;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by dboolbe on 3/27/14.
 */
public class ListAccountsActivity extends ListActivity implements View.OnClickListener, AdapterView.OnItemLongClickListener {

    AccountDataSource accountDB;
    TransactionDataSource transactionDB;

    final int ADD_ACCOUNT = 12110;
    final int EDIT_ACCOUNT = 12111;
    final int LIST_TRANSACTION = 12112;
    final int DELETE_ACCOUNT = 12113;

    /**
     * Called when the activity is first created.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_accounts);

        accountDB = new AccountDataSource(this);
        transactionDB = new TransactionDataSource(this);

        accountDB.open();
        transactionDB.open();

        findViewById(R.id.list_accounts_header).setOnClickListener(this);
        Log.d(ListAccountsActivity.class.getName(), "Constructing Array Adapter");
//        ArrayAdapter<Account> adapter = new ArrayAdapter<Account>(this,
//                android.R.layout.simple_list_item_1, accountDB.getAllAccount());
        ListAccountsAdapter adapter = new ListAccountsAdapter(this,
                R.layout.list_accounts_row, accountDB.getAllAccount());
        Log.d(ListAccountsActivity.class.getName(), "Connecting The Adapter");
        setListAdapter(adapter);
        Log.d(ListAccountsActivity.class.getName(), "Setting The Listener");
        getListView().setOnItemLongClickListener(this);

        accountDB.close();

        // Initiate the application title
        updateTitle();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.list_accounts_header:
                gotoAddAccountPage();
                break;
            default:
                Toast.makeText(this, "Function Pending Implementation", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public boolean onItemLongClick(final AdapterView<?> parent, View view, final int position, long id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.list_accounts_dialog_title);
        builder.setMessage(R.string.list_accounts_dialog_message);
        builder.setPositiveButton(R.string.edit, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                gotoEditAccountPage((Account) parent.getItemAtPosition(position));
            }
        });
        builder.setNegativeButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                gotoDeleteAccountConfirmationPage((Account) parent.getItemAtPosition(position));
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ADD_ACCOUNT:
                if (resultCode == RESULT_OK) {
                    Log.d(ListAccountsActivity.class.getName(), "Returning From Adding Account");
                    refreshAccountList();

                    // Update the application title
                    updateTitle();
                }
                break;
            case LIST_TRANSACTION:
                if (resultCode == RESULT_OK) {
                    Log.d(ListAccountsActivity.class.getName(), "Returning From Listing Transactions");
                    refreshAccountList();

                    // Update the application title
                    updateTitle();
                }
                break;
            case DELETE_ACCOUNT:
                if (resultCode == RESULT_OK) {
                    Log.d(ListAccountsActivity.class.getName(), "Returning From Deleting Account");
                    refreshAccountList();

                    // Update the application title
                    updateTitle();
                }
                break;
            case EDIT_ACCOUNT:
                if (resultCode == RESULT_OK) {
                    Log.d(ListAccountsActivity.class.getName(), "Returning From Editing Account");
                    refreshAccountList();

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
        gotoListAccountTransactionsPage((Account) parent.getItemAtPosition(position));
        super.onListItemClick(parent, view, position, id);
    }

    @Override
    protected void onResume() {
//        accountDB.open();
        transactionDB.open();
        super.onResume();
    }

    @Override
    protected void onPause() {
//        accountDB.close();
        transactionDB.close();
        super.onPause();
    }

    private void gotoAddAccountPage() {
        Intent intent = new Intent(this, AddEditAccountActivity.class);
        intent.putExtra("add_account", "Y");
        startActivityForResult(intent, ADD_ACCOUNT);
    }

    private void gotoEditAccountPage(Account account) {
        Intent intent = new Intent(this, AddEditAccountActivity.class);
        intent.putExtra("add_account", "N");
        intent.putExtra("account_id", account.getId());
        startActivityForResult(intent, EDIT_ACCOUNT);
    }

    private void gotoDeleteAccountConfirmationPage(Account account) {
        Intent intent = new Intent(this, ConfirmDeleteActivity.class);
        intent.putExtra("element_type", "ACCOUNT");
        intent.putExtra("element_id", account.getId());
        startActivityForResult(intent, DELETE_ACCOUNT);
    }

    private void gotoListAccountTransactionsPage(Account account) {
        Intent intent = new Intent(this, ListTransactionsActivity.class);
        intent.putExtra("account_id", account.getId());
        startActivityForResult(intent, LIST_TRANSACTION);
    }

    private void refreshAccountList() {
        // Open connection to the database.
        accountDB.open();

        ArrayAdapter<Account> adapter = (ArrayAdapter<Account>) getListAdapter();
        adapter.clear();
        for (Account tmpAccount : accountDB.getAllAccount())
            adapter.add(tmpAccount);
        adapter.notifyDataSetChanged();

        // Close database connection.
        accountDB.close();
    }

    private void updateTitle() {
        // Open connection to the database.
        transactionDB.open();

        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(getResources().getConfiguration().locale);
        setTitle(getResources().getString(R.string.app_name) + " := " +
                currencyFormatter.format(transactionDB.getBalanceAcrossAllAccounts()));

        // Close database connection.
        transactionDB.close();
    }

    private class ListAccountsAdapter extends ArrayAdapter<Account> {

        Context context;
        int layoutResourceId;
        List<Account> data = null;

        public ListAccountsAdapter(Context context, int layoutResourceId, List<Account> data) {
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
                holder.listAccountsImage = (ImageView) row.findViewById(R.id.list_accounts_image);
                holder.listAccountsName = (TextView) row.findViewById(R.id.list_accounts_name);
                holder.listAccountsAmount = (TextView) row.findViewById(R.id.list_accounts_amount);

                row.setTag(holder);
            } else {
                holder = (AccountHolder) row.getTag();
            }

            Account account = data.get(position);
            float balance = transactionDB.getBalance(account.getId());

            final Locale locale = getResources().getConfiguration().locale;
            final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(locale);

//            holder.listAccountsImage.setImageDrawable(R.drawable.ic_launcher);
            holder.listAccountsName.setText(account.getNickname());
            holder.listAccountsAmount.setText(currencyFormatter.format(balance));

            if (balance < 0)
                holder.listAccountsAmount.setTextColor(Color.RED);
            else
                holder.listAccountsAmount.setTextColor(Color.WHITE);

            return row;
        }

        class AccountHolder {
            ImageView listAccountsImage;
            TextView listAccountsName;
            TextView listAccountsAmount;
        }
    }
}
