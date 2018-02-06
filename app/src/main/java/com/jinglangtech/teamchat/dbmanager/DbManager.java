package com.jinglangtech.teamchat.dbmanager;

import com.jinglangtech.teamchat.model.ChatGroup;
import com.jinglangtech.teamchat.model.ChatMsg;

import java.util.Collection;
import java.util.List;

/**
 *  采用工厂方法模式，有助于后期数据库的替换
 *  @author  xjbin
 *
 */
public interface DbManager {

    //查询所有
    public abstract List findAll();

    //条件查询1(返回一个集合)
    public abstract List coditionQuery(String key, String value);

    //条件查询2
    public abstract Object conditionQueryWithOne(String key, String value);


    //查询聊天数据表－ChatMsg
    //查询每个聊天室最后一条聊天记录，用于聊天室分组显示
    public abstract Object findMaxDateOne(String key, String value);

    //查询某个聊天室未读的聊天记录数量
    public abstract long findUnread(String key, String value);

    //查询每个聊天室所有聊天数据
    public abstract List<ChatMsg> findLocalMsgWithRoomId(String msgId);

    //查询某个聊天室的所有成员
    public abstract ChatGroup findGroupInfoWithRoomId(String groupId);

    //编辑(单个)
    public abstract Object modifyOneElement(Object object);

    //删除
    public abstract  void deleteAll();

    //增加
    public abstract  void insertOneElement(Object obj);

    //批量增加
    public abstract void insetList(Collection collection);

    public abstract  void insertListWithNoAction(Collection collection);

    //异步查找
    public abstract List findSynAll();

    //
    public abstract List findSomeElement(String key, String value);

    //修改数量
    public abstract  void modifyGroupUnreadQuatity(Object object);

    public abstract void delOneElement(Object object);

    //删除指定时间下所有的sku
    public abstract void delSomeElementWithTime(String time);

    //
    public abstract  void insertLocalMsgList(List<ChatMsg> localList);


}
