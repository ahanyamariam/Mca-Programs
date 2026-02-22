class Item {
    int itemId;
    String itemName;
    float basePrice;
    int stockQty;
    final float GST_RATE = 0.18f;
    final String SYSTEM_NAME = "IMS v1.0";


    // Constructor
    Item(int itemId, String itemName, float basePrice, int stockQty) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.basePrice = basePrice;
        this.stockQty = stockQty;
    }


    void displayDetails() {
        System.out.println("\nSystem: " + SYSTEM_NAME);
        System.out.println("Item ID: " + itemId);
        System.out.println("Item Name: " + itemName);
        System.out.println("Base Price: " + basePrice);
        System.out.println("Stock Quantity: " + stockQty);
        System.out.println("Net Price: " + getNetPrice());
    }


    float getNetPrice() {
        float newPrice;
        newPrice = basePrice + (basePrice * GST_RATE);
        return newPrice;
    }


    final void showPolicy() {
        System.out.println("\nINVENTORY MANAGEMENT POLICY:");
        System.out.println("1. All items must be verified upon receipt for quality and quantity.");
        System.out.println("2. Pricing includes applicable GST and handling charges.");
        System.out.println("3. Damaged or expired goods are not eligible for resale.");
        System.out.println("4. Returns must be made within 7 days of purchase with a valid invoice.");
        System.out.println("5. Bulk discounts apply only for quantities above the set threshold.");
        System.out.println("6. System auto-alerts when stock quantity falls below the reorder level.");
    }
}


// Subclass 1
class PerishableItem extends Item {
    String expiryDate;
    float storageTemp;


    PerishableItem(int itemId, String itemName, float basePrice, int stockQty, String expiryDate, float storageTemp) {
        super(itemId, itemName, basePrice, stockQty);
        this.expiryDate = expiryDate;
        this.storageTemp = storageTemp;
    }


    @Override
    float getNetPrice() {
        float newPrice = super.getNetPrice();
        // Apply 10% discount for perishable goods
        newPrice = newPrice - (newPrice * 0.10f);
        return newPrice;
    }


    @Override
    void displayDetails() {
        super.displayDetails();
        System.out.println("Expiry Date: " + expiryDate);
        System.out.println("Recommended Storage Temperature: " + storageTemp + "°C");
    }
}


// Subclass 2
class NonPerishableItem extends Item {
    int warrantyMonths;


    NonPerishableItem(int itemId, String itemName, float basePrice, int stockQty, int warrantyMonths) {
        super(itemId, itemName, basePrice, stockQty);
        this.warrantyMonths = warrantyMonths;
    }


    @Override
    float getNetPrice() {
        float newPrice = super.getNetPrice();
        // Apply 5% bulk discount for non-perishable items
        newPrice = newPrice - (newPrice * 0.05f);
        return newPrice;
    }


    @Override
    void displayDetails() {
        super.displayDetails();
        System.out.println("Warranty Period: " + warrantyMonths + " months");
    }
}


// Main class
public class InventoryMain {
    public static void main(String[] args) {
        PerishableItem milk = new PerishableItem(101, "Fresh Milk", 50.0f, 100, "2025-10-20", 4.0f);
        NonPerishableItem tv = new NonPerishableItem(202, "Smart TV", 25000.0f, 10, 24);


        // Display item details
        milk.displayDetails();
        tv.displayDetails();


        // Show company policy
        milk.showPolicy();
    }
}


