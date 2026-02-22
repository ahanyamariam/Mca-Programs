
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;


// Main demo class
public class InventoryDomainAnalyzer {


   public static void main(String[] args) {
       Scanner scanner = new Scanner(System.in);


       System.out.println("=== Inventory Management System - Domain Data Analyzer ===");
       System.out.println("You will enter data for your inventory.");
       System.out.println();


       // -------------------------
       // 1. USER INPUT FOR DATA
       // -------------------------
       System.out.print("Enter number of inventory items: ");
       int itemCount = readPositiveInt(scanner);


       // DATASET 1: Integer (Units in stock per item)
       DomainDataSet<Integer> stockLevels =
               new DomainDataSet<>("Warehouse - Units in Stock");


       // DATASET 2: Double (Inventory value per item)
       DomainDataSet<Double> stockValues =
               new DomainDataSet<>("Warehouse - Inventory Value per Item (USD)");


       System.out.println();
       System.out.println("Enter data for each item:");


       for (int i = 1; i <= itemCount; i++) {
           System.out.println("Item " + i + ":");


           System.out.print("  Units in stock (integer): ");
           int units = readNonNegativeInt(scanner);
           stockLevels.addReading(units);


           System.out.print("  Inventory value for this item (double, e.g., 1234.56): ");
           double value = readNonNegativeDouble(scanner);
           stockValues.addReading(value);


           System.out.println();
       }


       // Thresholds from user
       System.out.print("Enter low-stock threshold (items below this are considered LOW stock): ");
       int lowStockThreshold = readNonNegativeInt(scanner);


       System.out.print("Enter high-value threshold (items above this value are HIGH value): ");
       double highValueThreshold = readNonNegativeDouble(scanner);


       // -------------------------
       // 2. DEFINE ANALYTICS USING LAMBDAS
       // -------------------------


       // Integer dataset analytics (stock levels)
       NumericOperation<Integer, Integer> totalUnits = list ->
               list.stream().mapToInt(Number::intValue).sum();


       NumericOperation<Integer, Double> averageUnits = list ->
               list.isEmpty()
                       ? 0.0
                       : list.stream().mapToInt(Number::intValue).average().orElse(0.0);


       NumericOperation<Integer, Long> lowStockCount = list ->
               list.stream()
                       .filter(q -> q.intValue() < lowStockThreshold)
                       .count();


       // Comparator: safer stock = higher units
       DomainComparator<Integer> higherStockIsSafer =
               (a, b) -> Integer.compare(a, b);


       // Double dataset analytics (stock values)
       NumericOperation<Double, Double> totalInventoryValue = list ->
               list.stream().mapToDouble(Number::doubleValue).sum();


       NumericOperation<Double, Double> averageInventoryValue = list ->
               list.isEmpty()
                       ? 0.0
                       : list.stream().mapToDouble(Number::doubleValue).average().orElse(0.0);


       NumericOperation<Double, Long> highValueItemsCount = list ->
               list.stream()
                       .filter(v -> v.doubleValue() > highValueThreshold)
                       .count();


       // Comparator: more valuable item stock is "higher"
       DomainComparator<Double> higherValueIsMoreValuable =
               (a, b) -> Double.compare(a.doubleValue(), b.doubleValue());


       // -------------------------
       // 3. RUN ANALYTICS VIA GENERIC METHOD analyze()
       // -------------------------
       int totalUnitsResult = stockLevels.analyze(totalUnits);
       double averageUnitsResult = stockLevels.analyze(averageUnits);
       long lowStockCountResult = stockLevels.analyze(lowStockCount);
       Integer safestStockLevel = stockLevels.bestValue(higherStockIsSafer);


       double totalInventoryValueResult = stockValues.analyze(totalInventoryValue);
       double averageInventoryValueResult = stockValues.analyze(averageInventoryValue);
       long highValueItemsCountResult = stockValues.analyze(highValueItemsCount);
       Double mostValuableItemStock = stockValues.bestValue(higherValueIsMoreValuable);


       // -------------------------
       // 4. OUTPUT RESULTS (DOMAIN-SPECIFIC)
       // -------------------------
       System.out.println();
       System.out.println("=== ANALYSIS RESULTS ===");


       System.out.println();
       System.out.println("Dataset 1: " + stockLevels.getName());
       System.out.println("  Total units in stock: " + totalUnitsResult);
       System.out.printf("  Average units per item: %.2f%n", averageUnitsResult);
       System.out.println("  Number of low-stock items (< " + lowStockThreshold + " units): "
               + lowStockCountResult);
       if (safestStockLevel != null) {
           System.out.println("  Safest stock level (highest units): "
                   + safestStockLevel + " units");
       } else {
           System.out.println("  No stock data available.");
       }


       System.out.println();
       System.out.println("Dataset 2: " + stockValues.getName());
       System.out.printf("  Total inventory value: $%.2f%n", totalInventoryValueResult);
       System.out.printf("  Average inventory value per item: $%.2f%n", averageInventoryValueResult);
       System.out.println("  Number of high-value items (>" + highValueThreshold + "): "
               + highValueItemsCountResult);
       if (mostValuableItemStock != null) {
           System.out.printf("  Most valuable item stock: $%.2f%n", mostValuableItemStock);
       } else {
           System.out.println("  No inventory value data available.");
       }


       scanner.close();
   }


