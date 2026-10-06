package org.sherlock201.spble;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.util.Log;

public class MyGattCallback extends BluetoothGattCallback {
    private static final String TAG = "MyGattCallback";

    public interface BleListener {
        void onConnected();
        void onDisconnected();
        void onServicesDiscovered(BluetoothGatt gatt, int status);
        void onError(String message);
    }

    private BleListener listener;

    public MyGattCallback(BleListener listener) {
        this.listener = listener;
    }

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        if (newState == 2) { // STATE_CONNECTED
            Log.d(TAG, "Connected to GATT server, discovering services...");
            gatt.discoverServices();
            if (listener != null) listener.onConnected();
        } else if (newState == 0) { // STATE_DISCONNECTED
            Log.d(TAG, "Disconnected from GATT server");
            if (listener != null) listener.onDisconnected();
        }
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        if (listener != null) {
            listener.onServicesDiscovered(gatt, status);
        }
    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        Log.d(TAG, "Characteristic write status: " + status);
    }
}
