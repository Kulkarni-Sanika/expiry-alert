package com.expiryalert.repository;

import com.expiryalert.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for document persistence operations.
 * Spring Data JPA automatically generates the implementation
 * and derives database queries from method names.
 */
@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    List<Document> findAllByOrderByExpiryDateAsc();

    List<Document> findAllByOrderByExpiryDateDesc();

    List<Document> findByExpiryDateLessThanEqualOrderByExpiryDateAsc(LocalDate date);

    long countByExpiryDateBefore(LocalDate date);

    long countByExpiryDateBetween(LocalDate from, LocalDate to);
}
