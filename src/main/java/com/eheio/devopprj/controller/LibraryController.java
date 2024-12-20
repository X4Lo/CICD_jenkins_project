package com.eheio.devopprj.controller;

import com.eheio.devopprj.dto.BookAddDTO;
import com.eheio.devopprj.model.Book;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;

@RestController
@RequestMapping("/api/v1/library")
public class LibraryController {
    static Collection<Book> books;

    public LibraryController() {
        books = new ArrayList<Book>();
    }

    @GetMapping
    public ResponseEntity<Collection<Book>> getBooks() {
        if (!books.isEmpty()) {
            return ResponseEntity.ok(books);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Book> addBook(@RequestBody BookAddDTO bookAddDTO) {
        Book book = new Book();
        book.setId(Math.random() * (100000 - 999999));
        book.setTitle(bookAddDTO.getTitle());
        book.setAuthor(bookAddDTO.getAuthor());

        return ResponseEntity.status(HttpStatus.CREATED).body(book);
    }
}
