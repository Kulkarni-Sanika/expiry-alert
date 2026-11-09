package com.expiryalert.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** Checks the calculated fields of a Document using dates relative to today. */
class DocumentTest {

    private Document documentExpiringOn(LocalDate expiryDate) {
        return new Document("Test", "Other", null, null, expiryDate, null);
    }

    @Test
    void expiryTodayIsExpiringSoon() {
        Document document = documentExpiringOn(LocalDate.now());
        assertEquals(0, document.getDaysRemaining());
        assertEquals(ExpiryStatus.EXPIRING_SOON, document.getStatus());
        assertEquals("Expires today", document.getExpiryMessage());
    }

    @Test
    void expiredYesterday() {
        Document document = documentExpiringOn(LocalDate.now().minusDays(1));
        assertEquals(ExpiryStatus.EXPIRED, document.getStatus());
        assertEquals("Expired 1 day ago", document.getExpiryMessage());
    }

    @Test
    void expiresTomorrow() {
        Document document = documentExpiringOn(LocalDate.now().plusDays(1));
        assertEquals("Expires tomorrow", document.getExpiryMessage());
    }

    @Test
    void expiresIn45DaysIsValid() {
        Document document = documentExpiringOn(LocalDate.now().plusDays(45));
        assertEquals(ExpiryStatus.VALID, document.getStatus());
        assertEquals("Expires in 45 days", document.getExpiryMessage());
    }
}
