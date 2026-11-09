package com.expiryalert.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * Starts the whole application with an empty in-memory database and sends fake browser requests.
 * @Transactional undoes every database change after each test, so tests do not affect each other.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class DocumentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void dashboardShowsEmptyStateWhenNoDocuments() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard"))
                .andExpect(content().string(containsString("No documents yet")));
    }

    @Test
    void listShowsEmptyStateWhenNoDocuments() throws Exception {
        mockMvc.perform(get("/documents"))
                .andExpect(status().isOk())
                .andExpect(view().name("documents/list"))
                .andExpect(content().string(containsString("No documents yet")));
    }

    @Test
    void emptyFormShowsValidationErrors() throws Exception {
        mockMvc.perform(post("/documents")
                        .param("name", "")
                        .param("category", "")
                        .param("expiryDate", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("documents/form"))
                .andExpect(model().attributeHasFieldErrors("document", "name", "category", "expiryDate"));
    }

    @Test
    void expiryBeforeIssueDateIsRejected() throws Exception {
        mockMvc.perform(post("/documents")
                        .param("name", "Passport")
                        .param("category", "Passport")
                        .param("issueDate", LocalDate.now().minusDays(5).toString())
                        .param("expiryDate", LocalDate.now().minusDays(10).toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("documents/form"))
                .andExpect(model().attributeHasFieldErrors("document", "expiryDate"));
    }

    @Test
    void futureIssueDateIsRejected() throws Exception {
        mockMvc.perform(post("/documents")
                        .param("name", "Passport")
                        .param("category", "Passport")
                        .param("issueDate", LocalDate.now().plusDays(3).toString())
                        .param("expiryDate", LocalDate.now().plusDays(400).toString()))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("document", "issueDate"));
    }

    @Test
    void validDocumentIsSavedAndListed() throws Exception {
        mockMvc.perform(post("/documents")
                        .param("name", "Car Insurance")
                        .param("category", "Vehicle Insurance")
                        .param("expiryDate", LocalDate.now().toString()))
                .andExpect(status().is3xxRedirection());

        mockMvc.perform(get("/documents"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Car Insurance")))
                .andExpect(content().string(containsString("Expires today")));
    }

    @Test
    void searchWithNoMatchShowsFilterEmptyState() throws Exception {
        mockMvc.perform(post("/documents")
                .param("name", "Car Insurance")
                .param("category", "Vehicle Insurance")
                .param("expiryDate", LocalDate.now().plusDays(60).toString()));

        mockMvc.perform(get("/documents").param("q", "zzz"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("No documents match your filters")));
    }

    @Test
    void unknownDocumentGivesNotFound() throws Exception {
        mockMvc.perform(get("/documents/999999"))
                .andExpect(status().isNotFound());
    }
}
