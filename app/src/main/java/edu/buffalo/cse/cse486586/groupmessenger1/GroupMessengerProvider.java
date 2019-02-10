package edu.buffalo.cse.cse486586.groupmessenger1;

import android.app.Application;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * GroupMessengerProvider is a key-value table. Once again, please note that we do not implement
 * full support for SQL as a usual ContentProvider does. We re-purpose ContentProvider's interface
 * to use it as a key-value table.
 * 
 * Please read:
 * 
 * http://developer.android.com/guide/topics/providers/content-providers.html
 * http://developer.android.com/reference/android/content/ContentProvider.html
 * 
 * before you start to get yourself familiarized with ContentProvider.
 * 
 * There are two methods you need to implement---insert() and query(). Others are optional and
 * will not be tested.
 * 
 * @author stevko
 *
 */

public class GroupMessengerProvider extends ContentProvider {

    //https://stackoverflow.com/questions/36652944/how-do-i-read-in-binary-data-files-in-java
    static final String fileName = "database.db";
    static String[] columns = {"key", "value"};
    static int newKey;

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // You do not need to implement this.
        return 0;
    }

    @Override
    public String getType(Uri uri) {
        // You do not need to implement this.
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        /*
         * TODO: You need to implement this method. Note that values will have two columns (a key
         * column and a value column) and one row that contains the actual (key, value) pair to be
         * inserted.
         * 
         * For actual storage, you can use any option. If you know how to use SQL, then you can use
         * SQLite. But this is not a requirement. You can use other storage options, such as the
         * internal storage option that we used in PA1. If you want to use that option, please
         * take a look at the code for PA1.
         */

        // Open OutputStream and write to the file fo all values
        FileOutputStream fileOutputStream = null;
        try {
            //https://docs.oracle.com/javase/7/docs/api/java/io/FileOutputStream.html
            //https://stackoverflow.com/questions/4015773/the-method-openfileoutput-is-undefined
            fileOutputStream = getContext().openFileOutput(fileName, Context.MODE_PRIVATE | Context.MODE_APPEND);
            for( String key: values.keySet()){
                String toWrite = newKey + " " + values.get(key) + "\n";
                fileOutputStream.write(toWrite.getBytes());
                newKey++;
                Log.d("Provider","Wrote "+ toWrite);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Log.v("insert", values.toString());
        return uri;
    }

    @Override
    public boolean onCreate() {
        // If you need to perform any one-time initialization task, please do it here.
        File databaseFile;
        databaseFile = new File(fileName);
        // If the file exists delete it as the app resets the database file every time its re-run for assignment
        if (databaseFile.exists()) {
            databaseFile.delete();
            try {
                databaseFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        newKey = 0;
        return true;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // You do not need to implement this.
        return 0;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        /*
         * TODO: You need to implement this method. Note that you need to return a Cursor object
         * with the right format. If the formatting is not correct, then it is not going to work.
         *
         * If you use SQLite, whatever is returned from SQLite is a Cursor object. However, you
         * still need to be careful because the formatting might still be incorrect.
         *
         * If you use a file storage option, then it is your job to build a Cursor * object. I
         * recommend building a MatrixCursor described at:
         * http://developer.android.com/reference/android/database/MatrixCursor.html
         */

        String requiredValue = null;
        BufferedReader bufferedReader = null;
        InputStreamReader inputStreamReader = null;
        FileInputStream fileInputStream = null;
        try {
            //https://docs.oracle.com/javase/7/docs/api/java/io/BufferedReader.html
            //https://stackoverflow.com/questions/5200187/convert-inputstream-to-bufferedreader
            fileInputStream = getContext().openFileInput(fileName);
            inputStreamReader = new InputStreamReader(fileInputStream);
            bufferedReader = new BufferedReader(inputStreamReader);

            String s = null;
            //To handle large files too. TODO: Replace with DB for PA 2B.
            while ( (s = bufferedReader.readLine()) != null){
                //https://stackoverflow.com/questions/3481828/how-to-split-a-string-in-java
                String[] values = s.split(" ");
                System.out.println("Querying " + values[0]+" : "+values[1]);
                if(values[0].equals(selection)) {
                    requiredValue = values[1];
                    break;
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bufferedReader.close();
                inputStreamReader.close();
                fileInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return  null;
    }
}
