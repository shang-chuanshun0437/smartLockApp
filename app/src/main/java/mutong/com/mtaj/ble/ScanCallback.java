package mutong.com.mtaj.ble;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class ScanCallback implements BluetoothAdapter.LeScanCallback {
    private Context context;

    private Activity activity;

    private List<BluetoothDevice> bluetoothDevices = new ArrayList<BluetoothDevice>();

    public ScanCallback(Context context) {
        this.context = context;
        this.activity = (Activity) context;
    }

    /**
     * @param bluetoothDevice：识别的远程设备
     * @param rssi：                     RSSI的值作为对远程蓝牙设备的报告; 0代表没有蓝牙设备;
     * @param scanRecord：远程设备提供的配对号(公告)
     */
    @Override
    public void onLeScan(BluetoothDevice bluetoothDevice, int rssi, byte[] scanRecord) {
        System.out.println("onLeScan thread:" + Thread.currentThread().getName() + ",mac:" + bluetoothDevice.getAddress() +
                ",name:" + bluetoothDevice.getName() + ",rssi:" + rssi);
        if (bluetoothDevice.getName().equals("test")) {
            if ( !bluetoothDevices.contains(bluetoothDevice)) {
                bluetoothDevices.add(bluetoothDevice);
            }
        }
    }

    public List<BluetoothDevice> getBluetoothDevices() {
        return bluetoothDevices;
    }

    public void setBluetoothDevices(List<BluetoothDevice> bluetoothDevices) {
        this.bluetoothDevices = bluetoothDevices;
    }
}