   // -----------------------------------------------------------
   // Helper methods for safe user input
   // -----------------------------------------------------------
   private static int readPositiveInt(Scanner scanner) {
       while (true) {
           try {
               int value = Integer.parseInt(scanner.nextLine().trim());
               if (value <= 0) {
                   System.out.print("  Please enter a positive integer: ");
               } else {
                   return value;
               }
           } catch (NumberFormatException e) {
               System.out.print("  Invalid number. Please enter a positive integer: ");
           }
       }
   }


   private static int readNonNegativeInt(Scanner scanner) {
       while (true) {
           try {
               int value = Integer.parseInt(scanner.nextLine().trim());
               if (value < 0) {
                   System.out.print("  Please enter a non-negative integer: ");
               } else {
                   return value;
               }
           } catch (NumberFormatException e) {
               System.out.print("  Invalid number. Please enter a non-negative integer: ");
           }
       }
   }


   private static double readNonNegativeDouble(Scanner scanner) {
       while (true) {
           try {
               double value = Double.parseDouble(scanner.nextLine().trim());
               if (value < 0) {
                   System.out.print("  Please enter a non-negative double: ");
               } else {
                   return value;
               }
           } catch (NumberFormatException e) {
               System.out.print("  Invalid number. Please enter a non-negative double: ");
           }
       }
   }


   // -----------------------------------------------------------
   // Generic interface to compare / evaluate two numeric values
   // -----------------------------------------------------------
   @FunctionalInterface
   interface DomainComparator<T extends Number> {
       /**
        * @return positive if a "better" than b (domain-specific),
        *         0 if equal, negative if worse.
        */
       int compare(T a, T b);
   }


   // -----------------------------------------------------------
   // Functional interface for numeric operations (sum, avg, etc.)
   // -----------------------------------------------------------
   @FunctionalInterface
   interface NumericOperation<T extends Number, R> {
       R apply(List<T> data);
   }


   // -----------------------------------------------------------
   // Generic class with bounded type parameter: T extends Number
   // -----------------------------------------------------------
   static class DomainDataSet<T extends Number> {
       private final String name;
       private final List<T> readings = new ArrayList<>();


       public DomainDataSet(String name) {
           this.name = name;
       }


       public String getName() {
           return name;
       }


       public void addReading(T value) {
           readings.add(value);
       }


       public void addAll(List<? extends T> values) {
           readings.addAll(values);
       }


       public List<T> getReadings() {
           return Collections.unmodifiableList(readings);
       }


       /**
        * Generic method that accepts a functional interface to perform computations.
        */
       public <R> R analyze(NumericOperation<T, R> operation) {
           return operation.apply(getReadings());
       }


       /**
        * Uses the generic DomainComparator to find the "best" value
        * in this dataset according to the domain-specific comparison rule.
        */
       public T bestValue(DomainComparator<? super T> comparator) {
           if (readings.isEmpty()) {
               return null;
           }
           T best = readings.get(0);
           for (T value : readings) {
               if (comparator.compare(value, best) > 0) {
                   best = value;
               }
           }
           return best;
       }
   }
}







