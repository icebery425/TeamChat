package com.jinglangtech.teamchat.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by icebery on 2017/4/25 0025.
 *
 * 聊天用户
 */

public class ChatUser extends RealmObject {

    @PrimaryKey
    public String _id;

    public String name;
    public String avatar;
    public String account;
}
