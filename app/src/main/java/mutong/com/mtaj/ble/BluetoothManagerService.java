package mutong.com.mtaj.ble;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class BluetoothManagerService
{
    private Context context;

    private BluetoothManager bluetoothManager;

    private BluetoothAdapter bluetoothAdapter;

    private Boolean scanning = false;

    private Handler handler;

    private ScanCallback scanCallback;

    private BluetoothGatt bluetoothGatt;;

    private Activity activity;

    private BluetoothGattService bluetoothGattServices;

    /**
     * 自定义的打开 Bluetooth 的请求码，与 onActivityResult 中返回的 requestCode 匹配。
     */
    private static final int REQUEST_CODE_BLUETOOTH_ON = 1313;

    public BluetoothManagerService(Context context,Handler handler)
    {
        this.context = context;
        this.handler = handler;
        this.activity = (Activity)context;
        scanCallback = new ScanCallback(context,bluetoothAdapter);

        bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
    }

    public boolean isSupoortBluetooth()
    {
        if( !context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE))
        {
            return false;
        }
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            Activity activity = (Activity)context;

            activity.startActivityForResult(enableBtIntent,REQUEST_CODE_BLUETOOTH_ON);
            return false;
        }
        return true;
    }

    //扫描BLE设备
    public void scanLeDevice()
    {
        /*BleScan bleScan = new BleScan(bluetoothAdapter);

        ExecutorService executor = Executors.newCachedThreadPool();
        Future<List<BluetoothDevice>> future = executor.submit(bleScan);

        executor.shutdown();

        try
        {
            List<BluetoothDevice> bluetoothDevices = future.get();
            for (BluetoothDevice bluetoothDevice : bluetoothDevices)
            {
                System.out.println(bluetoothDevice.getAddress());
            }
        }
        catch (Exception e)
        {

        }*/

        System.out.println("scanLeDevice thread:" + Thread.currentThread().getName());
        if (scanning)
        {
            return;
        }

        scanning = true;
        bluetoothAdapter.startLeScan(scanCallback);

        //10ms后关闭蓝牙扫描
        handler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                scanning = false;
                bluetoothAdapter.stopLeScan(scanCallback);
            }
        },10 * 1000);

    }

    public boolean connect(final String address)
    {
        //mac:08:7C:BE:EA:38:F8,name:SL1B172L06X,
        if (bluetoothAdapter == null || address == null)
        {
            return false;
        }

        final BluetoothDevice device = bluetoothAdapter.getRemoteDevice("08:7C:BE:EA:38:F8");
        if (device == null)
        {
            System.out.println("BluetoothDevice can not find");
            return false;
        }

        bluetoothGatt = device.connectGatt(context, true, gattcallback);
        return true;
    }

    private BluetoothGattCallback gattcallback = new BluetoothGattCallback()
    {
        //连接状态发生变更的时候的回调
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, final int newState)
        {
            super.onConnectionStateChange(gatt, status, newState);
            switch (newState)
            {
                //已经连接
                case BluetoothGatt.STATE_CONNECTED:
                    System.out.println("已连接蓝牙");
                    //该方法用于获取设备的服务，寻找服务
                    bluetoothGatt.discoverServices();
                    break;
                //正在连接
                case BluetoothGatt.STATE_CONNECTING:
                    System.out.println("正在连接蓝牙");
                    break;
                //连接断开
                case BluetoothGatt.STATE_DISCONNECTED:
                    System.out.println("蓝牙已断开");
                    break;
                //正在断开
                case BluetoothGatt.STATE_DISCONNECTING:
                    System.out.println("正在断开蓝牙");
                    break;
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status)
        {
            super.onServicesDiscovered(gatt, status);
            //寻找到服务时
            if (status == bluetoothGatt.GATT_SUCCESS)
            {
                System.out.println("已经寻找到蓝牙服务");
                final List<BluetoothGattService> services = bluetoothGatt.getServices();

                for (final BluetoothGattService bluetoothGattService : services)
                {
                    System.out.println("蓝牙的 Uuid: " + bluetoothGattService.getUuid());

                    List<BluetoothGattCharacteristic> characteristics = bluetoothGattService.getCharacteristics();

                    for (BluetoothGattCharacteristic characteristic : characteristics)
                    {
                        int properties = characteristic.getProperties();
                        System.out.println("蓝牙的Characteristic UUID:" + characteristic.getUuid() + ",properties:" + properties);
                        if( (properties & BluetoothGattCharacteristic.PROPERTY_READ) != 0)
                        {
                            System.out.println("蓝牙的Characteristic UUID:" + characteristic.getUuid() + ",properties:" + properties + ",属性为可读.");
                        }
                        if( (properties & BluetoothGattCharacteristic.PROPERTY_WRITE) != 0)
                        {
                            System.out.println("蓝牙的Characteristic UUID:" + characteristic.getUuid() + ",properties:" + properties + ",属性为可写.");
                        }
                        //找到透传特征值
                        // 00002a06-0000-1000-8000-00805f9b34fb 小米手环震动特征值 0x01震动 0x02强震
                        if (characteristic.getUuid().toString().equals("00002a06-0000-1000-8000-00805f9b34fb")) {
                            //设备 震动特征值
                            //characteristic_zd = charac;

                        }
                        else if (characteristic.getUuid().toString().equals("0000ff06-0000-1000-8000-00805f9b34fb")) {
                            //设备 步数
                            //characteristic_jb = charac;
                            //bluetoothGatt.readCharacteristic(characteristic_jb);

                        }
                        else if (characteristic.getUuid().toString().equals("")) {
                            //设备 电量特征值
                        }
                    }
                    //serviceslist.add(bluetoothGattService.getUuid().toString());
                }
//                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
//                                MainActivity.this, android.R.layout.simple_expandable_list_item_1, serviceslist);
                //list.setAdapter(adapter);
            }
        }
        //当读取设备时会回调该函数
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status)
        {
            super.onCharacteristicRead(gatt, characteristic, status);
            if (status == bluetoothGatt.GATT_SUCCESS)
            {
                System.out.println("读取蓝牙数据onCharacteristicRead: " + characteristic.getValue()[0]);
            }
        }

        //当向设备Descriptor中写数据时，会回调该函数
        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status)
        {
            System.out.println("onDescriptorWriteonDescriptorWrite = " + status + ", descriptor =" + descriptor.getUuid().toString());
        }

        //当向Characteristic写数据时会回调该函数
        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status)
        {
            super.onCharacteristicWrite(gatt, characteristic, status);

            System.out.println("向特性中写数据onCharacteristicWrite: " + characteristic.getValue()[0]);

        }

        //设备发出通知时会调用到该接口
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic)
        {
            super.onCharacteristicChanged(gatt, characteristic);


            System.out.println("onCharacteristicChanged: 设备发出通知时会调用到该接口");

            System.out.println(characteristic.getValue());

            final byte[] values = characteristic.getValue();

        }
    };
}
