package com.jinglangtech.teamchat.dbmanager;

/**
 *
 *  采用工厂方法模式
 */
public class DBFactory {

    public static DbManager getDBInstance(){
        return RealmDbManger.getRealmInstance();
    }
}
