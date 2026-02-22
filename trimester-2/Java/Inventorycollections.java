import java.util.*;
import java.util.stream.*;


interface Stockable {
    String getSku();
    String getName();
    String getCategory();
    int getQuantity();
    double getUnitPrice();

    default double getTotalValue() {
        return getQuantity() * getUnitPrice();
    }
}


interface Reportable {
    void printReport();
}



class Product implements Stockable {
    private final String sku;
    private final String name;
    private final String category;
    private int quantity;
    private final double unitPrice;

    public Product(String sku, String name, String category,
                   int quantity, double unitPrice) {
        this.sku       = sku;
        this.name      = name;
        this.category  = category;
        this.quantity  = quantity;
        this.unitPrice = unitPrice;
    }

    @Override public String getSku()       { return sku; }
    @Override public String getName()      { return name; }
    @Override public String getCategory()  { return category; }
    @Override public int    getQuantity()  { return quantity; }
    @Override public double getUnitPrice() { return unitPrice; }

    public void restock(int units) { quantity += units; }
    public void sell(int units)    { quantity = Math.max(0, quantity - units); }

    @Override
    public String toString() {
        return String.format("%-10s %-25s %-15s Qty:%-5d $%.2f",
                sku, name, category, quantity, unitPrice);
    }
}


class InventoryManager implements Reportable {

  
    private final Map<String, Product> catalog = new LinkedHashMap<>();

    
    private final Map<String, List<Product>> byCategory = new TreeMap<>();

    
    private final Queue<String> restockQueue = new LinkedList<>();

    
    private final Set<String> lowStockAlerts = new HashSet<>();

   
    private final List<Double> priceList = new ArrayList<>();

    private static final int LOW_STOCK_THRESHOLD = 10;

   
    /** Add a new product to the inventory. */
    public void addProduct(Product p) {
        catalog.put(p.getSku(), p);
        byCategory.computeIfAbsent(p.getCategory(), k -> new ArrayList<>()).add(p);
        priceList.add(p.getUnitPrice());
        checkLowStock(p);
        System.out.println("  [ADDED] " + p);
    }

    /** Look up a product by SKU — O(1) via HashMap. */
    public Optional<Product> findBySku(String sku) {
        return Optional.ofNullable(catalog.get(sku));
    }

    /** Restock a product; enqueues a log entry and updates quantity. */
    public void restock(String sku, int units) {
        findBySku(sku).ifPresentOrElse(p -> {
            p.restock(units);
            restockQueue.offer("RESTOCK | " + sku + " | +" + units + " units | New Qty: " + p.getQuantity());
            lowStockAlerts.remove(sku); // clear alert if restocked
            System.out.printf("  [RESTOCK] %s +%d units → qty now %d%n", sku, units, p.getQuantity());
        }, () -> System.out.println("  [ERROR] SKU not found: " + sku));
    }

    /** Sell units of a product and check low-stock after. */
    public void sell(String sku, int units) {
        findBySku(sku).ifPresentOrElse(p -> {
            p.sell(units);
            checkLowStock(p);
            System.out.printf("  [SOLD]  %s -%d units → qty now %d%n", sku, units, p.getQuantity());
        }, () -> System.out.println("  [ERROR] SKU not found: " + sku));
    }

    /** Flag item as low-stock if below threshold. */
    private void checkLowStock(Product p) {
        if (p.getQuantity() < LOW_STOCK_THRESHOLD) {
            lowStockAlerts.add(p.getSku());
        }
    }

    /** Total inventory value across all products. */
    public double computeTotalValue() {
        return catalog.values().stream()
                .mapToDouble(Stockable::getTotalValue)
                .sum();
    }

