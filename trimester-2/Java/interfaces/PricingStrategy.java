package interfaces;

/** Strategy for computing price given an item and quantity. */
public interface PricingStrategy {
    double price(StockItem item, int units);

    String name();
}