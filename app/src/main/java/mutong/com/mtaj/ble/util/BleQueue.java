package mutong.com.mtaj.ble.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import android.util.Log;

import static mutong.com.mtaj.ble.util.BleConstant.D;

/**
 * Implement the utility method set relevant to parse message Or package command.
 * The message what defined received from Bluetooth Low Energy(BLE), And
 * the command what defined send to BLE,The same below.
 * @author UESTC-PRMI-Burial
 * @date 2014-12-13
 *
 */
public class BleQueue
{
	private final static String TAG = "BleQueue";
	/** The lock for read and write operation */
	private ReadWriteLock mRWLock;
	/** When the data is full, the lock is locked */
	private Lock mRangeLock;
	private Condition mRangeCondition;

	private List<String> sendQueue = new ArrayList<String>();
	private List<String> reciveQueue = new ArrayList<String>();

	private static BleQueue bleQueue;

	private BleQueue()
	{
		mRWLock = new ReentrantReadWriteLock();
		mRangeLock = new ReentrantLock();
		mRangeCondition = mRangeLock.newCondition();
	}

	//获取单实例
	public static BleQueue getInstance()
	{
		if (bleQueue == null)
		{
			synchronized (BleQueue.class)
			{
				if (bleQueue == null)
				{
					bleQueue = new BleQueue();
				}
			}
		}
		return bleQueue;
	}

	public synchronized void addSendElement(byte[] data)
	{
		//mRWLock.writeLock().lock();	// Write Lock
		String sendData = new String(data);

		sendQueue.add(sendData);

		//mRWLock.writeLock().unlock();	// UnLock
		if (D) Log.i(TAG, "Add data successful");

	}

	public void addReciveElement(byte[] data)
	{
		mRWLock.writeLock().lock();	// Write Lock
		String sendData = new String(data);

		reciveQueue.add(sendData);

		mRWLock.writeLock().unlock();	// UnLock
		if (D) Log.i(TAG, "Add data successful");

	}

	public synchronized String getSendElement()
	{
		//mRWLock.writeLock().lock();	// Write Lock

		if (sendQueue.size() == 0)
		{
			return null;
		}

		String sendData = sendQueue.get(0);
		sendQueue.remove(0);

		//mRWLock.writeLock().unlock();	// UnLock
		if (D) Log.i(TAG, "Add data successful");

		return sendData;
	}

	public synchronized String getReviceElement()
	{
		//mRWLock.writeLock().lock();	// Write Lock

		if (reciveQueue.size() == 0)
		{
			return null;
		}
		String recivedData = reciveQueue.get(0);
		reciveQueue.remove(0);

		//mRWLock.writeLock().unlock();	// UnLock
		if (D) Log.i(TAG, "Add data successful");

		return recivedData;
	}

	//type 0 发送队列； 1 接受队列
	public boolean isEmpty(int type)
	{
		List<String> list = (type == 0) ? sendQueue : reciveQueue;

		if(list.size() <= 0)
		{
			return true;
		}

		return true;
	}

	public void destroy()
	{
		mRWLock.writeLock().lock();	// Write Lock

		sendQueue.clear();
		reciveQueue.clear();

		mRWLock.writeLock().unlock();	// UnLock
		if (D) Log.i(TAG, "blequeue destroy successful");
	}
}
