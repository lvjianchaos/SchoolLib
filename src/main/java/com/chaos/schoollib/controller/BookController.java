package com.chaos.schoollib.controller;

import com.chaos.schoollib.common.result.Result;
import com.chaos.schoollib.common.result.Results;
import com.chaos.schoollib.dto.BookDTO;
import com.chaos.schoollib.entity.Book;
import com.chaos.schoollib.service.BookService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * (重大更新) BookController
 * - 返回 Result<T>
 */
@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    /**
     * 1. 创建新书
     */
    @PostMapping
    public Result<Book> createBook(@Valid @RequestBody BookDTO bookDTO) {
        Book createdBook = bookService.createBook(bookDTO);
        return Results.success(createdBook);
    }

    /**
     * 2. 获取所有图书
     */
    @GetMapping
    public Result<List<Book>> getAllBooks() {
        List<Book> books = bookService.getAllBooks();
        return Results.success(books);
    }

    /**
     * 3. 根据 ID 获取单本图书
     */
    @GetMapping("/{id}")
    public Result<Book> getBookById(@PathVariable("id") Integer bookId) {
        Book book = bookService.getBookById(bookId);
        return Results.success(book);
    }

    /**
     * 4. 更新图书
     */
    @PutMapping("/{id}")
    public Result<Book> updateBook(@PathVariable("id") Integer bookId,
                                   @Valid @RequestBody BookDTO bookDTO) {
        Book updatedBook = bookService.updateBook(bookId, bookDTO);
        return Results.success(updatedBook);
    }

    /**
     * 5. 删除图书
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteBook(@PathVariable("id") Integer bookId) {
        bookService.deleteBook(bookId);
        return Results.success();
    }
}