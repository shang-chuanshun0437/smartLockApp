package mutong.com.mtaj.ble;

import android.app.Activity;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.List;

import mutong.com.mtaj.ble.util.BleConstant;
import mutong.com.mtaj.ble.util.BleParseMsgThread;
import mutong.com.mtaj.ble.util.BleSendCmdThread;
import mutong.com.mtaj.common.Constant;

import static mutong.com.mtaj.ble.util.BleConstant.D;


/**
 * Service for managing connection and data communication with a GATT server hosted on a
 * given BLE device.
 * 
 * @author UESTC-PRMI-Burial
 * @date 2014-12-11
 * 
 */
public class BleHelper extends Service
{

    private final static String TAG = "BleHelper";

    /**
     * 自定义的打开 Bluetooth 的请求码，与 onActivityResult 中返回的 requestCode 匹配。
     */
    private static final int REQUEST_CODE_BLUETOOTH_ON = 1313;
    /**
     * High level manager used to obtain an instance of an BluetoothAdapter
     * and to conduct overall BluetoothAdapter Management
     */
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    
    /**	Generic Attribute Profile (GATT)	*/ 
    private BluetoothGatt mBluetoothGatt;

    /**	The special characteristic with UUID_WRITE for write operation	*/ 
    private BluetoothGattCharacteristic mWriteCharacteristic = null;

    /**	The special characteristic with UUID_READ for read operation	*/ 
    private BluetoothGattCharacteristic mReadCharacteristic = null;

	/** What the thread aims to get message, parse message etc is traced back to {@link Thread}	*/
	private BleParseMsgThread mParseMessageThread;

	/** What the thread aims to send command is traced back to {@link Thread}	*/
	private BleSendCmdThread mSendCommandThread;

    /**	What the Handler is traced to BleHandler is an input parameter when you get the instance of BleHelper	*/ 
    private Handler handler;

    /**	The Connection State	*/
    private ConnectionState mConnectionState = ConnectionState.STATE_NONE;

    //context
    private Context context;

    //the device mac you want to connect
    private String bluetoothMac;

    private List<BluetoothDevice> bluetoothDevices = new ArrayList<BluetoothDevice>();

    public BleHelper(Context context,Handler handler,String bluetoothMac)
    {
        this.context = context;
        this.handler = handler;
        this.bluetoothMac = bluetoothMac;
    }
    /**
     * Return true if the connection is currently connected.
     * 
     * @return true if the connection is connected, Or return false.
     */
    public boolean getCurrentConnectionState()
    {
    	return (mConnectionState == ConnectionState.STATE_CONNECTED);
    }

    /**
     * Initializes a reference to the local BluetoothAdapter.
     *
     * @return Return true if the initialization is successful.
     */
    public boolean initialize()
    {
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        if (mBluetoothManager == null)
        {
            mBluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null)
            {
                handler.sendEmptyMessage(BleConstant.HM_BLE_NONSUPPORT);
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();

        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled())
        {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            Activity activity = (Activity)context;

            activity.startActivityForResult(enableBtIntent,REQUEST_CODE_BLUETOOTH_ON);

            if(mBluetoothAdapter.isEnabled())
            {
                return true;
            }
            return false;
        }
        return true;
    }
    
    /**
     *
     * @param address The device address of the destination device.
     */
	public boolean connectByMac(final String address)
    {
        //Todo 测试
        //mBluetoothAdapter.startLeScan(scanCallback);
        
        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null)
        {
            System.out.println("未找到设备：" + address);
            broadcastUpdate(BleConstant.BLE_NOTFOUND,address);
            return false;
        }

