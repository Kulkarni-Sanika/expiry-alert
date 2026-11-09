package com.expiryalert.entity;

/**
 * The four expiry states of a document.
 *
 * RULE (days remaining = expiry date minus today's date):
 *   below 0       -> EXPIRED        (expiry date is before today)
 *   0 to 7        -> EXPIRING_SOON  (today counts as "expiring soon", NOT expired)
 *   8 to 30       -> UPCOMING
 *   more than 30  -> VALID
 *
 * A document that expires today is still usable until the end of today,
 * so it becomes EXPIRED only from tomorrow onwards.
 */
public enum ExpiryStatus {

    EXPIRED("Expired", "expired", "bi-x-octagon-fill"),
    EXPIRING_SOON("Expiring Soon", "soon", "bi-exclamation-triangle-fill"),
    UPCOMING("Upcoming", "upcoming", "bi-clock-history"),
    VALID("Valid", "valid", "bi-check-circle-fill");

    private final String label;
    private final String cssClass;
    private final String icon;

    ExpiryStatus(String label, String cssClass, String icon) {
        this.label = label;
        this.cssClass = cssClass;
        this.icon = icon;
    }

    public String getLabel() {
        return label;
    }

    public String getCssClass() {
        return cssClass;
    }

    public String getIcon() {
        return icon;
    }

    /** Converts "days remaining" into a status. This is the single place where the rule lives. */
    public static ExpiryStatus fromDaysRemaining(long daysRemaining) {
        if (daysRemaining < 0) {
            return EXPIRED;
        }
        if (daysRemaining <= 7) {
            return EXPIRING_SOON;
        }
        if (daysRemaining <= 30) {
            return UPCOMING;
        }
        return VALID;
    }

    /** Reads a status from a request parameter such as "EXPIRED". Returns null for blank or unknown text. */
    public static ExpiryStatus fromName(String name) {
        if (name == null || name.isBlank()) {
            return null;
        }
        try {
            return ExpiryStatus.valueOf(name.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}
