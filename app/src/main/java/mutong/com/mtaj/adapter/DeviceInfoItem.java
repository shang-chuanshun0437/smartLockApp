package mutong.com.mtaj.adapter;

public class DeviceInfoItem
{
    private String itemName;
    private String item;

    public DeviceInfoItem(String item,String itemName)
    {
        this.item = item;
        this.itemName = itemName;
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
}
