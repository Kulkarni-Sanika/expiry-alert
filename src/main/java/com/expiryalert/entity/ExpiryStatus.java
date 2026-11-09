package com.expiryalert.entity;
/**
 * Defines the four possible expiry states of a document.
 *
 * Expiry status is determined by the number of days remaining:
 * - Negative days: EXPIRED
 * - 0–7 days: EXPIRING_SOON
 * - 8–30 days: UPCOMING
 * - More than 30 days: VALID
 *
 * Documents expiring today are considered EXPIRING_SOON
 * and become EXPIRED from the following day.
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
