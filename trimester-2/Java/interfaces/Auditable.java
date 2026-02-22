package interfaces;

import java.util.List;

/** Items that record significant operations for audits. */
public interface Auditable {
    List<String> getAuditLog();
}