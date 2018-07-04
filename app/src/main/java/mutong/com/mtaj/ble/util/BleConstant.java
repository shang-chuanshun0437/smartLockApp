package mutong.com.mtaj.ble.util;

import java.util.UUID;

/**
 * Defined the constants or the static variables
 * @author UESTC-PRMI-Burial
 * @date 2014-12-13
 *
 */
public final class BleConstant {
	
	private final static String TAG = "BleConstant";
	
    /**
     * Handler传递消息码定义.
     */
	/**	该手机不支持BLE	*/
	public final static int HM_BLE_NONSUPPORT	= 0xFF;
	/**	请求开启蓝牙	*/
	public final static int HM_BLE_ENABLEBT		= 0x02;
	/**	连接成功	*/
    public final static int HM_BLE_CONNECTED	= 0x03;
    /**	连接失败或连接丢失	*/
    public final static int HM_BLE_DISCONNECTED	= 0x04;
    /**	读写准备就绪	*/
    public final static int HM_BLE_READY		= 0x05;
    /**	写UUID没找到	*/
    public final static int BLE_WRITE_NOTFOUND	= 0xFE;
    /**	写UUID已找到	*/
    public final static int BLE_WRITE_FOUND	= 1003;

    /**	读UUID没找到	*/
    public final static int BLE_READ_NOTFOUND	= 1004;
    /**	读UUID已找到	*/
    public final static int BLE_READ_FOUND	= 1005;

    /**	读取成功*/
    public final static int BLE_READ_SUCCESS = 1006;
    /**	读取失败	*/
    public final static int BLE_READ_FAIL	= 1007;

    /**	写失败	*/
    public final static int BLE_WRITE_FAIL	= 1002;
    /**	写成功	*/
    public final static int BLE_WRITE_SUCCESS	= 1009;

    /**	收到通知	*/
    public final static int BLE_NOTIFY_SUCCESS	= 1008;

    /**	找不到蓝牙	*/
    public final static int BLE_NOTFOUND	= 1010;

    /**	Toast, 系统信息(测试用)	*/
    public final static int HM_DEBUG_TOAST		= 0x88;
    /**	Command(测试用)	*/
    public final static int HM_DEBUG_CMD		= 0x89;
    /**	Message(测试用)	*/
    public final static int HM_DEBUG_MSG		= 0x90;
	
	/**	A file what save some cache	*/
	public final static String BLE_CACHE = "BleCache";
	/**	The name of the preference about the bluetooth address	*/
	public final static String BLE_PREF_MAC = "BleMac";
	
	/**	True If You Want To print The Debug Information	*/
	public final static boolean D = true;
	
    /**	The UUID of the characteristic for write	*/
    public final static UUID UUID_WRITE = UUID.fromString("0000fef1-0000-1000-8000-00805f9b34fb");
    //public final static UUID UUID_WRITE = UUID.fromString("00002902-0000-1000-8000-00805f9b34fc");
    /**	The UUID of the characteristic for read	*/
    public final static UUID UUID_READ = UUID.fromString("0000fef2-0000-1000-8000-00805f9b34fb");
    //public final static UUID UUID_READ = UUID.fromString("00002902-0000-1000-8000-00805f9b34fc");
    /**	The UUID of the service */
    public final static UUID UUID_SERVICE = UUID.fromString("0000fef0-0000-1000-8000-00805f9b34fb");
    //public final static UUID UUID_SERVICE = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");


}
