package mutong.com.mtaj.repository;

/*
*用来存储用户的设置信息
 */
public class Preference
{
    private String phoneNum;

    //头像保存的路径
    private String headPortraitPath;

    public String getHeadPortraitPath() {
        return headPortraitPath;
    }

    public void setHeadPortraitPath(String headPortraitPath) {
        this.headPortraitPath = headPortraitPath;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }
}
