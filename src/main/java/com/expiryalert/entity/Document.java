package com.expiryalert.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * One tracked document (licence, passport, insurance, ...).
 * Each object of this class is stored as one row in the "documents" table.
 */
@Entity
@Table(name = "documents")
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Document name is required")
    @Size(max = 100, message = "Name must be 100 characters or fewer")
    @Column(nullable = false, length = 100)
    private String name;

    @NotBlank(message = "Please choose a category")
    @Size(max = 50, message = "Category must be 50 characters or fewer")
    @Column(nullable = false, length = 50)
    private String category;

    @Size(max = 50, message = "Document number must be 50 characters or fewer")
    @Column(length = 50)
    private String documentNumber;

    @PastOrPresent(message = "Issue date cannot be in the future")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate issueDate;

    @NotNull(message = "Expiry date is required")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(nullable = false)
    private LocalDate expiryDate;

    @Size(max = 500, message = "Notes must be 500 characters or fewer")
    @Column(length = 500)
    private String notes;

    /** JPA needs a no-argument constructor. */
    public Document() {
    }

    public Document(String name, String category, String documentNumber,
                    LocalDate issueDate, LocalDate expiryDate, String notes) {
        this.name = name;
        this.category = category;
        this.documentNumber = documentNumber;
        this.issueDate = issueDate;
        this.expiryDate = expiryDate;
        this.notes = notes;
    }

    // ---------- Calculated values (not stored in the database) ----------

    /** Days from today until expiry. Negative when already expired, 0 when it expires today. */
    @Transient
    public long getDaysRemaining() {
        if (expiryDate == null) {
            return 0;
        }
        return ChronoUnit.DAYS.between(LocalDate.now(), expiryDate);
    }

    @Transient
    public ExpiryStatus getStatus() {
        return ExpiryStatus.fromDaysRemaining(getDaysRemaining());
    }

    /** Friendly sentence for the UI, for example "Expires in 5 days". */
    @Transient
    public String getExpiryMessage() {
        long days = getDaysRemaining();
        if (days < 0) {
            long ago = Math.abs(days);
            return "Expired " + ago + (ago == 1 ? " day ago" : " days ago");
        }
        if (days == 0) {
            return "Expires today";
        }
        if (days == 1) {
            return "Expires tomorrow";
        }
        return "Expires in " + days + " days";
    }

    // ---------- Getters and setters ----------

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
