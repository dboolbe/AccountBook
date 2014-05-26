package com.thesyndicate.android.dboolbe.AccountBook.AccountTypeDAO;

/**
 * Created by dboolbe on 3/27/14.
 */
public class AccountType {

    private long id;
    private String type;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type;
    }
}
