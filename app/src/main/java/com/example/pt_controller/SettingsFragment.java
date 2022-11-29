package com.example.pt_controller;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.harrysoft.androidbluetoothserial.BluetoothManager;
import com.harrysoft.androidbluetoothserial.BluetoothSerialDevice;
import com.harrysoft.androidbluetoothserial.SimpleBluetoothDeviceInterface;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


public class SettingsFragment extends Fragment {

    public Button send,receive;
    public EditText tiempoMuestreo;

    public Context context;

    Globals sharedBT = Globals.getInstance();

    public boolean conectado;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        context = view.getContext();
        send = view.findViewById(R.id.send);
        receive = view.findViewById(R.id.receive);
        tiempoMuestreo = view.findViewById(R.id.tiempoMuestreo);

        // importing the device Interface from shareFragment


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    sendMessage(String.valueOf(tiempoMuestreo.getText()));
                } catch (Exception e){
                    Toast.makeText(context,"Error al enviar mensaje",Toast.LENGTH_LONG).show();
                }
            }
        });


        return view;
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
        Toast.makeText(context, getString(R.string.messageSent) + message, Toast.LENGTH_LONG).show(); // Replace context with your context instance.
    }

    private void onMessageReceived(String message) {
        // We received a message! Handle it here.
        Toast.makeText(context, getString(R.string.messageReceived) + message, Toast.LENGTH_LONG).show(); // Replace context with your context instance.
//        msgBox.setText(message);
    }

    public void sendMessage(String message){
        sharedBT.getDeviceInterface().sendMessage(message);
    }

    private void onError(Throwable error) {
        // Handle the error
//        status.setText("Error");
        Toast.makeText(context, getString(R.string.unspectedError),Toast.LENGTH_LONG).show();
    }

    private void disconnectDevices(){
        sharedBT.getBluetoothManager().close();
//        status.setText("Desconectado");

        sharedBT.setBluetoothManager(BluetoothManager.getInstance());
        Toast.makeText(context, getString(R.string.disconnectDevice),Toast.LENGTH_LONG).show();
        conectado=false;
    }


}