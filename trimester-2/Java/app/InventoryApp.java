package app;

import impl.*;
import interfaces.PricingStrategy;
import interfaces.StockItem;
import interfaces.Auditable;
import java.time.LocalDate;
import java.util.List;

/** Public main class as requested. */
public class InventoryApp {

    public static void main(String[] args) {
        System.out.println("\n=== Inventory Management System — Demo & Tests ===\n");

        // Create pricing strategies via interface ref
        PricingStrategy flat = new FlatRatePricing();
        PricingStrategy bulk10 = new BulkDiscountStrategy(10, 0.10);
        PricingStrategy clearance5d = new ClearancePricing(5, 0.30);

        // Create items using interface references (polymorphism)
        StockItem rice = BaseStockItem.of("SKU-001", "Basmati Rice 5kg", "Aisle 3 / Bin B", 600.00, 50);
        StockItem milk = new PerishableItem("SKU-010", "Organic Milk 1L", "Chiller 1", 75.00, 30,
                LocalDate.now().plusDays(4));
        StockItem laptop = new SerializedItem("SKU-500", "Ultrabook 13\"", "Cage 2", 82000.00, 2,
                List.of("S-AX9Q1", "S-AX9Q2"));

        // Exercise functionality: restock, reserve, release, ship
        rice.restock(20); // on-hand 70
        boolean ok1 = rice.reserve(12); // reserve 12
        rice.shipReserved(10); // ship 10
        rice.release(2); // release remaining 2

        boolean ok2 = milk.reserve(5); // allowed (not expired)
        milk.shipReserved(5);

        // Serialized item: restock with serials, then reserve/ship
        if (laptop instanceof SerializedItem si) {
            si.restockWithSerial("S-AX9Q3"); // adds 1 unit and serial
        }
        boolean ok3 = laptop.reserve(2);
        laptop.shipReserved(2);

        // Pricing demonstrations through PricingStrategy interface ref
        printPrice(rice, 8, flat);
        printPrice(rice, 12, bulk10);
        printPrice(milk, 3, flat);
        printPrice(milk, 3, clearance5d); // near expiry → markdown applies
        printPrice(laptop, 1, flat);

        // Pretty print item state & selected audit lines
        printItemSummary(rice);
        printItemSummary(milk);
        printItemSummary(laptop);

        // Basic assertions acting as lightweight tests
        assert ok1 : "Rice reservation should succeed";
        assert ok2 : "Milk reservation should succeed before expiry";
        assert ok3 : "Laptop reservation should succeed with stock";

        System.out.println("\nAll tests executed. If no assertion errors were printed, the demo passed.\n");
    }

    private static void printPrice(StockItem item, int qty, PricingStrategy strategy) {
        double amount = strategy.price(item, qty);
        System.out.printf("PRICE • %-22s × %2d using %-20s → ₹%,.2f%n",
                item.getName(), qty, strategy.name(), amount);
    }

    private static void printItemSummary(StockItem item) {
        String hdr = "-".repeat(78);
        System.out.println("\n" + hdr);
        System.out.printf("%-16s: %s%n", "SKU", item.getSku());
        System.out.printf("%-16s: %s%n", "Name", item.getName());
        System.out.printf("%-16s: %s%n", "Location", item.getLocation());
        System.out.printf("%-16s: %d on-hand | %d reserved | %d available%n",
                "Quantities", item.getQuantityOnHand(), item.getQuantityReserved(), item.getAvailable());
        System.out.printf("%-16s: ₹%,.2f%n", "Base Unit Price", item.getBaseUnitPrice());

        if (item instanceof PerishableItem p) {
            System.out.printf("%-16s: %s%n", "Expiry", p.getExpiryDate());
        }
        if (item instanceof SerializedItem si) {
            System.out.printf("%-16s: %s%n", "Next Serial", String.valueOf(si.peekNextSerial()));
        }

        // Print the last few audit entries if available
        if (item instanceof Auditable a) {
            var log = a.getAuditLog();
            System.out.println("Audit (latest 5):");
            int start = Math.max(0, log.size() - 5);
            for (int i = start; i < log.size(); i++) {
                System.out.printf("  • %s%n", log.get(i));
            }
        }
    }
}
