package com.jinglangtech.teamchat.dbmanager;

import android.util.Log;

import com.jinglangtech.teamchat.model.ChatGroup;
import com.jinglangtech.teamchat.model.ChatMsg;
import com.jinglangtech.teamchat.util.TimeConverterUtil;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 *  Realm
 *
 */
public class RealmDbManger implements DbManager {

    private final static boolean DEBUG = false;
    private final static String TAG = "Realm";

    private static RealmDbManger realmDbManger = null;

    public static RealmDbManger getRealmInstance(){
        if (realmDbManger == null){
            realmDbManger = new RealmDbManger();
        }
        return  realmDbManger;
    }

    @Override
    public List<ChatGroup> findAll() {

        Realm realm = null;
        RealmResults<ChatGroup> results = null;
        try{
            realm = Realm.getDefaultInstance();
            results = Realm.getDefaultInstance().where(ChatGroup.class).findAll();
            if (DEBUG){
                for (int i = 0 ; i< results.size();i++){
                    Log.i(TAG,results.get(i).toString());
                }
            }
        }catch (Exception e){
            if (realm != null){
                realm.close();
            }
        }

        return results;
    }

    @Override
    public List<ChatGroup> findSomeElement(String key, String value) {
        RealmResults<ChatGroup> results = Realm.getDefaultInstance().where(ChatGroup.class).equalTo(key, value).findAll();
        if (DEBUG){
            for (int i = 0 ; i< results.size();i++){
                Log.i(TAG,results.get(i).toString());
            }
        }
        return results;
    }

    //查询每个聊天室所有聊天数据
    @Override
    public List<ChatMsg> findLocalMsgWithRoomId(String roomId) {
        RealmResults<ChatMsg> realmResults = Realm.getDefaultInstance().where(ChatMsg.class)
                .equalTo("roomid", roomId)
                .findAllSorted("dTime");
        return realmResults;
    }

    //查询某个聊天室的所有成员
    public ChatGroup findGroupInfoWithRoomId(String groupId){
        ChatGroup realmResults = Realm.getDefaultInstance().where(ChatGroup.class).equalTo("_id", groupId).findFirst();
        return realmResults;
    }

    @Override
    public List coditionQuery(String key, String value) {
        return null;
    }

    @Override
    public ChatGroup conditionQueryWithOne(String keys, String values) {
        return  Realm.getDefaultInstance().where(ChatGroup.class).equalTo(keys, values).findFirst();
    }

    @Override
    public ChatMsg findMaxDateOne(String key, String value){
        RealmResults<ChatMsg> result = Realm.getDefaultInstance().where(ChatMsg.class).equalTo(key, value).findAll();
        if (result.size() > 0){
            RealmResults<ChatMsg> msgList = result.sort("dTime", Sort.DESCENDING);//逆序排列
            return  msgList.first();
        }
        return null;
    }

    //查询某个聊天室未读的聊天记录数量
    @Override
    public long findUnread(String key, String value){
        long ret = -1;
        ret = Realm.getDefaultInstance().where(ChatMsg.class)
                .equalTo(key, value)
                .equalTo("isread", false)
                .count();
        return ret;
    }

    @Override
    public ChatGroup modifyOneElement(Object paramSku) {
        ChatGroup mSku = (ChatGroup)paramSku;
        ChatGroup sku = Realm.getDefaultInstance().where(ChatGroup.class).equalTo("_id", mSku._id).findFirst();
        Realm.getDefaultInstance().beginTransaction();
        //SkuUtil.customclone(sku, mSku);//克隆
        Realm.getDefaultInstance().commitTransaction();
        return sku;
    }

    //修改消息发送结果及更新此条消息发送时间
    @Override
    public  void modifySendResult(Object object){
        ChatMsg msg = (ChatMsg)object;
        ChatMsg oldMsg = Realm.getDefaultInstance().where(ChatMsg.class).equalTo("_id", msg._id).findFirst();
        Realm.getDefaultInstance().beginTransaction();
        oldMsg.isSend = msg.isSend;
        oldMsg.dTime = msg.dTime;
        oldMsg.time = msg.time;
        Realm.getDefaultInstance().commitTransaction();

    }

