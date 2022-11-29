package com.example.pt_controller;

import android.bluetooth.BluetoothDevice;
import android.content.Context;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.harrysoft.androidbluetoothserial.BluetoothManager;
import com.harrysoft.androidbluetoothserial.BluetoothSerialDevice;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


public class ShareFragment extends Fragment {

    public View view;

    private Button listDevices,disconnect;
    private ListView deviceList;
    private Context context;


    // variables for the serial bt connection
    Globals sharedBT = Globals.getInstance();
    List<BluetoothDevice> list;
    private boolean conectado;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_share, container, false);
        listDevices = view.findViewById(R.id.listDevices);
        disconnect = view.findViewById(R.id.disconnect);
        deviceList = view.findViewById(R.id.deviceList);
        context = view.getContext();


        Collection<BluetoothDevice> pairedDevices = sharedBT.getBluetoothManager().getPairedDevicesList();
        list = new ArrayList<BluetoothDevice>();
        for (BluetoothDevice device : pairedDevices) {
            list.add(device);
        }
        deviceList.setAdapter(new ArrayAdapter<BluetoothDevice>(context, android.R.layout.simple_list_item_1,list));
        List<String> listName = new ArrayList<String>();
        for (BluetoothDevice device : pairedDevices) {
            listName.add(device.getName());
        }
        deviceList.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line,listName));


        deviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    // TODO: change the string to the final name of the encoder
                    if(list.get(i).getName().equals("ESP32Prueba")){
                        Toast.makeText(context,"Conectando al Encoder Optico...",Toast.LENGTH_LONG).show();
                        // TODO: change the state of the nav header
                    }
                    connectDevice(list.get(i).getAddress());

                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(context,"Error al conectar",Toast.LENGTH_LONG).show();
                }
            }
        });

        listDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateDeviceList(view);
            }
        });

        disconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disconnectDevices();
            }
        });

        this.view=view;
        return view;
    }



    private void updateDeviceList(View view){
        Collection<BluetoothDevice> pairedDevices = sharedBT.getBluetoothManager().getPairedDevicesList();
        List<BluetoothDevice> list = new ArrayList<BluetoothDevice>();
        for (BluetoothDevice device : pairedDevices) {
            list.add(device);
        }
        deviceList.setAdapter(new ArrayAdapter<BluetoothDevice>(context, android.R.layout.simple_list_item_1,list));

        List<String> listName = new ArrayList<String>();
        for (BluetoothDevice device : pairedDevices) {
            listName.add(device.getName());
        }
        deviceList.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line,listName));
    }

    private void connectDevice(String mac) {
        sharedBT.getBluetoothManager().openSerialDevice(mac)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onConnected, this::onError);
    }

    private void onConnected(BluetoothSerialDevice connectedDevice) {
        // You are now connected to this device!
        // Here you may want to retain an instance to your device:
        if(conectado){
            disconnectDevices();
        }
        sharedBT.setDeviceInterface(connectedDevice.toSimpleDeviceInterface());


        conectado = true;

        // Let's send a message:
        sharedBT.getDeviceInterface().sendMessage("Conexion Exitosa");

//        status.setText("Conectado a "+ connectedDevice.getMac());

        // Listen to bluetooth events
        sharedBT.getDeviceInterface().setListeners(this::onMessageReceived, this::onMessageSent, this::onError);

    }

    private void onMessageSent(String message) {
        // We sent a message! Handle it here.
        Toast.makeText(context, view.getContext().getString(R.string.messageSent) + message, Toast.LENGTH_LONG).show(); // Replace context with your context instance.
    }

    private void onMessageReceived(String message) {
        // We received a message! Handle it here.
        Toast.makeText(context, view.getContext().getString(R.string.messageReceived) + message, Toast.LENGTH_LONG).show(); // Replace context with your context instance.
//        msgBox.setText(message);
    }

    public void sendMessage(String message){
        sharedBT.getDeviceInterface().sendMessage(message);
    }

    private void onError(Throwable error) {
        // Handle the error
//        status.setText("Error");
        Toast.makeText(context, view.getContext().getString(R.string.unspectedError),Toast.LENGTH_LONG).show();
    }

    private void disconnectDevices(){
        sharedBT.getBluetoothManager().close();
//        status.setText("Desconectado");

        sharedBT.setBluetoothManager(BluetoothManager.getInstance());
        Toast.makeText(context, view.getContext().getString(R.string.disconnectDevice),Toast.LENGTH_LONG).show();
        conectado=false;
    }


}