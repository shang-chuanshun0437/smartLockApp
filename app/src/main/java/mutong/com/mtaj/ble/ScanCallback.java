package mutong.com.mtaj.ble;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;

public class ScanCallback implements BluetoothAdapter.LeScanCallback
{
    private Context context;

    private BluetoothAdapter bluetoothAdapter;

    private Activity activity;

    public ScanCallback(Context context,BluetoothAdapter bluetoothAdapter)
    {
        this.context = context;
        this.bluetoothAdapter = bluetoothAdapter;
        this.activity = (Activity)context;
    }
    /**
     * @param bluetoothDevice：识别的远程设备
     * @param rssi：  RSSI的值作为对远程蓝牙设备的报告; 0代表没有蓝牙设备;
     * @param scanRecord：远程设备提供的配对号(公告)
     */
    @Override
    public void onLeScan(BluetoothDevice bluetoothDevice, int rssi, byte[] scanRecord)
    {
        System.out.println("onLeScan thread:" + Thread.currentThread().getName() +",mac:" + bluetoothDevice.getAddress() +
                ",name:" + bluetoothDevice.getName() + ",rssi:" + rssi);
    }
}
