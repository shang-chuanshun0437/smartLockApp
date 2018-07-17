package mutong.com.mtaj.common;

/**
 * Created by Administrator on 2018/5/30.
 */

public class ErrorCode
{
    //用户名已存在
    public static final String USERNAME_EXIST = "100004";

    public static final String DEFAULT_ERROR = "100005";

    //服务器返回成功
    public static final String SUCEESS = "000000";

    //用户不存在
    public static final String USERPHONE_NOT_EXIST = "100006";

    //用户未登录
    public static final String NOT_LOGIN = "100009";

    //密码错误
    public static final String PASSWORD_ERROR = "100007";

    //redis中验证码为null
    public static final String VERIFY_CODE_NULL = "100012";

    //redis中验证码为不正确
    public static final String VERIFY_CODE_ERROR = "100013";

    //redis中验证码凭据不正确
    public static final String VERIFY_VOUCHER_ERROR = "100014";

    //数据库中不存在该设备
    public static final String DEVICE_NOT_EXIT = "200001";

    //后台数据库中，设备的主用户和请求中传来的用户不匹配
    public static final String MAIN_USER_MISSMATCH = "200002";

    //设备的管理员删除自己，该设备下有其他用户
    public static final String OTHER_USERS_EXIST = "200003";

}
