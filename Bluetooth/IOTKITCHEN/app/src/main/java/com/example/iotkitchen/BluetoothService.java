package com.example.iotkitchen;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
import java.util.Vector;

//Deals with all bluetooth related activity, connecting and receiving data from the sensors
public class BluetoothService extends Service {
        private BluetoothAdapter mBluetoothAdapter;
        public static final String BT_DEVICE = "btdevice";
        public static final String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";
        public static final int STATE_NONE = 0; // we're doing nothing
        public static final int STATE_LISTEN = 1; // now listening for incoming
        // connections
        public static final int STATE_CONNECTING = 2; // now initiating an outgoing
        // connection
        public static final int STATE_CONNECTED = 3; // now connected to a remote
        // device
        private ConnectThread mConnectThread;
        private static ConnectedThread mConnectedThread;
        // public mInHangler mHandler = new mInHangler(this);
        private static Handler mHandler = null;
        public static int mState = STATE_NONE;
        public static String deviceName;
        public Vector<Byte> packdata = new Vector<>(2048);
        public static BluetoothDevice device = null;

        @Override
        public void onCreate() {
            Log.d("BluetoothService", "Service started");
            super.onCreate();
        }

        //When an activity binds to this service
        @Override
        public IBinder onBind(Intent intent) {
            mHandler = ((MyApplication) getApplication()).getHandler();
            return mBinder;
        }

        public class LocalBinder extends Binder {
            BluetoothService getService() {
                return BluetoothService.this;
            }
        }



        private final IBinder mBinder = new LocalBinder();

