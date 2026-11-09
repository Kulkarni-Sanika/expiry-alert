package com.expiryalert.service;

import com.expiryalert.entity.Document;
import com.expiryalert.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Adds demo documents the first time the app runs, so the dashboard is not empty.
 * Turn it off with expiryalert.seed-sample-data=false in application.properties.
 */
@Component
public class SampleDataLoader implements CommandLineRunner {

    private final DocumentRepository documentRepository;
    private final boolean seedEnabled;

    public SampleDataLoader(DocumentRepository documentRepository,
                            @Value("${expiryalert.seed-sample-data:false}") boolean seedEnabled) {
        this.documentRepository = documentRepository;
        this.seedEnabled = seedEnabled;
    }

    @Override
    public void run(String... args) {
        if (!seedEnabled || documentRepository.count() > 0) {
            return;
        }
        LocalDate today = LocalDate.now();

        documentRepository.save(new Document("Car Insurance", "Vehicle Insurance", "POL-2291-884",
                today.minusYears(1).plusDays(2), today.plusDays(2), "Renew online before the weekend."));
        documentRepository.save(new Document("Driving Licence", "Driving Licence", "MH12 2019 0045512",
                today.minusYears(5), today, "Expires today, renewal appointment needed."));
        documentRepository.save(new Document("Passport", "Passport", "P1234567",
                today.minusYears(9), today.plusDays(21), "Start renewal paperwork early."));
        documentRepository.save(new Document("Laptop Warranty", "Warranty", "SN-LT-55120",
                today.minusYears(2), today.minusDays(10), "Warranty ended."));
        documentRepository.save(new Document("Health Insurance", "Other", "HI-77821",
                today.minusMonths(11), today.plusDays(5), null));
        documentRepository.save(new Document("Aadhaar Card", "Government ID", "XXXX XXXX 4321",
                null, today.plusDays(400), null));
        documentRepository.save(new Document("First Aid Certificate", "Certificate", "FA-2024-118",
                today.minusMonths(6), today.plusDays(120), "Valid for two years."));
    }
}
