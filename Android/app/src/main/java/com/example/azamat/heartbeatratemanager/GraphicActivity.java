package com.example.azamat.heartbeatratemanager;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.UUID;

public class GraphicActivity extends AppCompatActivity {

//    ----VAR for GRAPH-------

    GraphView graph;
    LineGraphSeries<DataPoint> series;
    private double lastXPoint = 0;

//    ----------END---------------

    private static final String TAG = "bluetooth2";

    private DatabaseReference myRef;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    public static Handler h;


    int forDevision = 0;
    int forAvarageBpm = 0;

    TextView bpmTV;

    private static final int REQUEST_ENABLE_BT = 1;
    private  static final int RECIEVE_MESSAGE = 1;
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private StringBuilder sb = new StringBuilder();

    private ConnectedThread mConnectedThread;

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");


    private static String address = "30:14:07:31:04:37";

    int iCount = 0;
    Date date;
    String dateOnString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graphic);
        myRef = FirebaseDatabase.getInstance().getReference();
        graph = (GraphView)findViewById(R.id.graph_graph_activity);
        bpmTV = (TextView)findViewById(R.id.bmp_txt);

        series = new LineGraphSeries<>(new DataPoint[]{});
        graph.addSeries(series);

        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(10);
        graph.getViewport().setXAxisBoundsManual(true);

        graph.getViewport().setScalable(true);

        date = new Date();
        dateOnString = date.toString().replace("GMT+06:00 ", "");
        dateOnString = dateOnString.replace(" ", "Q");
        dateOnString = dateOnString.replace(":", "Q");

//        myRef.child(user.getUid()).child("HeartRate").child(date.toString()).child("Pulse").setValue("68");
        myRef.child(user.getUid()).child("Measure Dates").push().setValue(dateOnString);

        h = new Handler() {
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case RECIEVE_MESSAGE:
                        byte[] readBuf = (byte[]) msg.obj;
                        String strIncom = new String(readBuf, 0, msg.arg1);
                        sb.append(strIncom);
                        if (sb.length()>4)
                        {
                            int endOfMessage = sb.indexOf("S")+1;
                            int endOfPrefixCode = sb.indexOf("G");
                            String results = sb.substring(0, endOfMessage);
                            sb.delete(0, endOfMessage);
                            String prefix = results.substring(1, endOfPrefixCode);
                            String date = results.substring(endOfPrefixCode, endOfMessage-1);
                            if (prefix.equals("graph"))
                            {
                                try
                                {
                                    if (Integer.parseInt(date)<1024)
                                    {
                                        myRef.child(user.getUid()).child("HeartRate").child(dateOnString).child(String.valueOf(iCount)).setValue(Integer.parseInt(date));
                                        if (iCount==101)
                                        {
                                            iCount=0;
                                        }
                                        myRef.child(user.getUid()).child("Example").child(String.valueOf(iCount))
                                                .setValue(Integer.parseInt(date));
                                        lastXPoint++;
                                        iCount++;
                                        series.appendData(new DataPoint(lastXPoint, Integer.parseInt(date)), true, 1023);
                                    }
                                }
                                catch (Exception e)
                                {
                                    myRef.child("Errors").child("graphDate").push().setValue(results);
                                }
                            }
                            else if (prefix.equals("bpm"))
                            {
                                try
                                {
                                    int dateBPM=Integer.parseInt(date);
                                    bpmTV.setText(date);
                                    forAvarageBpm = forAvarageBpm+dateBPM;
                                    forDevision++;
                                    int avarageBPM = forAvarageBpm/forDevision;
                                    myRef.child(user.getUid()).child("BPM").child("Avarage").setValue(avarageBPM);
                                }
                                catch (Exception e)
                                {
                                    myRef.child("Errors").child("BPM").push().setValue(results);
                                }
                            }
                            break;
                        }
                }
            };
        };

        btAdapter = BluetoothAdapter.getDefaultAdapter();
        checkBTState();
    }

    @Override
    protected void onStop() {
        super.onStop();
        btAdapter.disable();
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d(TAG, "...onResume - попытка соединения...");


        BluetoothDevice device = btAdapter.getRemoteDevice(address);
        try {
            btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) {
            errorExit("Fatal Error", "In onResume() and socket create failed: " + e.getMessage() + ".");
        }

        btAdapter.cancelDiscovery();

        Log.d(TAG, "...Соединяемся...");
        try {
            btSocket.connect();
            Log.d(TAG, "...Соединение установлено и готово к передачи данных...");
        } catch (IOException e) {
            Log.d(TAG, "Ошибка соединения"+e.getMessage()+"...");
            try {
                btSocket.close();
            } catch (IOException e2) {
                errorExit("Fatal Error", "In onResume() and unable to close socket during connection failure" + e2.getMessage() + ".");
            }
        }

        Log.d(TAG, "...Создание Socket...");

        mConnectedThread = new ConnectedThread(btSocket);
        mConnectedThread.start();
    }

    @Override
    public void onPause() {
        super.onPause();

        Log.d(TAG, "...In onPause()...");

        try     {
            btSocket.close();
        } catch (IOException e2) {
            errorExit("Fatal Error", "In onPause() and failed to close socket." + e2.getMessage() + ".");
        }
    }

    private void checkBTState() {
        if(btAdapter==null) {
            errorExit("Fatal Error", "Bluetooth не поддерживается");
        } else {
            if (btAdapter.isEnabled()) {
                Log.d(TAG, "...Bluetooth включен...");
            } else {
                Intent enableBtIntent = new Intent(btAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
    }

    private void errorExit(String title, String message){
        Toast.makeText(getBaseContext(), title + " - " + message, Toast.LENGTH_LONG).show();
        finish();
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;


            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[256];
            int bytes;



            while (true) {
                try {

                    bytes = mmInStream.read(buffer);
                    h.obtainMessage(RECIEVE_MESSAGE, bytes, -1, buffer).sendToTarget();
                } catch (IOException e) {
                    Log.d(TAG, "...Ошибка get данных: " + e.getMessage() + "...");
                    break;
                }
            }
        }


        public void write(String message) {
            Log.d(TAG, "...Данные для отправки: " + message + "...");
            byte[] msgBuffer = message.getBytes();
            try {
                mmOutStream.write(msgBuffer);
                InputStream d = mmInStream;
            } catch (IOException e) {
                Log.d(TAG, "...Ошибка отправки данных: " + e.getMessage() + "...");
            }
        }


        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }


    public void BluetoothConnect()
    {

        Log.d(TAG, "...onResume - попытка соединения...");



        BluetoothDevice device = btAdapter.getRemoteDevice(address);



        try {
            btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) {
            errorExit("Fatal Error", "In onResume() and socket create failed: " + e.getMessage() + ".");
        }


        btAdapter.cancelDiscovery();


        Log.d(TAG, "...Соединяемся...");
        try {
            btSocket.connect();
            Log.d(TAG, "...Соединение установлено и готово к передачи данных...");
        } catch (IOException e) {
            Log.d(TAG, "Ошибка соединения"+e.getMessage()+"...");
            try {
                btSocket.close();
            } catch (IOException e2) {
                errorExit("Fatal Error", "In onResume() and unable to close socket during connection failure" + e2.getMessage() + ".");
            }
        }


        Log.d(TAG, "...Создание Socket...");

        mConnectedThread = new ConnectedThread(btSocket);
        mConnectedThread.start();
    }
}