        //When this activity starts, try to connect to the bluetooth module
        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            Log.d("BluetoothService", "Onstart Command");
            mHandler = ((MyApplication) getApplication()).getHandler();
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter != null) {                //If bluetooth is supported try and connect
                String macAddress = "00:14:03:06:16:AD";    //Device mac address
                device = mBluetoothAdapter.getRemoteDevice(macAddress);
                deviceName = device.getName();
                if (macAddress != null && macAddress.length() > 0) {
                    connectToDevice(macAddress);
                } else {
                    stopSelf();
                    return 0;
                }
            }
            String stopservice = intent.getStringExtra("stopservice");
            if (stopservice != null && stopservice.length() > 0) {
                stop();
            }
            return START_STICKY;
        }

        //Try to connect to the device
        private synchronized void connectToDevice(String macAddress) {
            BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(macAddress);
            if (mState == STATE_CONNECTING) {
                if (mConnectThread != null) {
                    mConnectThread.cancel();
                    mConnectThread = null;
                }
            }

            // Cancel any thread currently running a connection
            if (mConnectedThread != null) {
                mConnectedThread.cancel();
                mConnectedThread = null;
            }
            mConnectThread = new ConnectThread(device);
            mConnectThread.start();
            setState(STATE_CONNECTING);
        }

        //Change the bluetooth state (connecting, connected, etc.)
        private void setState(int state) {
            BluetoothService.mState = state;
            if (handler != null) {
                //mHandler.obtainMessage(AbstractActivity.MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
                sendMsg(state);
            }
        }

        //Stop this service
        public synchronized void stop() {
            setState(STATE_NONE);
            if (mConnectThread != null) {
                mConnectThread.cancel();
                mConnectThread = null;
            }

            if (mConnectedThread != null) {
                mConnectedThread.cancel();
                mConnectedThread = null;
            }
            if (mBluetoothAdapter != null) {
                mBluetoothAdapter.cancelDiscovery();
            }
            stopSelf();
        }

        //Stop this service, auto called when service stops
        @Override
        public boolean stopService(Intent name) {
            setState(STATE_NONE);
            if (mConnectThread != null) {
                mConnectThread.cancel();
                mConnectThread = null;
            }

            if (mConnectedThread != null) {
                mConnectedThread.cancel();
                mConnectedThread = null;
            }
            mBluetoothAdapter.cancelDiscovery();
            return super.stopService(name);
        }

        //If connecting to bluetooth fails
        private void connectionFailed() {
            BluetoothService.this.stop();
            Message msg = new Message();
            msg.what = 1;
            Bundle bundle = new Bundle();
            bundle.putString("AbstractActivity.TOAST", "getString(R.string.error_connect_failed)");
            msg.setData(bundle);
            mHandler.sendMessage(msg);
        }

        //If connection is lost to the bluetooth device
        private void connectionLost() {
            BluetoothService.this.stop();
            Message msg = new Message();
            msg.what = 1;
            Bundle bundle = new Bundle();
            bundle.putString("AbstractActivity.TOAST", "getString(R.string.error_connect_lost)");
            msg.setData(bundle);
            mHandler.sendMessage(msg);
        }

        private static Object obj = new Object();

        //Send a message to the bluetooth device, unused as only utilizing one way communication
        public static void write(byte[] out) {
            // Create temporary object
            ConnectedThread r;
            // Synchronize a copy of the ConnectedThread
            synchronized (obj) {
                if (mState != STATE_CONNECTED)
                    return;
                r = mConnectedThread;
            }
            // Perform the write unsynchronized
            r.write(out);
        }

        //Bluetooth has sucessfully connected, end connecting thread and start the connected thread to listen for data
        private synchronized void connected(BluetoothSocket mmSocket, BluetoothDevice mmDevice) {
            // Cancel the thread that completed the connection
            if (mConnectThread != null) {
                mConnectThread.cancel();
                mConnectThread = null;
            }

            // Cancel any thread currently running a connection
            if (mConnectedThread != null) {
                mConnectedThread.cancel();
                mConnectedThread = null;
            }

            mConnectedThread = new ConnectedThread(mmSocket);
            mConnectedThread.start();

            // Message msg =
            // mHandler.obtainMessage(AbstractActivity.MESSAGE_DEVICE_NAME);
            // Bundle bundle = new Bundle();
            // bundle.putString(AbstractActivity.DEVICE_NAME, "p25");
            // msg.setData(bundle);
            // mHandler.sendMessage(msg);
            setState(STATE_CONNECTED);

        }

        //Thread used to connect to bluetooth
        private class ConnectThread extends Thread {
            private final BluetoothSocket mmSocket;
            private final BluetoothDevice mmDevice;

            public ConnectThread(BluetoothDevice device) {
                this.mmDevice = device;
                BluetoothSocket tmp = null;
                try {
                    tmp = device.createRfcommSocketToServiceRecord(UUID.fromString(SPP_UUID));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mmSocket = tmp;
            }

            @Override
            public void run() {
                setName("ConnectThread");
                mBluetoothAdapter.cancelDiscovery();
                try {
                    mmSocket.connect();
                } catch (IOException e) {
                    try {
                        mmSocket.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    connectionFailed();
                    return;

                }
                synchronized (BluetoothService.this) {
                    mConnectThread = null;
                }
                connected(mmSocket, mmDevice);
            }

            public void cancel() {
                try {
                    mmSocket.close();
                } catch (IOException e) {
                    Log.e("BluetoothService", "close() of connect socket failed", e);
                }
            }
        }

        //Thread used once bluetooth has been connected, reads bluetooth packets
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
                } catch (IOException e) {
                    Log.e("BluetoothService", "temp sockets not created", e);
                }
                mmInStream = tmpIn;
                mmOutStream = tmpOut;
            }


            //Section that reads incoming data from bluetooth sensor
            @Override
            public void run() {
                byte[] buffer = new byte[256];  // buffer store for the stream
                int bytes; // bytes returned from read()
                int readBufferPosition = 0;
                final byte delimiter = 10; //This is the ASCII code for a newline character
                try {
                    mmInStream.read();
                }
                catch (Exception e) {}
                while (true) {
                    try {
                        if (mmInStream != null) {
                            mState = STATE_LISTEN;
                            // Read from the InputStream

                            int bytesAvailable = mmInStream.available();
                            //Checks if there is a message and sends the message to the relevant activity handler to store the data
                            if (bytesAvailable > 0) {
                                Thread.sleep(10);
                                bytesAvailable = mmInStream.available();
                                byte[] packetBytes = new byte[bytesAvailable];
                                mmInStream.read(packetBytes);
                                String string=new String(packetBytes,"UTF-8");
                                int temp = Integer.parseInt(string.substring(0,2));
                                string = string.substring(2,string.length()-2);
                                int data = Integer.parseInt(string);
                                mHandler.obtainMessage(2, data, temp, buffer).sendToTarget();
                                Thread.sleep(900);
                            }
                        }
                        else {
                            connectionLost();
                            break;
                        }
                        // mHandler.obtainMessage(AbstractActivity.MESSAGE_READ,
                        // bytes, -1, buffer).sendToTarget();
                    } catch (Exception e) {
                        e.printStackTrace();
                        //connectionLost();
                        //BluetoothService.this.stop();
                        //break;
                        continue;
                    }

                }
            }

            private byte[] btBuff;

            //Sends to bluetooth device, unused as utilizing one way communication
            public void write(byte[] buffer) {
                try {
                    mmOutStream.write(buffer);

                    // Share the sent message back to the UI Activity
                    //mHandler.obtainMessage(AbstractActivity.MESSAGE_WRITE, buffer.length, -1, buffer).sendToTarget();
                } catch (IOException e) {
                    Log.e("BluetoothService", "Exception during write", e);
                }
            }

            public void cancel() {
                try {
                    mmSocket.close();

                } catch (IOException e) {
                    Log.e("BluetoothService", "close() of connect socket failed", e);
                }
            }

        }

        public void trace(String msg) {
            Log.d("AbstractActivity", msg);
            toast(msg);
        }

        public void toast(String msg) {
            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onDestroy() {
            stop();
            Log.d("BluetoothService", "Destroyed");
            super.onDestroy();
        }

        //Sends a message to the handler shown below
        private void sendMsg(int flag) {
            Message msg = new Message();
            msg.what = flag;
            handler.sendMessage(msg);
        }

        //Handler that above method calls
        private Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (!Thread.currentThread().isInterrupted()) {
                    switch (msg.what) {
                        case 2:
                            toast("Connecting...");
                            break;

                        case 3:
                            toast("Connected");
                            break;

                        case 4:

                            break;
                        case 5:
                            break;

                        case -1:
                            break;
                    }
                }
                super.handleMessage(msg);
            }

        };
    }