package unicamp.ruiter.genius;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView mNavigationView;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice mDevice;
    private BluetoothSocket mSocket;
    private InputStream mInStream;
    private OutputStream mOutStream;
    private OnReceiveInputStream mListener;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    HomeFragment homeFragment = HomeFragment.newInstance();
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, homeFragment)
                            .commit();
                    return true;
                case R.id.navigation_jogar:
                    sendBluetooothSerial("P");
                    JogoFragment jogoFragment = JogoFragment.newInstance();
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, jogoFragment)
                            .commit();
                    return true;
                case R.id.navigation_high_score:
                    sendBluetooothSerial("S");
                    HighScoreFragment highScoreFragment = HighScoreFragment.newInstance();
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, highScoreFragment)
                            .commit();
                    mListener = highScoreFragment;
                    return true;
                case R.id.navigation_ajustes:
                    AjustesFragment ajustesFragment = AjustesFragment.newInstance();
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, ajustesFragment)
                            .commit();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        mNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        mNavigationView.setSelectedItemId(R.id.navigation_home);


        if (isSocketConnected()) {
            closeSocket();
        }
        connectThread();
    }

    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
    }

    public void setDevice(BluetoothDevice device) {
        mDevice = device;
        if (isSocketConnected()) {
            closeSocket();
        }
        connectThread();
    }

    public BluetoothAdapter initBluetooth(Fragment fragment) {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
        }

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            fragment.startActivityForResult(enableBtIntent, Constants.REQUEST_ENABLE_BT);
        }

        return mBluetoothAdapter;
    }

    public void sendBluetooothSerial(String text) {
            if (isSocketConnected()) {
            write(text.getBytes(Charset.forName("UTF-8")));
        }
    }

    private boolean isSocketConnected() {
        return mSocket != null && mSocket.isConnected();
    }

    public void connectThread() {
        if (mDevice == null) {
            Toast.makeText(this, "É preciso conectar ao dispositivo Bluetooth primeiro em Ajustes", Toast.LENGTH_SHORT).show();

            return;
        }

        try {
            mSocket = mDevice.createRfcommSocketToServiceRecord(Constants.GENIUS_UUID);
            connectSocket();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Não foi possível conectar ao dispositivo", Toast.LENGTH_SHORT).show();
        } catch (NullPointerException e) {
            e.printStackTrace();
            Toast.makeText(this, "É preciso conectar ao dispositivo Bluetooth primeiro em Ajustes", Toast.LENGTH_SHORT).show();
        }
    }

    public void connectSocket() {
        try {
            // Connect the device through the socket. This will block
            // until it succeeds or throws an exception
            mSocket.connect();

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                mInStream = mSocket.getInputStream();
                mOutStream = mSocket.getOutputStream();
            } catch (IOException stremException) { }

            Toast.makeText(this, "Conectado com o dispositivo", Toast.LENGTH_SHORT).show();

        } catch (IOException connectException) {
            connectException.printStackTrace();
            Toast.makeText(this, "Não foi possível iniciar a conexao com o dispositivo", Toast.LENGTH_SHORT).show();
            // Unable to connect; close the socket and get out
            closeSocket();
            return;
        } catch (NullPointerException e) {
            e.printStackTrace();
            Toast.makeText(this, "É preciso conectar ao dispositivo Bluetooth primeiro em Ajustes", Toast.LENGTH_SHORT).show();
        }
    }

    public void closeSocket() {
        try {
            mSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Não foi possível fechar a conexao com o dispositivo", Toast.LENGTH_SHORT).show();
        }
    }

    public String listen() {
        byte[] buffer = new byte[1024];  // buffer store for the stream
        byte[] finalBuffer = new byte[1024];
        int bufferPointer = 0;
        int bytes; // bytes returned from read()

        // Keep listening to the InputStream until an exception occurs
        while (true) {
            try {
                // Read from the InputStream
                bytes = mInStream.read(buffer);
                // Send the obtained bytes to the UI activity
                for (int i = 0; i < bytes; i++) {
                    finalBuffer[bufferPointer + i] = buffer[i];
                }
                bufferPointer += bytes;

                if (new String(buffer).contains("%")) {
                    return new String(finalBuffer).substring(0, bufferPointer);
                }
            } catch (IOException e) {
                e.printStackTrace();
                break;
            } catch (NullPointerException e) {
                e.printStackTrace();
                break;
            }
        }

        return null;
    }

    public String listenJogo() {
        byte[] buffer = new byte[1024];  // buffer store for the stream
        int bytes; // bytes returned from read()

        // Keep listening to the InputStream until an exception occurs
        while (true) {
            try {
                // Read from the InputStream
                bytes = mInStream.read(buffer);
                // Send the obtained bytes to the UI activity
                return new String(buffer).substring(0, 1);
            } catch (IOException e) {
                e.printStackTrace();
                break;
            } catch (NullPointerException e) {
                e.printStackTrace();
                break;
            }
        }

        return null;
    }

    /* Call this from the main activity to send data to the remote device */
    public void write(byte[] bytes) {
        try {
            mOutStream.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Não foi possível enviar mensagem ao dispositivo", Toast.LENGTH_SHORT).show();
        } catch (NullPointerException e) {
            e.printStackTrace();
            Toast.makeText(this, "É preciso conectar ao dispositivo Bluetooth primeiro em Ajustes", Toast.LENGTH_SHORT).show();
        }
    }

    public void selectHomeView() {
        mNavigationView.setSelectedItemId(R.id.navigation_home);
    }
    public void selectScoreView() {
        mNavigationView.setSelectedItemId(R.id.navigation_high_score);
    }

    public interface OnReceiveInputStream {
        void inputReceived(String bytes);
    }
}
