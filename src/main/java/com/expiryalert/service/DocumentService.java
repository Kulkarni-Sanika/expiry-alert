package com.expiryalert.service;

import com.expiryalert.entity.Document;
import com.expiryalert.entity.ExpiryStatus;
import com.expiryalert.repository.DocumentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

/**
 * Business logic lives here, so controllers stay thin.
 */
@Service
public class DocumentService {

    /** Categories offered in the form and the filter. "Other" covers custom documents. */
    public static final List<String> CATEGORIES = List.of(
            "Driving Licence",
            "Vehicle Insurance",
            "Passport",
            "Government ID",
            "Certificate",
            "Warranty",
            "Other");

    private final DocumentRepository documentRepository;

    // Spring passes the repository in automatically (constructor injection).
    public DocumentService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    public List<String> getCategories() {
        return CATEGORIES;
    }

    public long count() {
        return documentRepository.count();
    }

    /**
     * Search by name, filter by category and status, and sort by expiry date.
     * @param sort "farthest" for latest expiry first; anything else means nearest expiry first
     */
    public List<Document> search(String keyword, String category, String status, String sort) {
        List<Document> documents = "farthest".equals(sort)
                ? documentRepository.findAllByOrderByExpiryDateDesc()
                : documentRepository.findAllByOrderByExpiryDateAsc();

        String text = keyword == null ? "" : keyword.trim().toLowerCase();
        ExpiryStatus wantedStatus = ExpiryStatus.fromName(status);

        return documents.stream()
                .filter(d -> text.isEmpty() || d.getName().toLowerCase().contains(text))
                .filter(d -> category == null || category.isBlank() || category.equals(d.getCategory()))
                .filter(d -> wantedStatus == null || d.getStatus() == wantedStatus)
                .toList();
    }

    /** Finds one document or answers with HTTP 404 (shows our "not found" page). */
    public Document findById(Long id) {
        return documentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Document not found"));
    }

    public Document save(Document document) {
        document.setName(clean(document.getName()));
        document.setDocumentNumber(clean(document.getDocumentNumber()));
        document.setNotes(clean(document.getNotes()));
        return documentRepository.save(document);
    }

    public void deleteById(Long id) {
        findById(id); // makes sure it exists, otherwise 404
        documentRepository.deleteById(id);
    }

    public DashboardStats getDashboardStats() {
        LocalDate today = LocalDate.now();
        long total = documentRepository.count();
        long expired = documentRepository.countByExpiryDateBefore(today);
        long soon = documentRepository.countByExpiryDateBetween(today, today.plusDays(7));
        long upcoming = documentRepository.countByExpiryDateBetween(today.plusDays(8), today.plusDays(30));
        long valid = total - expired - soon - upcoming;
        return new DashboardStats(total, expired, soon, upcoming, valid);
    }

    /** Documents that need attention: already expired or expiring within 30 days. */
    public List<Document> getAlertDocuments() {
        return documentRepository.findByExpiryDateLessThanEqualOrderByExpiryDateAsc(LocalDate.now().plusDays(30));
    }

    /** Trims text and turns blank text into null. */
    private String clean(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }
        return text.trim();
    }
}