    //修改消息发送结果及更新此条消息发送时间
    @Override
    public  void modifySendResultExt(Object object,String oldId){
        ChatMsg msg = (ChatMsg)object;
        ChatMsg oldMsg = Realm.getDefaultInstance().where(ChatMsg.class).equalTo("_id", oldId).findFirst();
        Realm.getDefaultInstance().beginTransaction();
        oldMsg._id = msg._id;
        oldMsg.isSend = msg.isSend;
        oldMsg.dTime = msg.dTime;
        oldMsg.time = msg.time;
        Realm.getDefaultInstance().commitTransaction();

    }

    //删除指定ID的记录
    @Override
    public void delOneElement(Object obj) {
        ChatMsg sku = (ChatMsg)obj;
        ChatMsg result = Realm.getDefaultInstance().where(ChatMsg.class).equalTo("_id", sku._id).findFirst();
        Realm.getDefaultInstance().beginTransaction();
        result.deleteFromRealm();
        Realm.getDefaultInstance().commitTransaction();
    }

    //删除指定聊天室的消息
    public  void deleteGroupByChatId(String roomId){
        RealmResults<ChatMsg> results = Realm.getDefaultInstance().where(ChatMsg.class).equalTo("roomid", roomId).findAll();
        Realm.getDefaultInstance().beginTransaction();
        results.deleteAllFromRealm();
        Realm.getDefaultInstance().commitTransaction();
    }

    @Override
    public   void modifyGroupUnreadQuatity(Object obj){
        ChatGroup sku = (ChatGroup) obj;
        Realm realm = Realm.getDefaultInstance();
        ChatGroup tempSku = Realm.getDefaultInstance().where(ChatGroup.class).equalTo("_id", sku._id).findFirst();
        realm.beginTransaction();
        tempSku.setUnread(sku.getUnread());
        realm.commitTransaction();
    }

    @Override
    public void deleteAll() {
        RealmResults<ChatMsg> results = Realm.getDefaultInstance().where(ChatMsg.class).findAll();
        Realm.getDefaultInstance().beginTransaction();
        results.deleteAllFromRealm();
        Realm.getDefaultInstance().commitTransaction();
    }

    @Override
    public void delSomeElementWithTime(String time) {
        String strTime = TimeConverterUtil.utc2Local(time, "YYYY-MM-DD HH:mm:ss");
        Date dTime = Date.valueOf(strTime);
        RealmResults<ChatMsg> results = Realm.getDefaultInstance().where(ChatMsg.class).greaterThanOrEqualTo("time", dTime).findAll();
        Realm.getDefaultInstance().beginTransaction();
        boolean result = results.deleteAllFromRealm();
        Realm.getDefaultInstance().commitTransaction();
    }





    //单个元素的插入
    @Override
    public void insertOneElement(Object obj) {
        ChatMsg msg = (ChatMsg) obj;
        Realm.getDefaultInstance().beginTransaction();
        Realm.getDefaultInstance().copyToRealmOrUpdate(msg);
        Realm.getDefaultInstance().commitTransaction();
    }

    //集合的插入
    @Override
    public void insetList(Collection collection) {

        ArrayList<ChatMsg> list = (ArrayList<ChatMsg>)collection;

        Realm.getDefaultInstance().beginTransaction();
        for (ChatMsg sku: list){
            insert(sku);
        }
        Realm.getDefaultInstance().commitTransaction();
    }

    @Override
    public void insertListWithNoAction(Collection collection) {
        ArrayList<ChatMsg> list = (ArrayList<ChatMsg>)collection;

        for (ChatMsg sku: list){
            insert(sku);
        }
    }

    @Override
    public void insertLocalMsgList(List<ChatMsg> list) {

        Realm.getDefaultInstance().beginTransaction();
        for (ChatMsg msg: list){
            Realm.getDefaultInstance().copyToRealmOrUpdate(msg);
        }
        Realm.getDefaultInstance().commitTransaction();
    }

    private void insert(ChatMsg sku){
        Realm.getDefaultInstance().copyToRealmOrUpdate(sku);
    }


    @Override
    public List<ChatMsg> findSynAll(){
        RealmResults<ChatMsg> cats = Realm.getDefaultInstance().where(ChatMsg.class).findAllAsync();
        return  cats;
    }

    public interface FindAllChangeListener{
        public void call(RealmResults<ChatMsg> skuList);
    }

    FindAllChangeListener changeListener = null;
    public void setChangeListener(FindAllChangeListener changeListener){
        this.changeListener = changeListener;
    }



}
