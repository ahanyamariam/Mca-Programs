package impl;

import interfaces.PricingStrategy;
import interfaces.StockItem;
import java.time.LocalDate;

/**
 * Clearance pricing: if a PerishableItem is near expiry, apply a markdown.
 * For non-perishables, behaves like flat pricing.
 */
public class ClearancePricing implements PricingStrategy {

    private final int daysWindow;
    private final double markdown; // e.g., 0.3 = 30% off

    public ClearancePricing(int daysWindow, double markdown) {
        if (daysWindow <= 0)
            throw new IllegalArgumentException("daysWindow must be > 0");
        if (markdown < 0 || markdown >= 1)
            throw new IllegalArgumentException("markdown must be in [0,1)");
        this.daysWindow = daysWindow;
        this.markdown = markdown;
    }

    @Override
    public double price(StockItem item, int units) {
        if (units <= 0)
            return 0.0;
        double base = item.getBaseUnitPrice() * units;
        // If the item is perishable and near expiry, apply markdown.
        if (item instanceof PerishableItem p) {
            LocalDate today = LocalDate.now();
            if (!p.getExpiryDate().isBefore(today) &&
                    !p.getExpiryDate().isAfter(today.plusDays(daysWindow))) {
                base *= (1.0 - markdown);
            }
        }
        return base;
    }

    @Override
    public String name() {
        return String.format("Clearance (%d-day, -%.0f%%)", daysWindow, markdown * 100);
    }
}