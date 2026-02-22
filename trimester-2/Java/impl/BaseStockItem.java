package impl;

import interfaces.Auditable;
import interfaces.StockItem;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Base implementation shared by multiple item types.
 * Demonstrates encapsulation with private fields and a protected constructor
 * intended for subclass access.
 */
public class BaseStockItem implements StockItem, Auditable {

    private final String sku;
    private final String name;
    private final String location;
    private final double baseUnitPrice;
    private int onHand;
    private int reserved;
    private final List<String> audit = new ArrayList<>();

    /**
     * Protected constructor → accessible to subclasses, not to arbitrary callers.
     */
    protected BaseStockItem(String sku, String name, String location,
            double baseUnitPrice, int initialOnHand) {
        if (baseUnitPrice < 0)
            throw new IllegalArgumentException("Price cannot be negative");
        if (initialOnHand < 0)
            throw new IllegalArgumentException("Initial qty cannot be negative");
        this.sku = sku;
        this.name = name;
        this.location = location;
        this.baseUnitPrice = baseUnitPrice;
        this.onHand = initialOnHand;
        this.reserved = 0;
        addAudit("INIT onHand=" + initialOnHand);
    }

    /** Public factory for non-specialized items. */
    public static BaseStockItem of(String sku, String name, String location,
            double baseUnitPrice, int initialOnHand) {
        return new BaseStockItem(sku, name, location, baseUnitPrice, initialOnHand);
    }

    protected void addAudit(String message) {
        audit.add(LocalDateTime.now() + " — " + message);
    }

    // —— StockItem ——

    @Override
    public String getSku() {
        return sku;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getLocation() {
        return location;
    }

    @Override
    public double getBaseUnitPrice() {
        return baseUnitPrice;
    }

    @Override
    public int getQuantityOnHand() {
        return onHand;
    }

    @Override
    public int getQuantityReserved() {
        return reserved;
    }

    @Override
    public void restock(int amount) {
        if (amount <= 0)
            throw new IllegalArgumentException("Restock amount must be > 0");
        onHand += amount;
        addAudit("RESTOCK +" + amount + ", onHand=" + onHand);
    }

    @Override
    public boolean reserve(int amount) {
        if (amount <= 0)
            return false;
        if (amount > getAvailable())
            return false;
        reserved += amount;
        addAudit("RESERVE " + amount + ", reserved=" + reserved + ", available=" + getAvailable());
        return true;
    }

    @Override
    public void release(int amount) {
        if (amount <= 0)
            return;
        int delta = Math.min(amount, reserved);
        reserved -= delta;
        addAudit("RELEASE " + delta + ", reserved=" + reserved);
    }

    @Override
    public boolean shipReserved(int amount) {
        if (amount <= 0 || amount > reserved)
            return false;
        reserved -= amount;
        onHand -= amount;
        addAudit("SHIP " + amount + ", onHand=" + onHand + ", reserved=" + reserved);
        return true;
    }

    // —— Auditable ——

    @Override
    public List<String> getAuditLog() {
        return Collections.unmodifiableList(audit);
    }
}