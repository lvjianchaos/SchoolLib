package com.chaos.schoollib.service.impl;

import com.chaos.schoollib.common.convention.errorcode.BookErrorCode;
import com.chaos.schoollib.common.convention.exception.ClientException;
import com.chaos.schoollib.dto.BookDTO;
import com.chaos.schoollib.entity.Book;
import com.chaos.schoollib.mapper.BookMapper;
import com.chaos.schoollib.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * (重构) BookService
 * - 抛出 ClientException
 */
@Service
public class BookServiceImpl implements BookService {

    private final BookMapper bookMapper;

    @Autowired
    public BookServiceImpl(BookMapper bookMapper) {
        this.bookMapper = bookMapper;
    }

    @Transactional
    @Override
    public Book createBook(BookDTO bookDTO) {
        Book book = new Book();
        book.setTitle(bookDTO.getTitle());
        book.setAuthor(bookDTO.getAuthor());
        book.setPublisher(bookDTO.getPublisher());
        book.setIsbn(bookDTO.getIsbn());
        book.setCategory(bookDTO.getCategory());
        book.setTotal(bookDTO.getTotal());
        // 刚创建时, 库存 = 总数
        book.setStock(bookDTO.getTotal());
        bookMapper.insert(book);
        return book;
    }

    @Transactional
    @Override
    public Book updateBook(Integer bookId, BookDTO bookDTO) {
        Book existingBook = getBookById(bookId); // getBookById 内部会抛出异常

        Integer stockChange = 0;
        if (bookDTO.getTotal() != null) {
            stockChange = bookDTO.getTotal() - existingBook.getTotal();
        }

        existingBook.setTitle(bookDTO.getTitle());
        existingBook.setAuthor(bookDTO.getAuthor());
        existingBook.setPublisher(bookDTO.getPublisher());
        existingBook.setIsbn(bookDTO.getIsbn());
        existingBook.setCategory(bookDTO.getCategory());
        existingBook.setTotal(bookDTO.getTotal());
        existingBook.setStock(existingBook.getStock() + stockChange);

        if (existingBook.getStock() < 0) {
            // (更新)
            throw new ClientException(BookErrorCode.BOOK_STOCK_UPDATE_ERROR);
        }

        bookMapper.update(existingBook);
        return existingBook;
    }

    @Transactional
    @Override
    public void deleteBook(Integer bookId) {
        int affectedRows = bookMapper.deleteById(bookId);
        if (affectedRows == 0) {
            // (更新)
            throw new ClientException(BookErrorCode.BOOK_NOT_FOUND);
        }
    }

    @Override
    public Book getBookById(Integer bookId) {
        Book book = bookMapper.findById(bookId);
        if (book == null) {
            // (更新)
            throw new ClientException(BookErrorCode.BOOK_NOT_FOUND);
        }
        return book;
    }

    @Override
    public List<Book> getAllBooks() {
        return bookMapper.findAll();
    }
}