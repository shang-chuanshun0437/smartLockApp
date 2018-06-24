package mutong.com.mtaj.ble;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothProfile;

public class ConnectCallback extends BluetoothGattCallback
{
    //连接状态改变的回调
    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState)
    {
        if (newState == BluetoothProfile.STATE_CONNECTED)
        {
            // 连接成功后启动服务发现
            System.out.println("连接成功");
            //Log.e("AAAAAAAA", "启动服务发现:" + mBluetoothGatt.discoverServices());
        }
    }
    //发现服务的回调
    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status)
    {
        if (status == BluetoothGatt.GATT_SUCCESS)
        {
            System.out.println("成功发现服务");
        }
        else
        {
            System.out.println("发现服务" + status);
        }
    };

    //写操作的回调
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status)
    {
        if (status == BluetoothGatt.GATT_SUCCESS)
        {
            System.out.println("写入成功" + status);
        }
    };

    //读操作的回调
    public void onCharacteristicRead(BluetoothGatt gatt,BluetoothGattCharacteristic characteristic, int status)
    {
        if (status == BluetoothGatt.GATT_SUCCESS)
        {
            System.out.println("读取成功" + characteristic.getValue());
        }
    }

    //数据返回的回调（此处接收BLE设备返回数据）
    public void onCharacteristicChanged(BluetoothGatt gatt,BluetoothGattCharacteristic characteristic)
    {

    }
}
