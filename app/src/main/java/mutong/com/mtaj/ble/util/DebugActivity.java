package mutong.com.mtaj.ble.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

import com.burial.blehelper.BleHandler;
import com.burial.blehelper.BleHelper;
import com.burial.blehelpertestactivity.R;
import com.burial.util.BleConstant;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 测试界面类.	AndroidManifest.xml 需要添加响应的Server声明
 * 包含工具类的使用方法
 * @author UESTC-PRMI-Burial
 *
 */
public class DebugActivity extends Activity {

	private final static String TAG = "TestActivity";
	
	private MyHandler mBleHandler = new MyHandler(this);
	private BleHelper mBleHelper;
	// private String mDeviceAddress = "D0:39:72:A8:F3:A3";
	private String mDeviceAddress = "B4:99:4C:4D:C1:D1";
	
	private MenuItem mConnect;
	private Button mRead;
	private Button mWrite;
	private Button mClear;
	private EditText mMac;
	private ListView mRecv;
	private ListView mSend;
	private StrAdapter mRecvAdapter;
	private StrAdapter mSendAdapter;
	private ArrayList<String> mRecvList;
	private ArrayList<String> mSendList;
	private final boolean bRecv = true;
	private final boolean bSend = false;
	
	/**
	 * 界面类应该包含代码
	 * 管理服务器生命周期的代码
	 */
    private final ServiceConnection mServiceConnection = new ServiceConnection() {
    	// TODO Auto-generated method stub
    	private final static String TAG = "ServiceConnection";
    	
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
        	mBleHelper = ((BleHelper.LocalBinder) service).getService(mBleHandler);
            if (!mBleHelper.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            // 成功初始化之后,自动连接蓝牙
            if (!mBleHelper.connect()) {
            	showToast("Please Input The Mac Address And Click The Mac");
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
        	mBleHelper = null;
        }
    };
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blehelper);
        
