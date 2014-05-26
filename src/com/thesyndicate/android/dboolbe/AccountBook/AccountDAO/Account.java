package com.thesyndicate.android.dboolbe.AccountBook.AccountDAO;

/**
 * Created by dboolbe on 3/27/14.
 */
public class Account {

    private long id;
    private long typeId;
    private String nickname;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTypeId() {
        return typeId;
    }

    public void setTypeId(long typeId) {
        this.typeId = typeId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    // Will be used by the ArrayAdapter in the ListView
    @Override
    public String toString() {
        return nickname;
    }
}
