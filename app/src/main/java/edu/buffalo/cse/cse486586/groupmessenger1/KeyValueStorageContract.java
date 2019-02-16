package edu.buffalo.cse.cse486586.groupmessenger1;

import android.provider.BaseColumns;

//https://developer.android.com/training/data-storage/sqlite
public class KeyValueStorageContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private KeyValueStorageContract() {}

    /* Inner class that defines the table contents */
    public static class KeyValueEntry implements BaseColumns {
        public static final String TABLE_NAME = "key_value";
        public static final String COLUMN_KEY = "key";
        public static final String COLUMN_VALUE = "value";
    }

    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + KeyValueEntry.TABLE_NAME + " (" +
                    KeyValueEntry.COLUMN_VALUE + " STRING," +
                    KeyValueEntry.COLUMN_KEY + " STRING PRIMARY KEY)";

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + KeyValueEntry.TABLE_NAME;
}
