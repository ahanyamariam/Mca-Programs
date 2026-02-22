
public class InventoryItem {
    private String itemPassword;
    public int itemId;
    String itemName;   // use lowercase for consistency


    private String showPassword() {
        return itemPassword;
    }


    public int showItemId() {
        return itemId;
    }


    String showItemName() {
        return itemName;
    }
}


class InventorySystem {
    public static void main(String args[]) {
        InventoryItem item = new InventoryItem();   // only this line needed
        item.itemId = 101;
        item.itemName = "Laptop";
        // item.itemPassword = "admin123"; //  private: not accessible here


        System.out.println("Item ID: " + item.showItemId());  
        System.out.println("Item Name: " + item.showItemName());
        // System.out.println("Item Password: " + item.showPassword()); //  private: not accessible
    }
}


 	
