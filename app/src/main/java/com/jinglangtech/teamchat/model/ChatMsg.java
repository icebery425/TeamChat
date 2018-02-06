package com.jinglangtech.teamchat.model;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by think on 2018/2/3.
 */

public class ChatMsg extends RealmObject {

    @PrimaryKey
    public String _id;

    public String from;
    public String content;

    public String time;
    public Date dTime;

    public String roomid;
    public String name;

    public boolean isread = false;

    public boolean isMine;
}
