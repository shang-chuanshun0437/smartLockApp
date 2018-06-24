package mutong.com.mtaj.repository;

/**
 * Created by Administrator on 2018/5/29.
 */

public class Device
{
    private String userName;

    private String deviceNum;

    private String deviceName;

    //管理员
    private String adminName;

    private String deviceVersion;

    //0 管理员；1 普通用户
    private int role;

    //绑定智能锁的时间
    private String attachedTime;

    //智能锁的bloothmac
    private String bloothMac;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDeviceNum() {
        return deviceNum;
    }

    public void setDeviceNum(String deviceNum) {
        this.deviceNum = deviceNum;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public String getDeviceVersion() {
        return deviceVersion;
    }

    public void setDeviceVersion(String deviceVersion) {
        this.deviceVersion = deviceVersion;
    }

    public String getAttachedTime() {
        return attachedTime;
    }

    public void setAttachedTime(String attachedTime) {
        this.attachedTime = attachedTime;
    }

    public String getBloothMac() {
        return bloothMac;
    }

    public void setBloothMac(String bloothMac) {
        this.bloothMac = bloothMac;
    }
}
