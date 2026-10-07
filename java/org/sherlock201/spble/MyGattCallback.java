package org.sherlock201.spble;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Handler;
import android.os.Looper;
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
    private final Handler handler = new Handler(Looper.getMainLooper());

    public MyGattCallback(BleListener listener) {
        this.listener = listener;
    }

    @Override
    public void onConnectionStateChange(final BluetoothGatt gatt, int status, int newState) {
        if (newState == 2) { // STATE_CONNECTED
            Log.d(TAG, "Connected to GATT server, will discover services in 400ms...");

            // Сообщаем Python, что канал поднят (для UI)
            if (listener != null) listener.onConnected();

            // Даём стеку Android время зарегистрировать connId
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "Now calling discoverServices()");
                    boolean ok = gatt.discoverServices();
                    Log.d(TAG, "discoverServices() returned: " + ok);
                    if (!ok && listener != null) {
                        listener.onError("discoverServices() failed to start");
                    }
                }
            }, 400);

        } else if (newState == 0) { // STATE_DISCONNECTED
            Log.d(TAG, "Disconnected from GATT server");
            handler.removeCallbacksAndMessages(null); // отменяем отложенный discover
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
