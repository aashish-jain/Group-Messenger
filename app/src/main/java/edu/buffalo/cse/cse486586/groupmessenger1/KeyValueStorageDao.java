package edu.buffalo.cse.cse486586.groupmessenger1;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

@Dao
public interface KeyValueStorageDao {
    @Insert
    public void  insert(KeyValueStorage keyValueStorage);

    @Query("SELECT value from key_value_storage where `key` = :primaryKey")
    public String getValueForKey(String primaryKey);
}
