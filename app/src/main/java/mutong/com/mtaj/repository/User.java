package mutong.com.mtaj.repository;

/**
 * Created by Administrator on 2018/5/28.
 */

public class User
{
    private String userName;

    private String phoneNum;

    private String userToken ;

    private String refreshToken ;

    private String password;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    @Override
    public String toString() {
        return "User{" +
                "userName='" + userName + '\'' +
                ", userToken='" + userToken + '\'' +
                ", refreshToken='" + refreshToken + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }
}
