package mutong.com.mtaj.ble.util;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import mutong.com.mtaj.utils.StringUtil;

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
	private BleQueue mQueue;
	
	public BleSendCmdThread(BluetoothGatt btGatt, BluetoothGattCharacteristic btChactacteristic, Handler handler)
	{
		// Initialization
		mBluetoothGatt = btGatt;
		mWriteCharacteristic = btChactacteristic;
		mHandler = handler;
		
		mQueue = BleQueue.getInstance();
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
			return;
		}
		
		mQueue.addSendElement(command);

		mInnerLock.lock();
		mInnerCondition.signalAll();	// UnLock
		mInnerLock.unlock();

	}
	
	@Override
	public void run()
	{
		String command = "";
		while (mIsRun)
		{
			command = getCommandFromBuffer();
			
			if (!StringUtil.isEmpty(command))
			{
				sendCmdToBle(command.getBytes());
			}
			else
			{
				mInnerLock.lock();
				try
				{
					System.out.println("进入BleSend线程，正在等待添加数据");
					mInnerCondition.await(1000, TimeUnit.MILLISECONDS);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				finally {
					mInnerLock.unlock();
					System.out.println("锁已释放");
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
	private String getCommandFromBuffer()
	{
		return mQueue.getSendElement();
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
		Boolean flags = false;
		try
		{
			//BluetoothGattService gattService = mBluetoothGatt.getService(BleConstant.UUID_WRITE);
			//BluetoothGattCharacteristic characteristic = gattService.getCharacteristic(BleConstant.UUID_WRITE);
			int length = cmd.length;
			if (length <= 20)
			{	// 每次最多写入20字节
				mWriteCharacteristic.setValue(cmd);
				flags = mBluetoothGatt.writeCharacteristic(mWriteCharacteristic);
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
					flags = mBluetoothGatt.writeCharacteristic(mWriteCharacteristic);
				}

				byte[] remaindCmd = new byte[remainder];
				System.arraycopy(cmd, count * 20,
						remaindCmd, 0, remainder);

				mWriteCharacteristic.setValue(remaindCmd);
				flags = mBluetoothGatt.writeCharacteristic(mWriteCharacteristic);
			}
			if(flags == false)
			{
				//mHandler.sendEmptyMessage(BleConstant.BLE_WRITE_FAIL);
			}
			else
			{
				mHandler.sendEmptyMessage(BleConstant.BLE_WRITE_SUCCESS);
			}
			System.out.println("写标志位：flags：" + flags + ",写入的数据:" + new String(cmd));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		mIsRun = false;
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

	public boolean ismIsRun() {
		return mIsRun;
	}
}
