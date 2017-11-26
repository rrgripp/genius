package unicamp.ruiter.genius;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AjustesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AjustesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AjustesFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private BluetoothAdapter mBluetoothAdapter;
    private ListView mDevicesList;
    private ArrayAdapter<String> mArrayAdapter;
    private BluetoothDevice mDevice;

    public AjustesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AjustesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AjustesFragment newInstance() {
        AjustesFragment fragment = new AjustesFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ajustes, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
        }

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, Constants.REQUEST_ENABLE_BT);
        }

        mArrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1);
        mDevicesList = (ListView) getView().findViewById(R.id.devices_list);
        mDevicesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String value = (String)adapterView.getItemAtPosition(i);
                String[] deviceInfo = value.split("\n");
                mDevice = mBluetoothAdapter.getRemoteDevice(deviceInfo[1]);
                ((MainActivity) getActivity()).setDevice(mDevice);
                Toast.makeText(getActivity(), "Conexão estabelecida com: " + deviceInfo[0], Toast.LENGTH_SHORT).show();
            }
        });

        updateDeviceList();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.REQUEST_ENABLE_BT) {
            updateDeviceList();
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public void updateDeviceList() {
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        // If there are paired devices
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                // Add the name and address to an array adapter to show in a ListView
                mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        }

        mDevicesList.setAdapter(mArrayAdapter);
    }

    public BluetoothSocket connectToDevice() {
        BluetoothSocket tmp;

        // Get a BluetoothSocket for a connection with the
        // given BluetoothDevice
        try {
            tmp = mDevice.createRfcommSocketToServiceRecord(Constants.GENIUS_UUID);
//            Method m = mDevice.getClass().getMethod("createRfcommSocket", new Class[] {int.class});
//            tmp = (BluetoothSocket) m.invoke(mDevice, 1);
            return tmp;
        } catch (IOException e) {
            Log.e(Constants.TAG, "create() failed", e);
        }

        return null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}