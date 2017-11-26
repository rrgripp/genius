package unicamp.ruiter.genius;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice mDevice;
    private BluetoothSocket mSocket;

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
                    JogoFragment jogoFragment = JogoFragment.newInstance();
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, jogoFragment)
                            .commit();
                    return true;
                case R.id.navigation_high_score:
                    HighScoreFragment highScoreFragment = HighScoreFragment.newInstance();
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, highScoreFragment)
                            .commit();
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

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    public void setDevice(BluetoothDevice device) {
        mDevice = device;
    }

    public BluetoothAdapter initBluetooth() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
        }

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, Constants.REQUEST_ENABLE_BT);
        }

        return mBluetoothAdapter;
    }

    public void sendBluetooothSerial(String identifier) {

    }

    public void connectThread() {
        if (mDevice == null) {

        }
        try {
            // MY_UUID is the app's UUID string, also used by the server code
            mSocket = mDevice.createRfcommSocketToServiceRecord(Constants.GENIUS_UUID);
        } catch (IOException e) { }
    }
}
