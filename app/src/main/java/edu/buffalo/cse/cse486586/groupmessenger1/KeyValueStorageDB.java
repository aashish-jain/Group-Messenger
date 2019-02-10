package edu.buffalo.cse.cse486586.groupmessenger1;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

//https://codelabs.developers.google.com/codelabs/android-room-with-a-view/#6
@Database(entities = {KeyValueStorage.class}, version = 1)
public abstract class KeyValueStorageDB  extends RoomDatabase {
    public abstract KeyValueStorageDB keyValueStorageDB();

    private static volatile KeyValueStorageDB INSTANCE;

    static KeyValueStorageDB getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (KeyValueStorageDB.class) {
                if (INSTANCE == null) {
                    // Create database here
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            KeyValueStorageDB.class, "key")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
