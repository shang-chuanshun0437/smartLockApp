package mutong.com.mtaj.common;

public class Constant
{
    public static int MAIN_GRID_VIEW_ROW_DIVID = 15;

    //sqlite版本
    public static int DBVERSION = 4;

    //手机号位数
    public static int PHONUM_COUNT = 11;

    //密码的最少位数
    public static int MIN_PWD = 6;

    //URL前缀
    public static String URL_PREFIX = "http://47.94.86.112:8080/smartlock/v1";

    //login_user表
    public static String LOGIN_USER_TABLE = "login_user";

    //device_user表
    public static String DEVICE_USER_TABLE = "device_user";

    //默认的terminalID
    public static String DEFALUT_TERMINALID = "000000000000000";

    //后台正常返回
    public static final int CONSLE_SUCCESS = 1;

    //后台异常返回
    public static final int CONSLE_FAIL = 0;

    //申请位置权限
    public static final int REQUEST_COARSE_LOCATION = 10001;

    //申请定位权限
    public static final int LOCATION_PERMISSION = 10002;

    //蓝牙读标志位
    public static final int BLE_READ = 10003;

    //蓝牙连接标志位
    public static final int BLE_CONNECT = 10004;

    //蓝牙服务标志位
    public static final int BLE_SERVICE = 10005;
}