        mBluetoothGatt = device.connectGatt(context, true, mGattCallback);
        System.out.println("######");
        mConnectionState = ConnectionState.STATE_CONNECTING;
        return true;
    }

    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public void disconnect()
    {
        if (mBluetoothAdapter == null || mBluetoothGatt == null)
        {
            return;
        }
        mBluetoothGatt.disconnect();
    }
    /**
     * After using a given BLE device, the APP must call this method to ensure resources are
     * released properly.
     */
    public void close()
    {
        if (mBluetoothGatt == null)
        {
            return;
        }
        disconnect();
        mBluetoothGatt.close();
        mBluetoothGatt = null;

        System.out.println("蓝牙被close了");
    }

    private void readCharacteristic(BluetoothGattCharacteristic characteristic)
    {
        if (characteristic != null)
        {
            Boolean flags = mBluetoothGatt.readCharacteristic(characteristic);
            System.out.println("flags:" + flags + "," + Thread.currentThread().getName());
        }
    }

    private void setCharacteristicNotification(BluetoothGattCharacteristic characteristic, boolean enabled)
    {
        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
        List<BluetoothGattDescriptor> descriptors = characteristic.getDescriptors();
        for(BluetoothGattDescriptor dp:descriptors)
        {
            dp.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            mBluetoothGatt.writeDescriptor(dp);
        }
    }

    /**
     * The messaging function
     * @param what The handler code defined in the Constants
     */
    private void broadcastUpdate(final int what)
    {
    	handler.sendEmptyMessage(what);
    }
    
    /**
     * The messaging function (Overload)
     * @param what	 The handler code defined in the Constants
     * @param stringMsg	The message received form BLE Or the Tip message
     * 
     */
    private void broadcastUpdate(final int what, final String stringMsg)
    {
    	Message msg = new Message();

    	msg.what = what;
    	msg.obj = stringMsg;

    	handler.sendMessage(msg);
    }

    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback()
    {
    	@Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState)
        {
            if (status == BluetoothGatt.GATT_SUCCESS)
            {
                if (newState == BluetoothProfile.STATE_CONNECTED)
                {
                    mConnectionState = ConnectionState.STATE_CONNECTED;
                    System.out.println("已连接蓝牙设备");
                    // Attempts to discover services after successful connection.
                    mBluetoothGatt.discoverServices();

                    broadcastUpdate(BleConstant.HM_BLE_CONNECTED);
                }
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status)
        {
            if (status == BluetoothGatt.GATT_SUCCESS)
            {
                try
                {
                    // Get service successful
                    BluetoothGattService gattService = mBluetoothGatt.getService(BleConstant.UUID_SERVICE);
                    // The temporary characteristic
                    BluetoothGattCharacteristic characteristic = null;

                    characteristic = gattService.getCharacteristic(BleConstant.UUID_WRITE);

                    if (characteristic != null)
                    {
                        System.out.println("写特征的UUID已找到:" + BleConstant.UUID_WRITE);
                        broadcastUpdate(BleConstant.BLE_WRITE_FOUND);
                        mWriteCharacteristic = characteristic;
                        setCharacteristicNotification(mWriteCharacteristic, true);

                        sendCmdToBle(new byte[]{65, 66});
                    }
                    else
                    {
                        System.out.println("写特征值没找到" + BleConstant.UUID_WRITE.toString());
                        broadcastUpdate(BleConstant.BLE_WRITE_NOTFOUND);
                    }

                    characteristic = gattService.getCharacteristic(BleConstant.UUID_READ);

                    if (characteristic != null) {
                        System.out.println("特征值读已找到：" + BleConstant.UUID_READ.toString());
                        broadcastUpdate(BleConstant.BLE_READ_FOUND);

                        setCharacteristicNotification(characteristic, true);
                        mReadCharacteristic = characteristic;

                    }
                    else
                    {
                        System.out.println("特征值读没找到：" + BleConstant.UUID_READ.toString());
                        broadcastUpdate(BleConstant.BLE_READ_NOTFOUND);
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
        
		@Override
		public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status)
        {
			if (status == BluetoothGatt.GATT_SUCCESS)
			{
				broadcastUpdate(BleConstant.BLE_WRITE_SUCCESS, characteristic.getStringValue(0));
				System.out.println("ble写入成功的回调:" + Thread.currentThread().getName());
                readCharacteristic(characteristic);
			}
			else
            {
				broadcastUpdate(BleConstant.BLE_WRITE_FAIL, "Write fails");
                System.out.println("ble写入失败的回调" + status);
			}
		}

		@Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status)
        {
            if (status == BluetoothGatt.GATT_SUCCESS)
            {
                System.out.println("读取到的数据为：" + characteristic.getStringValue(0) + ",uuid:" + characteristic.getUuid());
            	broadcastUpdate(BleConstant.BLE_READ_SUCCESS,characteristic.getStringValue(0));
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic)
        {
            final byte[] data = characteristic.getValue();
            System.out.println("接收到通知:" + characteristic.getStringValue(0));
            broadcastUpdate(BleConstant.BLE_NOTIFY_SUCCESS,characteristic.getStringValue(0));
        }
    };
    
    /**
     * Writing the command into the command queue.
     * 该函数本来是私有成员函数,由别的对外接口函数调用.
     * @param command The command to wirte
     */
    private void sendCmdToBle(byte[] command)
    {
    	/*if (mWriteCharacteristic != null)
    	{
	    	if ((mSendCommandThread != null) && (command != null) && (command.length > 0))
	    	{
                mSendCommandThread.addCommandPackage(command);
                return;
	        }
    	}*/
    	boolean flags = false;
        mWriteCharacteristic.setValue(command);
        flags = mBluetoothGatt.writeCharacteristic(mWriteCharacteristic);

        System.out.println("写标志位：flags：" + flags + ",写入的数据:" + new String(command));

	}

    /**	The Connection State Enum	*/
    private enum ConnectionState
    {
    	/**	We're doing nothing	*/
	    STATE_NONE,
	    /**	Now initiating an outgoing connection	*/
	    STATE_CONNECTING,
	    /**	Now connected to a remote device	*/
	    STATE_CONNECTED,
	};

    public class LocalBinder extends Binder {
        BleHelper getService() {
            return BleHelper.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // After using a given device, you should make sure that BluetoothGatt.close() is called
        // such that resources are cleaned up properly.  In this particular example, close() is
        // invoked when the UI is disconnected from the Service.
        close();
        return super.onUnbind(intent);
    }

    private final IBinder mBinder = new LocalBinder();

    //todo 测试
    private BluetoothAdapter.LeScanCallback scanCallback = new BluetoothAdapter.LeScanCallback()
    {
        @Override
        public void onLeScan(BluetoothDevice bluetoothDevice, int rssi, byte[] scanRecord) {
            System.out.println("onLeScan thread:" + Thread.currentThread().getName() + ",mac:" + bluetoothDevice.getAddress() +
                    ",name:" + bluetoothDevice.getName() + ",rssi:" + rssi);
            if (bluetoothMac.equals(bluetoothDevice.getAddress())) {
                if ( !bluetoothDevices.contains(bluetoothDevice)) {
                    bluetoothDevices.add(bluetoothDevice);

                    mBluetoothGatt = bluetoothDevice.connectGatt(context,false,mGattCallback);

                    mBluetoothAdapter.stopLeScan(scanCallback);
                }
            }
        }
    };

}
