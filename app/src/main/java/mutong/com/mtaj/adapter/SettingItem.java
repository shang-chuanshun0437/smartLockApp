package mutong.com.mtaj.adapter;

public class SettingItem
{
    private String settingsName;
    private int imgId;

    public SettingItem(String settingsName,int imgId)
    {
        this.settingsName = settingsName;
        this.imgId = imgId;
    }

    public SettingItem(){}

    public String getSettingsName() {
        return settingsName;
    }

    public void setSettingsName(String settingsName) {
        this.settingsName = settingsName;
    }

    public int getImgId() {
        return imgId;
    }

    public void setImgId(int imgId) {
        this.imgId = imgId;
    }
}
