package mutong.com.mtaj.adapter;

public class DeviceInfoPicItem
{
    private String itemName;
    private String item;
    private int imageId;

    public DeviceInfoPicItem(String item, String itemName,int imageId)
    {
        this.item = item;
        this.itemName = itemName;
        this.imageId = imageId;
    }
    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }
}
