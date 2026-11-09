package com.expiryalert.repository;

import com.expiryalert.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Database access. We only declare an interface; Spring Data JPA writes the
 * implementation for us. It also reads each method NAME and builds the SQL,
 * for example countByExpiryDateBefore(date) becomes
 * "select count(*) from documents where expiry_date < date".
 */
@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    List<Document> findAllByOrderByExpiryDateAsc();

    List<Document> findAllByOrderByExpiryDateDesc();

    /** Documents expiring on or before the given date, nearest first (includes expired ones). */
    List<Document> findByExpiryDateLessThanEqualOrderByExpiryDateAsc(LocalDate date);

    long countByExpiryDateBefore(LocalDate date);

    /** Both dates are inclusive. */
    long countByExpiryDateBetween(LocalDate from, LocalDate to);
}
