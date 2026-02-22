
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;




class Item {
   private final int id;
   private final String name;
   private int quantity;
   private double price;


   public Item(int id, String name, int quantity, double price) {
       if (name == null || name.isBlank()) {
           throw new InvalidInventoryOperationException("Item name cannot be null or blank");
       }
       if (quantity < 0) {
           throw new InvalidInventoryOperationException("Quantity cannot be negative");
       }
       if (price < 0) {
           throw new InvalidInventoryOperationException("Price cannot be negative");
       }
       this.id = id;
       this.name = name;
       this.quantity = quantity;
       this.price = price;
   }


   public int getId() { return id; }


   public String getName() { return name; }


   public int getQuantity() { return quantity; }


   public double getPrice() { return price; }


   public void changeQuantity(int delta) {
       int newQty = this.quantity + delta;
       if (newQty < 0) {
           throw new InvalidInventoryOperationException(
                   "Resulting quantity cannot be negative. Current=" + quantity + ", delta=" + delta
           );
       }
       this.quantity = newQty;
   }


   public void setPrice(double newPrice) {
       if (newPrice < 0) {
           throw new InvalidInventoryOperationException("New price cannot be negative");
       }
       this.price = newPrice;
   }


   @Override
   public String toString() {
       return String.format("Item{id=%d, name='%s', qty=%d, price=%.2f}",
               id, name, quantity, price);
   }
}









// CHECKED: missing resource (e.g., item ID not found in inventory)
class ItemNotFoundException extends Exception {
   public ItemNotFoundException(String message) {
       super(message);
   }
}




// UNCHECKED: logical/domain issues like invalid quantity, nulls, bad input, etc.
class InvalidInventoryOperationException extends RuntimeException {
   public InvalidInventoryOperationException(String message) {
       super(message);
   }
}








class Inventory {
   private final Map<Integer, Item> items = new HashMap<>();


   public void loadFromFile(Path filePath) throws IOException {
       // Basic existence check (I/O related)
       if (!Files.exists(filePath)) {
           throw new IOException("Inventory file not found: " + filePath.toAbsolutePath());
       }


       try (BufferedReader br = Files.newBufferedReader(filePath)) {
           String line;
           int lineNo = 0;
           while ((line = br.readLine()) != null) {
               lineNo++;
               line = line.trim();
               if (line.isEmpty()) continue; // skip blank lines


               String[] parts = line.split(",");
               if (parts.length != 4) {
                   throw new InvalidInventoryOperationException(
                           "Invalid line format at " + filePath.getFileName() +
                           ", line " + lineNo + ": " + line
                   );
               }


               try {
                   int id = Integer.parseInt(parts[0].trim());
                   String name = parts[1].trim();
                   int qty = Integer.parseInt(parts[2].trim());
                   double price = Double.parseDouble(parts[3].trim());


                   Item item = new Item(id, name, qty, price);
                   addItem(item);
               } catch (NumberFormatException e) {
                   // Logical/data issue, not low-level I/O
                   throw new InvalidInventoryOperationException(
                           "Number format error in " + filePath.getFileName() +
                           " at line " + lineNo + ": " + line
                   );
               }
           }
       }
   }


  
   public void loadFromDirectory(Path dir) throws IOException {
       // Use java.io.File and java.nio.file.Path/Files together
       File dirAsFile = dir.toFile();
       if (!dirAsFile.exists() || !dirAsFile.isDirectory()) {
           throw new IOException("Data directory not found: " + dirAsFile.getAbsolutePath());
       }


       System.out.println("Scanning directory: " + dirAsFile.getAbsolutePath());


       // 1) List and load all .txt files using Files.walk()
       try (Stream<Path> pathStream = Files.walk(dir)) {
           pathStream
                   .filter(Files::isRegularFile)
                   .filter(p -> p.toString().endsWith(".txt"))
                   .forEach(p -> {
                       try {
                           System.out.println("  Loading file: " + p.getFileName());
                           loadFromFile(p); // may throw IOException (file reading) or RuntimeException (data)
                       } catch (IOException e) {
                           // I/O problem: we log it and continue with other files
                           System.err.println("  [I/O ERROR] Failed to load " + p + ": " + e.getMessage());
                       } catch (RuntimeException e) {
                           // Logical/data issue: invalid line, bad values, etc.
                           System.err.println("  [LOGICAL ERROR] Invalid data in " + p + ": " + e.getMessage());
                       }
                   });
       } catch (IOException e) {
           // Traversal error (e.g., permission problem)
           throw new IOException("Error while traversing directory: " + dir.toAbsolutePath(), e);
       }


       // 2) Use Files.find() to search for a specific important domain file (e.g., "priority_inventory.txt")
       try (Stream<Path> found = Files.find(
               dir,
               3, // search depth
               (path, attrs) -> attrs.isRegularFile()
                       && path.getFileName().toString().equalsIgnoreCase("priority_inventory.txt"))) {


           Optional<Path> special = found.findFirst();
           if (special.isPresent()) {
               Path p = special.get();
               System.out.println("  Found special file via Files.find(): " + p.getFileName());
               try {
                   loadFromFile(p);
               } catch (IOException e) {
                   System.err.println("  [I/O ERROR] Failed to load special file " + p + ": " + e.getMessage());
               } catch (RuntimeException e) {
                   System.err.println("  [LOGICAL ERROR] Invalid data in special file " + p + ": " + e.getMessage());
               }
           } else {
               System.out.println("  No 'priority_inventory.txt' file found (Files.find example).");
           }
       } catch (IOException e) {
           throw new IOException("Error while searching for special files in: " + dir.toAbsolutePath(), e);
       }
   }


