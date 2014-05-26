package com.thesyndicate.android.dboolbe.AccountBook.TransferDAO;

/**
 * Created by dboolbe on 4/7/14.
 */
public class Transfer {

    private long id;
    private long debitTransactionId;
    private long creditTransactionId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getDebitTransactionId() {
        return debitTransactionId;
    }

    public void setDebitTransactionId(long debitTransactionId) {
        this.debitTransactionId = debitTransactionId;
    }

    public long getCreditTransactionId() {
        return creditTransactionId;
    }

    public void setCreditTransactionId(long creditTransactionId) {
        this.creditTransactionId = creditTransactionId;
    }
}
