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
    /**	读写准备失败	*/
    public final static int HM_BLE_READY_FAIL	= 0xFE;
    /**	命令写入蓝牙成功	*/
    public final static int HM_CMD_WRITED		= 0x06;
    /**	命令写入蓝牙失败	*/
    public final static int HM_CMD_FAILURE		= 0xFD;

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
    public final static UUID UUID_WRITE = 
    		UUID.fromString("00002a00-0000-1000-8000-00805f9b34fb");
    /**	The UUID of the characteristic for read	*/
    public final static UUID UUID_READ = 
    		UUID.fromString("00002a00-0000-1000-8000-00805f9b34fb");

}
