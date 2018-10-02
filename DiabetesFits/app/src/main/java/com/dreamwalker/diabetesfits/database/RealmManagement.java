package com.dreamwalker.diabetesfits.database;

import io.realm.RealmConfiguration;

public class RealmManagement {

    public static RealmConfiguration getRealmConfiguration() {
        return new RealmConfiguration.Builder().schemaVersion(1).migration(new MyMigration()).build();
    }
}
