package com.jinglangtech.teamchat.model;

import java.util.List;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

/**
 * Created by think on 2018/2/3.
 */

public class ChatGroup extends RealmObject{

    @PrimaryKey
    public String _id;

    public String name;
    public String avatar;
    public boolean canuse;

    public String msg;
    public String time;

    public int unread;

    @Ignore
    public List<ChatUser> group;

    public int getUnread() {
        return unread;
    }

    public void setUnread(int unread) {
        this.unread = unread;
    }
}
