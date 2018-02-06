package com.jinglangtech.teamchat.model;

import java.util.List;

import io.realm.RealmList;
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

    public long unread;

    public RealmList<ChatUser> group;

    public long getUnread() {
        return unread;
    }

    public void setUnread(long unread) {
        this.unread = unread;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public boolean isCanuse() {
        return canuse;
    }

    public void setCanuse(boolean canuse) {
        this.canuse = canuse;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public RealmList<ChatUser> getGroup() {
        return group;
    }

    public void setGroup(RealmList<ChatUser> group) {
        this.group = group;
    }
}
