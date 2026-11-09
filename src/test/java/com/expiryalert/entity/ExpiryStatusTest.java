package com.expiryalert.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/** Checks the boundaries of the expiry rule. No Spring needed, so it runs very fast. */
class ExpiryStatusTest {

    @Test
    void yesterdayIsExpired() {
        assertEquals(ExpiryStatus.EXPIRED, ExpiryStatus.fromDaysRemaining(-1));
    }

    @Test
    void todayIsExpiringSoonNotExpired() {
        assertEquals(ExpiryStatus.EXPIRING_SOON, ExpiryStatus.fromDaysRemaining(0));
    }

    @Test
    void sevenDaysIsStillExpiringSoon() {
        assertEquals(ExpiryStatus.EXPIRING_SOON, ExpiryStatus.fromDaysRemaining(7));
    }

    @Test
    void eightToThirtyDaysIsUpcoming() {
        assertEquals(ExpiryStatus.UPCOMING, ExpiryStatus.fromDaysRemaining(8));
        assertEquals(ExpiryStatus.UPCOMING, ExpiryStatus.fromDaysRemaining(30));
    }

    @Test
    void moreThanThirtyDaysIsValid() {
        assertEquals(ExpiryStatus.VALID, ExpiryStatus.fromDaysRemaining(31));
        assertEquals(ExpiryStatus.VALID, ExpiryStatus.fromDaysRemaining(1000));
    }

    @Test
    void fromNameHandlesBlankAndUnknownText() {
        assertNull(ExpiryStatus.fromName(null));
        assertNull(ExpiryStatus.fromName(""));
        assertNull(ExpiryStatus.fromName("nonsense"));
        assertEquals(ExpiryStatus.EXPIRED, ExpiryStatus.fromName("expired"));
    }
}
