import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


class Inventory {
    private final List<String> items = new ArrayList<>();
    private final int MAX_CAPACITY = 5;
    private boolean paused = false;


    // Add item (Producer)
    public synchronized void addItem(String item) throws InterruptedException {
        while (items.size() == MAX_CAPACITY || paused) {
            if (paused) {
                System.out.println("[AddThread] System paused... waiting to resume.");
            } else {
                System.out.println("[AddThread] Inventory full... waiting to add " + item);
            }
            wait();
        }
        items.add(item);
        System.out.println("[AddThread] Added: " + item + " | Current inventory: " + items);
        notifyAll();
    }


    // Remove item (Consumer)
    public synchronized void removeItem() throws InterruptedException {
        while (items.isEmpty() || paused) {
            if (paused) {
                System.out.println("[RemoveThread] System paused... waiting to resume.");
            } else {
                System.out.println("[RemoveThread] Inventory empty... waiting to remove.");
            }
            wait();
        }
        String removed = items.remove(0);
        System.out.println("[RemoveThread] Removed: " + removed + " | Current inventory: " + items);
        notifyAll();
    }


    // Pause inventory operations
    public synchronized void pauseSystem() {
        paused = true;
        System.out.println("[System] Inventory operations paused.");
    }


    // Resume inventory operations
    public synchronized void resumeSystem() {
        paused = false;
        System.out.println("[System] Inventory operations resumed.");
        notifyAll();
    }


    // Check inventory (for end summary)
    public synchronized void showInventory() {
        System.out.println("Final Inventory: " + items);
    }
}


// Thread subclass → Adds items
class AddItemThread extends Thread {
    private final Inventory inventory;
    private final String[] itemsToAdd;


    public AddItemThread(Inventory inventory, String[] itemsToAdd) {
        this.inventory = inventory;
        this.itemsToAdd = itemsToAdd;
    }


    public void run() {
        try {
            for (String item : itemsToAdd) {
                inventory.addItem(item);
                Thread.sleep(700); // simulate delay between adding
            }
        } catch (InterruptedException e) {
            System.out.println("[AddItemThread] Interrupted.");
        }
    }
}


// Runnable implementation → Removes items
class RemoveItemRunnable implements Runnable {
    private final Inventory inventory;


    public RemoveItemRunnable(Inventory inventory) {
        this.inventory = inventory;
    }


    public void run() {
        try {
            while (true) {
                inventory.removeItem();
                Thread.sleep(1000); // simulate delay between removals
            }
        } catch (InterruptedException e) {
            System.out.println("[RemoveItemRunnable] Interrupted.");
        }
    }
}


public class InventoryManagement {
    public static void main(String[] args) throws InterruptedException {
        Scanner sc = new Scanner(System.in);
        Inventory inventory = new Inventory();


        System.out.println("=== INVENTORY MANAGEMENT SYSTEM ===");
        System.out.print("Enter number of items to add: ");
        int numItems = sc.nextInt();


        System.out.print("Enter number of removal threads: ");
        int numRemovers = sc.nextInt();
        sc.nextLine(); // consume newline


        String[] items = new String[numItems];
        for (int i = 0; i < numItems; i++) {
            System.out.print("Enter item " + (i + 1) + ": ");
            items[i] = sc.nextLine();
        }


        // Create threads
        AddItemThread adder = new AddItemThread(inventory, items);
        List<Thread> removers = new ArrayList<>();
        for (int i = 0; i < numRemovers; i++) {
            removers.add(new Thread(new RemoveItemRunnable(inventory), "Remover-" + (i + 1)));
        }


        // Start threads
        adder.start();
        for (Thread t : removers) t.start();


        // Control simulation
        Thread.sleep(4000);
        inventory.pauseSystem(); // pause threads
        Thread.sleep(3000);
        inventory.resumeSystem(); // resume threads


        // Let it run a bit more
        Thread.sleep(6000);


        System.out.println("\n[Main] Stopping simulation...");
        System.exit(0);
        sc.close();
    }
    
}





