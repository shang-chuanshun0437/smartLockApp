package mutong.com.mtaj.repository;

/*
*用来存储用户的设置信息
 */
public class Preference
{
    private String userName;

    //用户昵称
    private String nickName;

    //头像保存的路径
    private String headPortraitPath;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }


    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getHeadPortraitPath() {
        return headPortraitPath;
    }

    public void setHeadPortraitPath(String headPortraitPath) {
        this.headPortraitPath = headPortraitPath;
    }
}