    /** Average unit price across all products. */
    public double computeAveragePrice() {
        return priceList.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);
    }

    /** Most expensive product by unit price. */
    public Optional<Product> findMostExpensive() {
        return catalog.values().stream()
                .max(Comparator.comparingDouble(Product::getUnitPrice));
    }

    /** Find all unique categories (TreeMap keys — already unique & sorted). */
    public Set<String> getUniqueCategories() {
        return byCategory.keySet(); // TreeMap keys are unique by definition
    }

   
    public List<String> findDuplicateNames() {
        Map<String, Long> nameCounts = catalog.values().stream()
                .collect(Collectors.groupingBy(Product::getName, Collectors.counting()));
        return nameCounts.entrySet().stream()
                .filter(e -> e.getValue() > 1)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    /** Products sorted by total stock value descending (most valuable first). */
    public List<Product> getProductsByValue() {
        return catalog.values().stream()
                .sorted(Comparator.comparingDouble(Stockable::getTotalValue).reversed())
                .collect(Collectors.toList());
    }



    @Override
    public void printReport() {
        String line = "=".repeat(72);
        System.out.println("\n" + line);
        System.out.println("         INVENTORY MANAGEMENT SYSTEM — FULL REPORT");
        System.out.println(line);

        // --- Full catalog ---
        System.out.println("\n[ ALL PRODUCTS — Insertion Order (LinkedHashMap) ]");
        System.out.printf("%-10s %-25s %-15s %-8s %s%n",
                "SKU", "Name", "Category", "Qty", "Unit Price");
        System.out.println("-".repeat(72));
        catalog.values().forEach(System.out::println);

        // --- By category (TreeMap = alphabetical) ---
        System.out.println("\n[ PRODUCTS BY CATEGORY — Alphabetical (TreeMap) ]");
        byCategory.forEach((cat, products) -> {
            System.out.println("  ▶ " + cat);
            products.forEach(p -> System.out.println("      " + p));
        });

        // --- Analytics ---
        System.out.println("\n[ ANALYTICS ]");
        System.out.printf("  Total Inventory Value   : $%,.2f%n", computeTotalValue());
        System.out.printf("  Average Unit Price      : $%.2f%n",  computeAveragePrice());
        findMostExpensive().ifPresent(p ->
                System.out.printf("  Most Expensive Product  : %s ($%.2f)%n", p.getName(), p.getUnitPrice()));
        System.out.println("  Unique Categories       : " + getUniqueCategories());

        // --- Duplicate names ---
        List<String> dupes = findDuplicateNames();
        if (dupes.isEmpty()) {
            System.out.println("  Duplicate Product Names : None detected ✓");
        } else {
            System.out.println("  Duplicate Product Names : " + dupes);
        }

        // --- Ranked by value ---
        System.out.println("\n[ PRODUCTS RANKED BY TOTAL STOCK VALUE (High → Low) ]");
        getProductsByValue().forEach(p ->
                System.out.printf("  %-25s → $%,.2f%n", p.getName(), p.getTotalValue()));

        // --- Low stock alerts (HashSet) ---
        System.out.println("\n[ LOW STOCK ALERTS — Threshold < " + LOW_STOCK_THRESHOLD + " units (HashSet) ]");
        if (lowStockAlerts.isEmpty()) {
            System.out.println("  All products adequately stocked ✓");
        } else {
            lowStockAlerts.forEach(sku ->
                    findBySku(sku).ifPresent(p ->
                            System.out.printf("  ⚠  %-10s %-25s — only %d unit(s) left%n",
                                    p.getSku(), p.getName(), p.getQuantity())));
        }

        // --- Restock log (Queue) ---
        System.out.println("\n[ RESTOCK ACTIVITY LOG — FIFO Queue (LinkedList) ]");
        if (restockQueue.isEmpty()) {
            System.out.println("  No restock activity recorded.");
        } else {
            restockQueue.forEach(entry -> System.out.println("  • " + entry));
        }

        System.out.println("\n" + line + "\n");
    }
}



public class InventoryCollections {

    public static void main(String[] args) {

        InventoryManager manager = new InventoryManager();

        System.out.println("\n=== Adding Products ===");
        manager.addProduct(new Product("SKU-001", "Basmati Rice 5kg",    "Grains",       50,  599.00));
        manager.addProduct(new Product("SKU-002", "Sunflower Oil 1L",    "Oils",         30,  180.00));
        manager.addProduct(new Product("SKU-003", "Organic Milk 1L",     "Dairy",        7,   75.00));   // low stock
        manager.addProduct(new Product("SKU-004", "Whole Wheat Flour 2kg","Grains",      40,  120.00));
        manager.addProduct(new Product("SKU-005", "Cheddar Cheese 200g", "Dairy",        5,   250.00));  // low stock
        manager.addProduct(new Product("SKU-006", "Laptop Stand",        "Electronics",  20, 1499.00));
        manager.addProduct(new Product("SKU-007", "USB-C Hub",           "Electronics",  15,  899.00));
        manager.addProduct(new Product("SKU-008", "Basmati Rice 5kg",    "Grains",       25,  599.00));  // duplicate name

        System.out.println("\n=== Selling Products ===");
        manager.sell("SKU-001", 45);   // drops to 5 → triggers low stock
        manager.sell("SKU-006", 3);

        System.out.println("\n=== Restocking Products ===");
        manager.restock("SKU-003", 50);  // clears low stock alert
        manager.restock("SKU-001", 100); // clears low stock alert

        // Print full report
        manager.printReport();
    }
}