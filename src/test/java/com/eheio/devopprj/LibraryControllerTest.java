package com.eheio.devopprj.controller;

import com.eheio.devopprj.dto.BookAddDTO;
import com.eheio.devopprj.model.Book;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LibraryController.class)
public class LibraryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        LibraryController.books = new ArrayList<>(); // Reset books before each test
    }

    @Test
    void testGetBooks_NoContent() throws Exception {
        mockMvc.perform(get("/api/v1/library"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetBooks_WithBooks() throws Exception {
        // Add a book to the collection
        Book book = new Book();
        book.setId((double)1);
        book.setTitle("Test Book");
        book.setAuthor("Test Author");
        LibraryController.books.add(book);

        mockMvc.perform(get("/api/v1/library"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is("Test Book")))
                .andExpect(jsonPath("$[0].author", is("Test Author")));
    }

    @Test
    void testAddBook() throws Exception {
        BookAddDTO bookAddDTO = new BookAddDTO();
        bookAddDTO.setTitle("New Book");
        bookAddDTO.setAuthor("New Author");

        mockMvc.perform(post("/api/v1/library")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookAddDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title", is("New Book")))
                .andExpect(jsonPath("$.author", is("New Author")));
    }
}