package edu.buffalo.cse.cse486586.groupmessenger1;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcel;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * GroupMessengerActivity is the main Activity for the assignment.
 * 
 * @author stevko
 *
 */
public class GroupMessengerActivity extends Activity {

    static  TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_messenger);
        new Thread(new ServerTask()).start();
        /*
         * TODO: Use the TextView to display your messages. Though there is no grading component
         * on how you display the messages, if you implement it, it'll make your debugging easier.
         */
        textView = (TextView) findViewById(R.id.textView1);
        textView.setMovementMethod(new ScrollingMovementMethod());

        /*
         * Registers OnPTestClickListener for "button1" in the la`````yout, which is the "PTest" button.
         * OnPTestClickListener demonstrates how to access a ContentProvider.
         */
        findViewById(R.id.button1).setOnClickListener(
                new OnPTestClickListener(textView, getContentResolver()));

        /*
         * TODO: You need to register and implement an OnClickListener for the "Send" button.
         * In your implementation you need to get the message from the input box (EditText)
         * and send it to other AVDs.
         */

        final Button button = (Button) findViewById(R.id.button4);

        //https://developer.android.com/reference/android/widget/Button
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = (EditText) findViewById(R.id.editText1);
                textView = (TextView) findViewById(R.id.textView1);
                String msg = editText.getText().toString() + "\n";
                editText.setText(""); // This is ``one way to reset the input box.
                Log.d("UI", "Got " + msg);

                new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, msg);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_group_messenger, menu);
        return true;
    }


    private class ServerTask implements Runnable {
        static final int SERVER_PORT = 10000;
        static final String TAG = "Server Thread";

        int key;

        //https://stackoverflow.com/questions/5853167/runnable-with-a-parameter/5853198
        private class UpdateTextView implements Runnable{
            String message;

            UpdateTextView(String msg){
                message = msg;
            }

            @Override
            public void run() {
                textView.append(message);
            }
        }

        public void run() {

            //Open a socket
            ServerSocket serverSocket = null;
            try {
                serverSocket = new ServerSocket(SERVER_PORT);
            } catch (IOException e) {
                e.printStackTrace();
            }

            //read from socket to ObjectInputStream object
            Socket socket = null;
            ObjectOutputStream oos = null;
            ObjectInputStream ois = null;

            Uri.Builder uriBuilder = new Uri.Builder();
            uriBuilder.authority("edu.buffalo.cse.cse486586.groupmessenger1.provider");
            uriBuilder.scheme("content");
            Uri uri = uriBuilder.build();

            while (true)
                try {
                    socket = serverSocket.accept();

                    //https://stackoverflow.com/questions/11521027/whats-the-difference-between-dataoutputstream-and-objectoutputstream
                    ois = new ObjectInputStream(socket.getInputStream());

                    //Read from the socket
                    String message = ois.readUTF();

                    //Acknowledgement
                    oos = new ObjectOutputStream(socket.getOutputStream());
                    oos.writeByte(255);
                    oos.flush();

                    ContentValues contentValues = new ContentValues();
                    contentValues.put("key", new Integer(key).toString());
                    contentValues.put("value", message);
                    key++;

                    //https://stackoverflow.com/questions/12716850/android-update-textview-in-thread-and-runnable
                    //Update the UI thread
                    runOnUiThread(new UpdateTextView(message));

                    getContentResolver().insert(uri, contentValues);

                    oos.close();
                    ois.close();

                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "ServerTask socket IOException");
                    //Workaround for unable to close
                    if (false)
                        break;
                }
        }
    }

    private class ClientTask extends AsyncTask<String, Void, Void> {
        final int[] REMOTE_PORTS = new int[]{11108, 11112, 11116, 11120, 11124};
        final String TAG = "Client Task";

        @Override
        protected Void doInBackground(String... msgs) {
            Socket socket = null;
            String msgToSend = msgs[0];
            try {
                for (int remotePort : REMOTE_PORTS) {
                    socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                            remotePort);

                    //https://stackoverflow.com/questions/5680259/using-sockets-to-send-and-receive-data
                    //https://stackoverflow.com/questions/49654735/send-objects-and-strings-over-socket

                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                    oos.writeUTF(msgToSend);
                    oos.flush();
                    ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                    byte ack = ois.readByte();
                    oos.close();
                    socket.close();
                }
            } catch (UnknownHostException e) {
                Log.e(TAG, "ClientTask UnknownHostException");
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "ClientTask socket IOException");
            }
            Log.d("Client", "Sent message " + msgToSend);
            return null;
        }
    }
}