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

}
