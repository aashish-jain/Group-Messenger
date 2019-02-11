package edu.buffalo.cse.cse486586.groupmessenger1;

import android.app.Activity;
import android.content.ContentValues;
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

    static Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_messenger);

        new Thread(new ServerTask()).start();
        /*
         * TODO: Use the TextView to display your messages. Though there is no grading component
         * on how you display the messages, if you implement it, it'll make your debugging easier.
         */
        TextView tv = (TextView) findViewById(R.id.textView1);
        tv.setMovementMethod(new ScrollingMovementMethod());
        
        /*
         * Registers OnPTestClickListener for "button1" in the layout, which is the "PTest" button.
         * OnPTestClickListener demonstrates how to access a ContentProvider.
         */
        findViewById(R.id.button1).setOnClickListener(
                new OnPTestClickListener(tv, getContentResolver()));
        
        /*
         * TODO: You need to register and implement an OnClickListener for the "Send" button.
         * In your implementation you need to get the message from the input box (EditText)
         * and send it to other AVDs.
         */


        final Button button = (Button) findViewById(R.id.button4);
        Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.authority("edu.buffalo.cse.cse486586.groupmessenger1.provider");
        uriBuilder.scheme("content");
        uri = uriBuilder.build();

        //https://developer.android.com/reference/android/widget/Button
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = (EditText) findViewById(R.id.editText1);
                TextView textView = (TextView) findViewById(R.id.textView1);
                String msg = editText.getText().toString() + "\n";
                editText.setText(""); // This is one way to reset the input box.
                textView.append("\t" + msg); // This is one way to display a string.
                ContentValues contentValues = new ContentValues();
                contentValues.put("key", "key");
                contentValues.put("value", msg);
                Log.d("UI", "Got "+msg);

//                //TODO: Add it as a new thread to keep this thread light
//                getContentResolver().insert(uri, contentValues);

                //TODO: Create a new Client Thread to dispatch messages
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
}

class ServerTask implements Runnable {
    static final int SERVER_PORT = 10000;
    static final String TAG = "Server Thread";
    public void run() {

        Log.d(TAG, "Started Server Thread");
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
                oos.close();

                Log.d("Message Received: ", message);
                // TODO: Call contentproviders insert operation
                ois.close();
            } catch (IOException e) {
                Log.e(TAG, "ServerTask socket IOException");
                //Workaround for unable to close
                if(false)
                    break;
            }


        try {
            if(socket!=null)
                socket.close();
            if(ois!=null)
                ois.close();
            if(oos!=null)
                oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ClientTask extends AsyncTask<String, Void, Void> {
    static final int[] REMOTE_PORTS = new int[]{11108, 11112, 11116, 11120, 11124};
    static final String TAG = "Client Task";

    @Override
    protected Void doInBackground(String... msgs) {
        try {

            List<Socket> sockets = new LinkedList<Socket>();

            for(int remotePort: REMOTE_PORTS)
                sockets.add( new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                    remotePort));

            String msgToSend = msgs[0];

            //https://stackoverflow.com/questions/5680259/using-sockets-to-send-and-receive-data
            //https://stackoverflow.com/questions/49654735/send-objects-and-strings-over-socket

            int count = 0;
            for( Socket socket : sockets) {
                Log.d("Client", "Sending message " + msgToSend);
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                oos.writeUTF(msgToSend);
                count++;
                Log.d("Client", "Sent to "+ count);
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                byte ack = ois.readByte();
                oos.close();
                socket.close();
            }
        } catch (UnknownHostException e) {
            Log.e(TAG, "ClientTask UnknownHostException");
        } catch (IOException e) {
            Log.e(TAG, "ClientTask socket IOException");
        }

        return null;
    }
}
