package com.jinglangtech.teamchat.dbmanager;

import android.util.Log;


import io.realm.DynamicRealm;
import io.realm.RealmMigration;
import io.realm.RealmSchema;

public class DbMigration implements RealmMigration {

    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {

        RealmSchema schema = realm.getSchema();

         if (oldVersion == 1){
            Log.d("DbMigration","PDA-class oldVersion == 1");
            oldVersion++;
        }
    }
}
