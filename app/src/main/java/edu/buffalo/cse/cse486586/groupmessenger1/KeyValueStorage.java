package edu.buffalo.cse.cse486586.groupmessenger1;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

//https://codelabs.developers.google.com/codelabs/android-room-with-a-view/#3
@Entity(tableName = "key_value_storage")
public final class KeyValueStorage {
    @PrimaryKey
    @ColumnInfo(name = "key")
    private  String key;

    @ColumnInfo(name = "value")
    private  String value;


    public  KeyValueStorage(@NonNull String key, @NonNull String value){
        this.key = key;
        this.value = value;
    }

    public String getKey(){
        return  this.key;
    }

    public  String getValue(){
        return  this.value;
    }
}
