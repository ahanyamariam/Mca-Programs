package interfaces;

/**
 * Represents an item tracked in inventory.
 * Interfaces are public as requested.
 */
public interface StockItem {
    String getSku();

    String getName();

    String getLocation();

    /** Current on-hand units (excluding reservations). */
    int getQuantityOnHand();

    /** Units reserved but not yet shipped/picked. */
    int getQuantityReserved();

    /** Read-only access to base unit price for pricing strategies. */
    double getBaseUnitPrice();

    /** Add units to inventory. */
    void restock(int amount);

    /** Attempt to reserve units for an order; returns true if successful. */
    boolean reserve(int amount);

    /** Release previously reserved units. */
    void release(int amount);

    /** Ship (commit) reserved units, removing them from inventory. */
    boolean shipReserved(int amount);

    /** Convenience: total available for new reservations. */
    default int getAvailable() {
        return Math.max(0, getQuantityOnHand() - getQuantityReserved());
    }
}