   public void addItem(Item item) {
       if (item == null) {
           throw new InvalidInventoryOperationException("Cannot add null item");
       }
       if (items.containsKey(item.getId())) {
           throw new InvalidInventoryOperationException(
                   "Duplicate item ID: " + item.getId()
           );
       }
       items.put(item.getId(), item);
   }


   public Item getItemById(int id) throws ItemNotFoundException {
       Item item = items.get(id);
       if (item == null) {
           throw new ItemNotFoundException("No item found with id: " + id);
       }
       return item;
   }


   public void updateQuantity(int id, int delta) throws ItemNotFoundException {
       Item item = getItemById(id);  // may throw ItemNotFoundException (checked)
       item.changeQuantity(delta);   // may throw InvalidInventoryOperationException (unchecked)
   }


   public void updatePrice(int id, double newPrice) throws ItemNotFoundException {
       Item item = getItemById(id);
       item.setPrice(newPrice);
   }


   public double calculateTotalValue() {
       double total = 0.0;
       for (Item item : items.values()) {
           total += item.getQuantity() * item.getPrice();
       }
       return total;
   }


   public void printAllItems() {
       if (items.isEmpty()) {
           System.out.println("Inventory is empty.");
       } else {
           System.out.println("----- Current Inventory -----");
           for (Item item : items.values()) {
               System.out.println(item);
           }
       }
   }
}









public class InventoryApp {


   public static void main(String[] args) {
       Inventory inventory = new Inventory();


      
       Path dataDir = Paths.get("inventory_data");


       try {
           inventory.loadFromDirectory(dataDir);
           System.out.println("\nInventory loaded successfully from directory.\n");
       } catch (IOException e) {
           // CHECKED: I/O problems (missing directory, read issues, traversal errors)
           System.err.println("Failed to load inventory (I/O issue): " + e.getMessage());
           // In a real application, we might log and exit safely
           return;
       } catch (RuntimeException e) {
           // UNCHECKED: logical problems during loading (invalid data, duplicates, etc.)
           System.err.println("Failed to load inventory (logical issue): " + e.getMessage());
           return;
       }


       inventory.printAllItems();


       // 2. Interact with user & demonstrate RuntimeException handling for bad input.
       try (Scanner scanner = new Scanner(System.in)) {
           System.out.print("\nEnter item ID to update quantity: ");
           String idStr = scanner.nextLine();


           int id;
           try {
               id = Integer.parseInt(idStr.trim());
           } catch (NumberFormatException e) {
               // Incorrect user input -> logical error, treated as runtime
               throw new InvalidInventoryOperationException("Item ID must be an integer.");
           }


           System.out.print("Enter quantity change (e.g., +10 or -5): ");
           String deltaStr = scanner.nextLine();


           int delta;
           try {
               delta = Integer.parseInt(deltaStr.trim());
           } catch (NumberFormatException e) {
               throw new InvalidInventoryOperationException("Quantity change must be an integer.");
           }


           try {
               inventory.updateQuantity(id, delta);
               System.out.println("Quantity updated successfully.\n");
           } catch (ItemNotFoundException e) {
               // CHECKED: missing resource (item not found)
               System.err.println("Error: " + e.getMessage());
           } catch (InvalidInventoryOperationException e) {
               // UNCHECKED: logical problem (e.g., negative resulting quantity)
               System.err.println("Invalid operation: " + e.getMessage());
           }


           inventory.printAllItems();
           System.out.printf("\nTotal inventory value: %.2f\n", inventory.calculateTotalValue());
       } catch (InvalidInventoryOperationException e) {
           // Catching any unchecked logical issues from input or operations
           System.err.println("Application error: " + e.getMessage());
       }


       System.out.println("\nApplication finished safely.");
   }
}







