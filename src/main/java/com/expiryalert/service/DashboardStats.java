package com.expiryalert.service;

/**
 * Numbers shown on the dashboard cards.
 * A "record" is a short way to write a class that only holds data.
 */
public record DashboardStats(long total, long expired, long expiringSoon, long upcoming, long valid) {

    /** Everything that expires from today up to 30 days ahead (includes the 7-day group). */
    public long expiringWithin30Days() {
        return expiringSoon + upcoming;
    }

    /** Share of the total as a whole percentage, used by the progress bar. */
    public int percent(long part) {
        if (total == 0) {
            return 0;
        }
        return (int) Math.round(part * 100.0 / total);
    }
}
