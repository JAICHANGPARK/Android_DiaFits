package com.dreamwalker.diabetesfits.database;

import io.realm.RealmConfiguration;

public class RealmManagement {

    public static RealmConfiguration getRealmConfiguration() {
        return new RealmConfiguration.Builder().schemaVersion(2).migration(new MyMigration()).build();
    }
}
