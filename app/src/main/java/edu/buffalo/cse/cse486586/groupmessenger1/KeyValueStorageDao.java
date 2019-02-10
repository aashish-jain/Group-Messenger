package edu.buffalo.cse.cse486586.groupmessenger1;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.database.Cursor;

//https://codelabs.developers.google.com/codelabs/android-room-with-a-view/#4
@Dao
public interface KeyValueStorageDao {
    @Insert
    public void  insert(KeyValueStorage keyValueStorage);

    //https://stackoverflow.com/questions/45326479/how-to-get-list-of-cursor-object-from-room-dao-access
    @Query("SELECT value from key_value_storage where `key` = :primaryKey")
    public Cursor getValueForKey(String primaryKey);
}
