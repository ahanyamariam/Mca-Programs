package impl;

import interfaces.PricingStrategy;
import interfaces.StockItem;

/** Baseline pricing: simple unit price × quantity. */
public class FlatRatePricing implements PricingStrategy {

    @Override
    public double price(StockItem item, int units) {
        if (units <= 0)
            return 0.0;
        return item.getBaseUnitPrice() * units;
    }

    @Override
    public String name() {
        return "Flat Rate";
    }
}