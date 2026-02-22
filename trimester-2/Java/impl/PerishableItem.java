package impl;

import java.time.LocalDate;

/** Perishable inventory with an expiry date and custom reservation rule. */
public class PerishableItem extends BaseStockItem {

    private final LocalDate expiryDate; // encapsulated

    public PerishableItem(String sku, String name, String location,
            double baseUnitPrice, int initialOnHand, LocalDate expiryDate) {
        super(sku, name, location, baseUnitPrice, initialOnHand); // uses protected constructor from base class
        this.expiryDate = expiryDate;
        addAudit("SET EXPIRY " + expiryDate);
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    /** Disallow new reservations if the item is expired today or earlier. */
    @Override
    public boolean reserve(int amount) {
        if (expiryDate.isBefore(LocalDate.now()) || expiryDate.isEqual(LocalDate.now())) {
            return false;
        }
        return super.reserve(amount);
    }
}