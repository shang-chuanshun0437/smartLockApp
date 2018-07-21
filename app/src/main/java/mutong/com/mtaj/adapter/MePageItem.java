package mutong.com.mtaj.adapter;

public class MePageItem
{
    private String itemName;
    private int headId;
    private int forwardId;

    public MePageItem(String itemName, int headId,int forwardId)
    {
        this.itemName = itemName;
        this.headId = headId;
        this.forwardId = forwardId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getHeadId() {
        return headId;
    }

    public void setHeadId(int headId) {
        this.headId = headId;
    }

    public int getForwardId() {
        return forwardId;
    }

    public void setForwardId(int forwardId) {
        this.forwardId = forwardId;
    }
}
