package mutong.com.mtaj.ble.util;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import static mutong.com.mtaj.ble.util.BleConstant.D;

public class BleSendCmdThread extends Thread {
	
	private final static String TAG = "BleSendCmd";
	
    /**	Generic Attribute Profile (GATT)	*/ 
    private BluetoothGatt mBluetoothGatt;
    /**	The special characteristic with UUID_WRITE for write operation	*/ 
    private BluetoothGattCharacteristic mWriteCharacteristic;
	/** False if you want to cancel this thread.	*/
	private boolean mIsRun;
	/**	What the Handler is traced to BleHandler is an input parameter when you get the instance of BleParseMessageThread	*/ 
	private Handler mHandler;
	/** The inner lock */
	private Lock mInnerLock;
	/** The inner condition corresponding to the inner lock */
	private Condition mInnerCondition;
	
	/** 
	 * The queue for command.
	 */
	private BleUtil.BleQueue mQueue;
	
	public BleSendCmdThread(BluetoothGatt btGatt, BluetoothGattCharacteristic btChactacteristic, Handler handler)
	{
		// Initialization
		mBluetoothGatt = btGatt;
		mWriteCharacteristic = btChactacteristic;
		mHandler = handler;
		
		mQueue = new BleUtil.BleQueue(128);
		mIsRun = true;
		
		mInnerLock = new ReentrantLock();
		mInnerCondition = mInnerLock.newCondition();
		Log.i(TAG, "We are running the BleSendCmd thread.");
	}
	
	/**
	 * Writing the command into the command queue.
	 * @param command the command to write.
	 */
	public void addCommandPackage(byte[] command)
	{
		if (null == command)
		{
			if (D) Log.i(TAG, "The message is null!");
			return;
		}
		
		mQueue.addElement(command);
		
		mInnerLock.lock();
		mInnerCondition.signalAll();
		if (D)	Log.i(TAG, "InnerLock UnLock!");
		mInnerLock.unlock();
		
		if (D) Log.i(TAG, "Add the command!");
		
		return;
	}
	
	@Override
	public void run()
	{
		byte[] command = null;
		while (mIsRun)
		{
			command = getCommandFromBuffer();
			
			if (null != command)
			{
				sendCmdToBle(command);
			}
			else
			{
				mInnerLock.lock();
				try
				{
					System.out.println("We're waiting the command.");
					mInnerCondition.await();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				finally
				{
					mInnerLock.unlock();
				}
			}
		}
	}

	/**
	 * If you attempt to close the APP or when the connection has disconnected, you should
	 * cancel the thread through calling this function.
	 */
	public void cancel()
	{
		mInnerLock.lock();
		mInnerCondition.signalAll();	// UnLock
		mInnerLock.unlock();
		
		mIsRun = false;
		mQueue.destroy();
		System.out.println("We have stoped the BleSendCmd thread.");
	}
	
	/**
	 * Getting command from the {@link BleSendCmdThread#mQueue}.
	 * @return The command included in the buffer.
	 */
	private byte[] getCommandFromBuffer()
	{
		return mQueue.getAllBytes();
	}
	
    /**
     * The function is the only interface which we are allowed to send command to BLE.
     * Request a write on a given {@code BluetoothGattCharacteristic}. The write result is reported
     * asynchronously through the {@code BluetoothGattCallback#onCharacteristicWrite(BluetoothGatt, BluetoothGattCharacteristic, int)}
     * callback.
     * @param cmd The command to send.
     * 
     */
    private void sendCmdToBle(byte[] cmd)
	{
    	int length = cmd.length;
    	if (length <= 20)
    	{	// 每次最多写入20字节
    		mWriteCharacteristic.setValue(cmd);
            mBluetoothGatt.writeCharacteristic(mWriteCharacteristic);
    	}
    	else
		{
			// Every times send 20 bytes
    		byte[] subCmd = new byte[20];
    		int count = length / 20;
    		int remainder = length % 20;
    		for (int i = 0; i < count; ++i)
    		{
    			System.arraycopy(cmd, i * 20,
    					subCmd, 0, 20);
    			
    			mWriteCharacteristic.setValue(subCmd);
    			mBluetoothGatt.writeCharacteristic(mWriteCharacteristic);
    		}

    		byte[] remaindCmd = new byte[remainder];
    		System.arraycopy(cmd, count * 20,
    				remaindCmd, 0, remainder);
    		
			mWriteCharacteristic.setValue(remaindCmd);
			mBluetoothGatt.writeCharacteristic(mWriteCharacteristic);
    	}
    }
	
    /**
     * The messaging function
     * @param what	 The handler code defined in the Constants
     * @param command	The command readily to send
     * 
     */
    private void broadcastUpdate(final int what, Object command)
	{
    	Message msg = new Message();
    	msg.what = what;
    	msg.obj = command;
    	mHandler.sendMessage(msg);
        if (D)	Log.d(TAG, "" + what);
    }
	
}
