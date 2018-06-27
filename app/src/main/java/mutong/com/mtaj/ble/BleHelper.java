package mutong.com.mtaj.ble;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import java.util.List;

import mutong.com.mtaj.ble.util.BleConstant;
import mutong.com.mtaj.ble.util.BleParseMsgThread;
import mutong.com.mtaj.ble.util.BleSendCmdThread;

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
    private Handler mHandler;
    
    /**	The MAC address of the remote device's BLE	*/
    private String bluetoothMac;
    /**	The Connection State	*/
    private ConnectionState mConnectionState = ConnectionState.STATE_NONE;


    /**
     * Return true if the connection is currently connected.
     * 
     * @return true if the connection is connected, Or return false.
     */
    public boolean getCurrentConnectionState()
    {
    	return (mConnectionState == ConnectionState.STATE_CONNECTED);
    }
    
	public class LocalBinder extends Binder
    {
    	/**
    	 * Get BleHelper Service instance
    	 * @param handler The Handler extends from BleHandler
    	 * @return BleHelper Service instance
    	 * @see BleHelper
    	 */
    	public BleHelper getService(Handler handler)
        {
    		// Use this check to determine whether BLE is supported on the device. Then
    		// you can selectively disable BLE-related features.
            if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE))
            {
                Message msg = new Message();
                msg.what = BleConstant.HM_BLE_NONSUPPORT;
            	handler.sendMessage(msg);
            }
            
            if (null == handler)
            {
            	return null;
            }
            mHandler = handler;
            
            return BleHelper.this;
        }
    }

    private final IBinder mBinder = new LocalBinder();

    /**
     * Initializes a reference to the local BluetoothAdapter.
     *
     * @return Return true if the initialization is successful.
     */
    public boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }
        
        // Using the ACTION_REQUEST_ENABLE Intent, which will raise a dialog
		// that requests user permission to turn on BluetoothAdapter
        if (!mBluetoothAdapter.isEnabled())
        {
        	if (D)	Log.i(TAG, "Ensures Bluetooth is enabled on the device");
        	Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        	enableBtIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        	startActivity(enableBtIntent);
        }
        
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
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        // requestDisconnect(true);
        
        mBluetoothGatt.disconnect();
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent)
    {
        // After using a given device, you should make sure that BluetoothGatt.close() is called
        // such that resources are cleaned up properly.  In this particular example, close() is
        // invoked when the UI is disconnected from the Service.
        close();
        return super.onUnbind(intent);
    }
    
    /**
     * Connects to the GATT server hosted on the BLE device.
     *
     * @param address The device address of the destination device.
     *
     * @return Return true if the connection is initiated successfully. The connection result
     *         is reported asynchronously through the
     *         {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     *         callback.
     */
	private boolean connect(final String address)
    {
        // Previously connected device.  Try to reconnect.
        if (bluetoothMac != null && address.equals(bluetoothMac) && mBluetoothGatt != null)
        {
        	if (D)	Log.i(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            if (mBluetoothGatt.connect())
            {
                mConnectionState = ConnectionState.STATE_CONNECTING;
                return true;
            }
            else
            {
                return false;
            }
        }
        
        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null)
        {
            System.out.println("未找到设备：" + address);
            return false;
        }

        mBluetoothGatt = device.connectGatt(this, true, mGattCallback);
        bluetoothMac = address;
        mConnectionState = ConnectionState.STATE_CONNECTING;
        return true;
    }
    
    /**
     * After using a given BLE device, the APP must call this method to ensure resources are
     * released properly.
     */
    private void close()
    {
        if (mBluetoothGatt == null)
        {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
        // We're shutting the bluetooth down.
        if (mBluetoothAdapter.isEnabled())
        {
        	mBluetoothAdapter.disable();
        }
        // We're cancelling the running thread for parsing message
        if (mParseMessageThread != null)
        {
        	mParseMessageThread.cancel();
        	mParseMessageThread = null;
        }
        // We're cancelling the running thread for sending command
        if (mSendCommandThread != null)
        {
        	mSendCommandThread.cancel();
        	mSendCommandThread = null;
        }
        
    }

    /**
     * Request a read on a given {@code BluetoothGattCharacteristic}. The read result is reported
     * asynchronously through the {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
     * callback.
     * 
     * @param characteristic The characteristic to read from.
     */
    private void readCharacteristic(BluetoothGattCharacteristic characteristic)
    {
        if (characteristic == null)
        {
        	characteristic = mReadCharacteristic;
        }
        
        mBluetoothGatt.readCharacteristic(characteristic);
    }

    /**
     * Enables or disables notification on a give characteristic.
     *
     * @param characteristic Characteristic to act on.
     * @param enabled If true, enable notification.  False otherwise.
     */
    private void setCharacteristicNotification(BluetoothGattCharacteristic characteristic, boolean enabled)
    {
        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
    }

    /**
     * The messaging function
     * @param what The handler code defined in the Constants
     */
    private void broadcastUpdate(final int what)
    {
    	Message msg = new Message();
    	msg.what = what;
    	mHandler.sendMessage(msg);
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
    	mHandler.sendMessage(msg);
    }
    
    /**
     * Implements callback methods for GATT events that the APP cares about.  For example,
     * connection change, services discovered, characteristic change and characteristic write.
     * This is the most important function in the Helper.
     * 
     */
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback()
    {
        /**
         * The callback for the connect attempt, And attempts to discover services if successful connection.
         */
    	@Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState)
        {
            if (newState == BluetoothProfile.STATE_CONNECTED)
            {
                mConnectionState = ConnectionState.STATE_CONNECTED;
                Log.i(TAG, "Connected to GATT server.");
                // Attempts to discover services after successful connection.
                mBluetoothGatt.discoverServices();

                broadcastUpdate(BleConstant.HM_BLE_CONNECTED);

            }
            else if (newState == BluetoothProfile.STATE_DISCONNECTED)
            {
            	if (mParseMessageThread != null)
            	{
            		mParseMessageThread.cancel();
            		mParseMessageThread = null;
            	}
            	
            	if (mSendCommandThread != null)
            	{
            		mSendCommandThread.cancel();
            		mSendCommandThread = null;
            	}
            	mReadCharacteristic = null;
            	mWriteCharacteristic = null;
                mConnectionState = ConnectionState.STATE_NONE;
                broadcastUpdate(BleConstant.HM_BLE_DISCONNECTED);
            }
        }

        /**
         * In order to get the write characteristic and the read characteristic, We traverse
         * the service list.
         */
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status)
        {
            if (status == BluetoothGatt.GATT_SUCCESS)
            {
                // Get service successful
                List<BluetoothGattService> listGattService = mBluetoothGatt.getServices();
                // The temporary characteristic
                BluetoothGattCharacteristic characteristic;
                // Traverse the service list for the characteristic with the UUID_WRITE
                for (BluetoothGattService gattService : listGattService)
                {
                	if (null == mWriteCharacteristic)
                	{
                    	characteristic = gattService.getCharacteristic(BleConstant.UUID_WRITE);
                    	if (characteristic != null)
                    	{
                            System.out.println("写特征的UUID已找到:" + BleConstant.UUID_WRITE);
                    		mWriteCharacteristic = characteristic;
                    	}
                	}

                	if (null == mReadCharacteristic)
                	{
                     	characteristic = gattService.getCharacteristic(BleConstant.UUID_READ);
                    	if (characteristic != null)
                    	{
                    		System.out.println("特征值写已找到："+BleConstant.UUID_READ.toString());
                                
                    		final int charaProp = characteristic.getProperties();
                            if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0)
                            {
                                // If there is an active notification on a characteristic, clear
                                // it first so it doesn't update the data field on the user interface.
                                if (mReadCharacteristic != null)
                                {
                                    setCharacteristicNotification(mReadCharacteristic, false);
                                    mReadCharacteristic = null;
                                }
                                readCharacteristic(characteristic);
                            }
                            if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0)
                            {
                                mReadCharacteristic = characteristic;
                                setCharacteristicNotification(characteristic, true);
                            }
                    	}
                	}
                	
                	if ((null != mWriteCharacteristic) && (null != mReadCharacteristic))
                	{
            	    	if (null == mSendCommandThread)
            	    	{
            	    		mSendCommandThread = new BleSendCmdThread(mBluetoothGatt, mWriteCharacteristic, mHandler);
            	    		mSendCommandThread.start();
            	    	}
            	    	
                    	if (null == mParseMessageThread)
                    	{
                    		mParseMessageThread = new BleParseMsgThread(mHandler);
                    		mParseMessageThread.start();
                    	}
                		broadcastUpdate(BleConstant.HM_BLE_READY);
                		if (D)
                		{
                			broadcastUpdate(BleConstant.HM_DEBUG_TOAST, "I'm ready!");
                		}
                		break;
                	}
                }
                
                if ((mWriteCharacteristic == null) || (mReadCharacteristic == null))
                {
            		if (D)
            		{
            			Log.e(TAG, "No characteristic with the given UUID was found" +
            					BleConstant.UUID_WRITE.toString());
            			broadcastUpdate(BleConstant.HM_DEBUG_TOAST, "Characteristic fails");
            		}
            		broadcastUpdate(BleConstant.HM_BLE_READY_FAIL);
            		if (D)
            		{
            			broadcastUpdate(BleConstant.HM_DEBUG_TOAST, "Initialization Failure!");
            		}
            	}
                
            }
            else
            {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }
        
		@Override
		public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status)
        {
			if (status == BluetoothGatt.GATT_SUCCESS)
			{
				broadcastUpdate(BleConstant.HM_CMD_WRITED, "Write success");
			}
			else
            {
				broadcastUpdate(BleConstant.HM_CMD_FAILURE, "Write fails");
			}
		}

		@Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status)
        {
            if (status == BluetoothGatt.GATT_SUCCESS)
            {
            	final byte[] data = characteristic.getValue();
            	if (data != null && data.length > 0)
            	{
            		if (D)	broadcastUpdate(BleConstant.HM_DEBUG_MSG, data.toString());
            		// Create temporary object
            		BleParseMsgThread r;
                    // Synchronize a copy of the ConnectedThread
                    synchronized (this)
                    {
                        if (mConnectionState != ConnectionState.STATE_CONNECTED)
                        {
                        	return;
                        }
                        else
                        {
                        	r = mParseMessageThread;
                        }
                    }
                    // Perform the write unsynchronized
	            	r.addMessagePackage(data);
                }
            }
            else
            {
            	// broadcastUpdate(Constant.TEST_TOAST, "Read fails");
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic)
        {
            if (mReadCharacteristic.getUuid() == characteristic.getUuid())
            {
            	final byte[] data = characteristic.getValue();
            	if ((data != null) && (data.length > 0))
            	{
            		// Create temporary object
            		BleParseMsgThread r;
                    // Synchronize a copy of the ConnectedThread
                    synchronized (this)
                    {
                        if (mConnectionState != ConnectionState.STATE_CONNECTED)
                        {
                        	return;
                        }
                        else
                        {
                        	r = mParseMessageThread;
                        }
                    }
                    // Perform the write unsynchronized
	            	r.addMessagePackage(data);
                }
            }
        	
        }
    };
    
    /**
     * Writing the command into the command queue.
     * 该函数本来是私有成员函数,由别的对外接口函数调用.
     * @param command The command to wirte
     */
    public void sendCmdToBle(byte[] command)
    {
    	if (mWriteCharacteristic != null)
    	{
	    	if ((mSendCommandThread != null) && (command != null) && (command.length > 0))
	    	{
                mSendCommandThread.addCommandPackage(command);
                return;
	        }
    	}
    	System.out.println("The mWriteCharacteristic is null! Send Fail!");
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
	
}
