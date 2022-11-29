package com.example.pt_controller;

import com.harrysoft.androidbluetoothserial.BluetoothManager;
import com.harrysoft.androidbluetoothserial.SimpleBluetoothDeviceInterface;

public class Globals {


    // call first
    public static Globals instance = new Globals();
    // assign second
    public static Globals getInstance(){
        return instance;
    }

    public static void setInstance(Globals instance){
        Globals.instance = instance;
    }

    private BluetoothManager bluetoothManager = BluetoothManager.getInstance();
    private SimpleBluetoothDeviceInterface deviceInterface;

    private Globals(){}

    public SimpleBluetoothDeviceInterface getDeviceInterface(){
        return deviceInterface;
    }

    public void setDeviceInterface(SimpleBluetoothDeviceInterface deviceInterface){
        this.deviceInterface = deviceInterface;
    }

    public BluetoothManager getBluetoothManager(){
        return bluetoothManager;
    }

    public void setBluetoothManager(BluetoothManager bluetoothManager){
        this.bluetoothManager= bluetoothManager;
    }
}