        mRead = (Button) this.findViewById(R.id.bt_set);
        mRead.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String temp = mMac.getText().toString().toUpperCase();
				if ((null == temp) || (temp.equals(""))) {
					Toast.makeText(DebugActivity.this, "Please Input The Mac Address Of Bluetooth", Toast.LENGTH_SHORT).show();
					return;
				}
				if (/*mBleHelper.writeMacAddress(temp)*/mBleHelper.writeMacAddress(mDeviceAddress)) {
					mBleHelper.connect();
					showToast("Connecting...");
				} else {
					showToast("The MAC address is invalid");
				}
			}
        });
        
        mWrite = (Button) this.findViewById(R.id.bt_write);
        mWrite.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String temp = mMac.getText().toString().toUpperCase();
				if ((null == temp) || (temp.equals(""))) {
					Toast.makeText(DebugActivity.this, "Please Input The Mac Address Of Bluetooth", Toast.LENGTH_SHORT).show();
					return;
				}
				mBleHelper.sendCmdToBle(temp.getBytes());
			}
        });
        
        mClear = (Button) this.findViewById(R.id.bt_clear);
        mClear.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mRecvList.clear();
				mRecvAdapter.notifyDataSetChanged();
				
				mSendList.clear();
				mSendAdapter.notifyDataSetChanged();
			}
        	
        });
        
        mMac = (EditText) this.findViewById(R.id.et_mac);
        
        mRecv = (ListView) this.findViewById(R.id.lv_recv);
        mSend = (ListView) this.findViewById(R.id.lv_send);
        mRecvList = new ArrayList<String>();
        mSendList = new ArrayList<String>();
        mRecvAdapter = new StrAdapter(bRecv);
        mSendAdapter = new StrAdapter(bSend);
        mRecv.setAdapter(mRecvAdapter);
        mSend.setAdapter(mSendAdapter);

        /**
         * 初始化BleHelper服务器,AndroidManifest.xml一定要加入相关说明
         */
        Intent gattServiceIntent = new Intent(this, BleHelper.class);
        if (bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE)) {
        	Toast.makeText(this, "success", Toast.LENGTH_SHORT).show();
        } else {
        	Toast.makeText(this, "fail", Toast.LENGTH_SHORT).show();
        }
    }

    public byte[] hex2byte(byte[] b) {
    	byte[] temp = new byte[b.length];
    	int tempLen = 0;
    	for (byte bb : b) {
    		if (bb != 0x20) {
    			temp[tempLen++] = bb;
    		}
    	}
    	if (tempLen >= 2) {
    		b = new byte[tempLen];
    		System.arraycopy(temp, 0, b, 0, tempLen);
    	} else {
    		return null;
    	}

        if ((b.length % 2) != 0) {
            return null;
        }
        int length = b.length / 2;
        byte[] b2 = new byte[length];
        for (int n = 0; n < b.length; n += 2) {
            String item = new String(b, n, 2);
            // 两位一组，表示一个字节,把这样表示的16进制字符串，还原成一个进制字节
            b2[n / 2] = (byte) Integer.parseInt(item, 16);
        }
        b = null;
        return b2;
    }
    
    @Override
	protected void onDestroy() {
		super.onDestroy();
		// TODO Auto-generated method stub
		unbindService(mServiceConnection);
        mBleHelper = null;
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.i(TAG, "onResum");
		if (mBleHelper != null) {
			mBleHelper.disconnect();
            final boolean result = mBleHelper.connect();
            Log.d(TAG, "Connect request result=" + result);
        }
		
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.bletest, menu);
        
        mConnect = menu.findItem(R.id.connect);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.connect) {
        	if (mBleHelper.getCurrentConnectionState()) {
        		mBleHelper.disconnect();
        		showToast("Disconnecting");
        	} else {
        		mBleHelper.connect();
        		showToast("Connecting");
        	}
        }
        return super.onOptionsItemSelected(item);
    }
    
    private void showToast(String msg) {
    	Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
    
    private class StrAdapter extends BaseAdapter {
    	
        private LayoutInflater mmInflater;
        private boolean mmWhich;
        
        public StrAdapter(boolean which) {
        	mmInflater = LayoutInflater.from(DebugActivity.this);
        	mmWhich = which;
        }
        
    	@Override
    	public int getCount() {
    		if (mmWhich == bSend) {
    			return mSendList.size();
    		} else {
    			return mRecvList.size();
    		}
    		
    	}

    	@Override
    	public Object getItem(int position) {
    		if (mmWhich == bSend) {
    			return mSendList.get(position);
    		} else {
    			return mRecvList.get(position);
    		}
    	}

    	@Override
    	public long getItemId(int position) {
    		// TODO Auto-generated method stub
    		return position;
    	}

    	@Override
    	public View getView(int position, View convertView, ViewGroup parent) {
    		String content = null;
    		View view = mmInflater.inflate(R.layout.item, null);  
    		if (mmWhich == bSend) {
    			content = mSendList.get(position);
    			view.setBackgroundResource(R.drawable.sendbox);
    		} else {
    			content = mRecvList.get(position);
    			view.setBackgroundResource(R.drawable.recvbox);
    		}
            
            TextView tv = (TextView) view.findViewById(R.id.tv_item);
            tv.setText(content);
            tv.setTextSize(15);
            
            return view;
    	}
    	
    }
    
    private static class MyHandler extends BleHandler {

    	private DebugActivity mActivity;
    	MyHandler(DebugActivity activity) {
    		mActivity = activity;
    	}

		@Override
		protected void answerConnectionState(boolean isLink) {
			// TODO Auto-generated method stub
			if (isLink) {
				Toast.makeText(mActivity, "Connected", Toast.LENGTH_SHORT).show();
				// mContent.setText("Connected");
				mActivity.mConnect.setIcon(R.drawable.ic_bt_connected);
			} else {
				Toast.makeText(mActivity, "Disconnect", Toast.LENGTH_SHORT).show();
				// mContent.setText("Disconnect");
				mActivity.mConnect.setIcon(R.drawable.ic_bt_available);
			}
			
		}

		@Override
		protected void answerNonsupport() {
			// TODO Auto-generated method stub
			mActivity.mMac.setText("Nonsupport BLE");
		}
		
		@Override
		protected void answerToastForDebug(String msg) {
			// TODO Auto-generated method stub
			Toast.makeText(mActivity, msg, Toast.LENGTH_SHORT).show();
		}
		
		@Override
		protected void answerMessageForDebug(String msg) {
			// TODO Auto-generated method stub
			mActivity.mRecvList.add(msg);
			if (mActivity.mRecvList.size() >= 10) {
				mActivity.mRecvList.clear();
			}
			mActivity.mRecvAdapter.notifyDataSetChanged();
		}

		@Override
		protected void answerCommandForDebug(String cmd) {
			// TODO Auto-generated method stub
			mActivity.mSendList.add(cmd);
			if (mActivity.mRecvList.size() >= 10) {
				mActivity.mRecvList.clear();
			}
			mActivity.mSendAdapter.notifyDataSetChanged();
		}

		@Override
		protected void answerWrite(boolean isWrited) {
			// TODO Auto-generated method stub
			
		}

		@Override
		protected void answerReady(boolean isReady) {
			// TODO Auto-generated method stub
			
		}
    	
    }

}
