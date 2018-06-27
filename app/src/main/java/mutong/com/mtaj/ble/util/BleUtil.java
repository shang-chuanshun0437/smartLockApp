package mutong.com.mtaj.ble.util;

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
public final class BleUtil {

	private final static String TAG = "BlePackage";

	/**
	 * We have packaged the queue for message what received from BLE.
	 * And you can use some interface, for example {@link BleQueue#addElement(byte[])}
	 * , {@link BleQueue#getBytes(int)} etc.
	 * @author UESTC-PRMI-Burial
	 * @date 2014-12-15
	 *
	 */
	public final static class BleQueue {
		
		private final static String TAG = "BleQueue";
		/** The lock for read and write operation */
		private ReadWriteLock mRWLock;
		/** When the data is full, the lock is locked */
		private Lock mRangeLock;
		private Condition mRangeCondition;
		/** What the buffer is used as a temporary place for message is a queue.	*/
		private byte[] mQueue;
		/** The index of the top of {@link BleQueue#mQueue}.	*/
		private int mTop;
		/** The index of the bottom of {@link BleQueue#mQueue}.	*/
		private int mBottom;
		/** The length of {@link BleQueue#mQueue}.	*/
		public int length;
		/** We have been recording the size of queue which has used.	*/
		public int size;
		
		/**
		 * The constructor initalize this queue with the special queue length.
		 * @param queueLen The length of this queue.Fails if queueLen is equal
		 * or lesser than zero.
		 */
		public BleQueue(int queueLen) {
			if (queueLen <= 0) {
				Log.w(TAG, "The length of queue is wrong!");
				length = 0;
				size = 0;
				return;
			}
			length = queueLen;
			mQueue = new byte[length];
			mTop = 0;
			mBottom = 0;
			size = 0;
			mRWLock = new ReentrantReadWriteLock();
			mRangeLock = new ReentrantLock();
			mRangeCondition = mRangeLock.newCondition();
		}

		/**
		 * Adds the specified data at the end of this queue.
		 * @param data The data to add.
		 */
		public void addElement(byte[] data) {
			if (null == mQueue) {
				return;
			}
			if ((null == data) || (0 == data.length) || (data.length > length)) {
				if (D) Log.i(TAG, "The element is null");
				return;
			}
			
			while (true) {
				if ((data.length + size) > length) {
					mRangeLock.lock();
					try {
						if (D)	Log.i(TAG, "Try Range wait");
						mRangeCondition.await();
						if (D)	Log.i(TAG, "Range Signal");
					} catch (Exception e) {
						e.printStackTrace();
						if (D)	Log.i(TAG, "Range await() is fails.");
					}
					mRangeLock.unlock();
				} else {
					break;
				}
			}
			
			mRWLock.writeLock().lock();	// Write Lock
			
			if ((mBottom + data.length) <= length) {
				System.arraycopy(data, 0, mQueue, mBottom, data.length);
				mBottom += data.length;
			} else {
				int remainder = length - mBottom;
				System.arraycopy(data, 0, mQueue, mBottom, remainder);
				mBottom = data.length - remainder;
				System.arraycopy(data, remainder, mQueue, 0, mBottom);
			}
			mBottom %= length;
			// Now we are calculating the size of data
			size += data.length;
			if (size > length) {	// The maximum of size is equal to the length
				size = length;
				mTop = mBottom;
			}
			
			mRWLock.writeLock().unlock();	// UnLock
			if (D) Log.i(TAG, "Add data successful");
			
		}
		
		/**
		 * Removes the specified length of data at the front of this queue.
		 * @param dataLen The length of data to remove, If the dataLen is greater than
		 * {@link BleQueue#size}, we will {@link BleQueue#clear()}
		 */
		public void removeElement(int dataLen) {
			if (null == mQueue) {
				return;
			}
			if (dataLen <= 0) {
				if (D) Log.i(TAG, "The dataLen is wrong!");
				return;
			}
			if (dataLen >= size) {
				clear();
			} else {
				size -= dataLen;
			}
			
			mTop += dataLen;
			mTop %= length;	// The maximum of mTop is equal to the length
			
			mRangeLock.lock();
			mRangeCondition.signalAll();	// UnLock
			if (D)	Log.i(TAG, "Range signalAll");
			mRangeLock.unlock();

		}
		
		/**
		 * Returns the element at the specified index in this queque.
		 * @param index The index of the element to return.
		 * @return The element at the specified index, Or 0x00 if the index is out of range.
		 */
		public byte getByte(int index) {
			byte element = 0x00;
			if (null == mQueue) {
				return element;
			}
			mRWLock.readLock().lock();	// Read Lock
			
			if (index < size) {
				int position = ((mTop + index) % length);
				element = mQueue[position];
			}
			
			mRWLock.readLock().unlock();	// UnLock
			return element;
		}
		
		/**
		 * Returns the array at the specified length in this queque.
		 * @param arrayLen The length of the array to return.
		 * @return The array at the specified length.But if the arrayLen is greater than
		 * {@link BleQueue#size}, we will return all of the data in this queue,if there
		 * is no data, we will return null.
		 */
		public byte[] getBytes(int arrayLen) {
			byte[] array = null;
			if ((null == mQueue) || (0 == size)) {
				return array;
			}
			
			mRWLock.readLock().lock();	// Read Lock
			
			if (arrayLen >= size) {
				array = new byte[size];
				if ((mTop + size) <= length) {
					System.arraycopy(mQueue, mTop, array, 0, size);
				} else {
					int remainder = length - mTop;
					System.arraycopy(mQueue, mTop, array, 0, remainder);
					System.arraycopy(mQueue, 0, array, remainder, size - remainder);
				}
			} else {
				array = new byte[arrayLen];
				if ((arrayLen + mTop) <= length) {
					System.arraycopy(mQueue, mTop, array, 0, arrayLen);
				} else {
					int remainder = arrayLen + mTop - length;
					System.arraycopy(mQueue, mTop, array, 0, length - mTop);
					System.arraycopy(mQueue, 0, array, length - mTop, remainder);
				}
			}
			
			mRWLock.readLock().unlock();	// UnLock
			
			return array;
		}
		
		/**
		 * Returns the array included all of the data in this queque.
		 * @return We will return all of the data in this queue,if there
		 * is no data, we will return null.
		 */
		public byte[] getAllBytes() {
			byte[] array = null;
			if ((null == mQueue) || (0 == size)) {
				return array;
			}
			
			mRWLock.readLock().lock();	// Read Lock
			
			array = new byte[size];
			if ((mTop + size) <= length) {
				System.arraycopy(mQueue, mTop, array, 0, size);
			} else {
				int remainder = length - mTop;
				System.arraycopy(mQueue, mTop, array, 0, remainder);
				System.arraycopy(mQueue, 0, array, remainder, size - remainder);
			}
			
			mRWLock.readLock().unlock();	// UnLock
			
			return array;
		}
		
		/**
		 * If you want to know whether is empty about this queue, you can call this funcion.
		 * @return True if this queue is empty, Or false if not;
		 */
		public boolean isEmpty() {
			return (size == 0);
		}
		
		/**
		 * Removes all elements from this queue, leaving it empty.
		 */
		public void clear() {
			if (mQueue != null) {
				mQueue = null;
				mQueue = new byte[length];
			}
			size = 0;
			mTop = 0;
			mBottom = 0;
		}
		
		/**
		 * Destroys this queue.
		 */
		public void destroy() {
			if (mQueue != null) {
				mQueue = null;
			}
			length = 0;
			size = 0;
			mTop = 0;
			mBottom = 0;
		}
	
	}

    
}
