package mutong.com.mtaj.ble.util;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import mutong.com.mtaj.utils.StringUtil;

import static mutong.com.mtaj.ble.util.BleConstant.D;


/**
 * When message are reached the first time, This thread is start to run.
 * @author UESTC-PRMI-Burial
 *
 */
public class BleParseMsgThread extends Thread
{
	
	private final static String TAG = "BleParseMsg";
	
	/** False if you want to cancel this thread.	*/
	private boolean mIsRun;
	/**	What the Handler is traced to BleHandler is an input parameter when you get the instance of BleParseMessageThread	*/ 
	private Handler mHandler;
	/** The inner lock */
	private Lock mInnerLock;
	/** The inner condition corresponding to the inner lock */
	private Condition mInnerCondition;

	/** 
	 * The queue for message. 
	 * @see BleQueue	
	 */
	private BleQueue mQueue;
	
	/**
	 * The constructor for {@link BleParseMsgThread}.
	 */
	public BleParseMsgThread(Handler handler)
	{
		// Initialization
		mInnerLock = new ReentrantLock();
		mInnerCondition = mInnerLock.newCondition();

		mHandler = handler;
		mQueue = BleQueue.getInstance();
		// mMessage = null;
		mIsRun = true;
		Log.i(TAG, "We are running the BleParseMsg thread.");
	}
	
	/**
	 * Writing the message into the message queue.
	 * @param message the message to write.
	 */
	public void addMessagePackage(byte[] message)
	{
		if (null == message)
		{
			if (D) Log.i(TAG, "The message is null!");
			return;
		}
		
		mQueue.addReciveElement(message);
		
		mInnerLock.lock();
		mInnerCondition.signalAll();	// UnLock
		mInnerLock.unlock();
		
		return;
	}
	
	@Override
	public void run()
	{
		byte[] message = null;
		
		while (mIsRun)
		{
			message = getMessageFromBuffer();
			if (null != message)
			{
				parseMessage(message);
			}
			else
			{
				mInnerLock.lock();
				try
				{
					if (D)	Log.i(TAG, "We're waiting the message.");
					mInnerCondition.await();
				} catch (InterruptedException e)
				{
					e.printStackTrace();
					if (D)	Log.i(TAG, "await() is fails.");
				}
				mInnerLock.unlock();
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
		Log.i(TAG, "We have stoped the BleParseMsg thread.");
	}
    
	private void parseMessage(byte[] msg)
	{
		if (null == msg)
		{
			Log.w(TAG, "The message to parse is null");
			return;
		}

		// Now we're parsing the message
		
	}
	
    /**
     * The messaging function
     * @param what	 The handler code defined in the Constants
     * @param message	The message received form BLE
     * 
     */
    private void broadcastUpdate(final int what, Object message)
	{
    	Message msg = new Message();
    	msg.what = what;
    	msg.obj = message;
    	mHandler.sendMessage(msg);
        if (D)	Log.i(TAG, "broadcastUpdate: " + what);
    }
	
    /**
     * The messaging function
     * @param what	 The handler code defined in the Constants
     * @param para	The integer what you want to transfer
     * 
     */
    private void broadcastUpdate(final int what, int para)
	{
    	Message msg = new Message();
    	msg.what = what;
    	msg.arg1 = para;
    	mHandler.sendMessage(msg);
        if (D)	Log.i(TAG, "broadcastUpdate: " + what);
    }
    
	/**
	 * Getting message from the {@link BleParseMsgThread#mQueue}.
	 * @return The message included in the buffer.
	 */
	private byte[] getMessageFromBuffer()
	{
		String sendData = mQueue.getSendElement();
		if(StringUtil.isEmpty(sendData))
		{
			return null;
		}

		return sendData.getBytes();
	}
}
