package edu.buffalo.cse.cse486586.groupmessenger1;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {KeyValueStorage.class}, version = 1)
public abstract class KeyValueStorageDB  extends RoomDatabase {
    public abstract KeyValueStorageDB keyValueStorageDB();
}
