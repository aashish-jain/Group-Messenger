package edu.buffalo.cse.cse486586.groupmessenger1;

import android.provider.BaseColumns;

//https://developer.android.com/training/data-storage/sqlite
public class KeyValueContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private KeyValueContract(){}

    /* Inner Class that defines the table contents */
    public static class KeyValue implements BaseColumns{
        public static final String TABLE_NAME = "key_value_table";
        public static final String COLUMN_NAME_KEY= "key";
        public static final String COLUMN_NAME_VALUE = "value";
    }

    public static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + KeyValue.TABLE_NAME + " (" +
                    KeyValue.COLUMN_NAME_KEY + "STRING PRIMARY KEY," +
                    KeyValue.COLUMN_NAME_VALUE + " STRING)";

    public static final String SQL_CLEAR_ROWS = "DELETE FROM " + KeyValue.TABLE_NAME;

    public static final String SQL_DELETE_TABLE=
            "DROP TABLE IF EXISTS " + KeyValue.TABLE_NAME;
}
