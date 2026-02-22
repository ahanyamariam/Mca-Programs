package impl;

import interfaces.PricingStrategy;
import interfaces.StockItem;

/** Bulk discount when quantity meets/exceeds threshold. */
public class BulkDiscountStrategy implements PricingStrategy {

    private final int threshold;
    private final double discount; // e.g., 0.1 = 10%

    public BulkDiscountStrategy(int threshold, double discount) {
        if (threshold <= 0)
            throw new IllegalArgumentException("threshold must be > 0");
        if (discount < 0 || discount >= 1)
            throw new IllegalArgumentException("discount must be in [0,1)");
        this.threshold = threshold;
        this.discount = discount;
    }

    @Override
    public double price(StockItem item, int units) {
        if (units <= 0)
            return 0.0;
        double base = item.getBaseUnitPrice() * units;
        if (units >= threshold)
            base *= (1.0 - discount);
        return base;
    }

    @Override
    public String name() {
        return String.format("Bulk %d+ @ -%.0f%%", threshold, discount * 100);
    }
}