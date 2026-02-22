package impl;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;

/**
 * Items tracked by serial number. Reservation consumes specific serials (FIFO).
 */
public class SerializedItem extends BaseStockItem {

    private final Deque<String> serials = new ArrayDeque<>();

    public SerializedItem(String sku, String name, String location,
            double baseUnitPrice, int initialOnHand, Iterable<String> initialSerials) {
        super(sku, name, location, baseUnitPrice, initialOnHand);
        if (initialSerials != null) {
            for (String s : initialSerials) {
                if (s != null && !s.isBlank())
                    serials.addLast(s);
            }
        }
        addAudit("SERIALS init count=" + serials.size());
    }

    public String peekNextSerial() {
        return serials.peekFirst();
    }

    /** Ship will also pop serials, ensuring consistency with quantity movements. */
    @Override
    public boolean shipReserved(int amount) {
        if (!super.shipReserved(amount))
            return false;
        for (int i = 0; i < amount; i++) {
            serials.pollFirst();
        }
        addAudit("SERIALS shipped=" + amount + ", remaining=" + serials.size());
        return true;
    }

    /** Add a new unit with its serial when restocking serialized items. */
    public void restockWithSerial(String serial) {
        Objects.requireNonNull(serial, "serial");
        restock(1);
        serials.addLast(serial);
        addAudit("SERIAL added=" + serial);
    }
}