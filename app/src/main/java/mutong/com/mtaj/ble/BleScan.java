package mutong.com.mtaj.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class BleScan implements Callable<List<BluetoothDevice>>
{
    private BluetoothAdapter bluetoothAdapter;

    private List<BluetoothDevice> bluetoothDevices = new ArrayList<BluetoothDevice>();

    public BleScan(BluetoothAdapter bluetoothAdapter)
    {
        this.bluetoothAdapter = bluetoothAdapter;
    }

    @Override
    public List<BluetoothDevice> call() throws Exception
    {
        bluetoothDevices.clear();

        bluetoothAdapter.startLeScan(leScanCallback);

        Thread.sleep(30 * 1000);

        bluetoothAdapter.stopLeScan(leScanCallback);

        return bluetoothDevices;
    }

    private BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback()
    {
        @Override
        public void onLeScan(BluetoothDevice bluetoothDevice, int i, byte[] bytes)
        {
            if(!bluetoothDevices.contains(bluetoothDevice))
            {
                bluetoothDevices.add(bluetoothDevice);
            }
        }
    };
}
