package com.chaos.schoollib.service.impl;

import com.chaos.schoollib.dto.BookDTO;
import com.chaos.schoollib.entity.Book;
import com.chaos.schoollib.mapper.BookMapper;
import com.chaos.schoollib.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 图书管理业务逻辑实现
 */
@Service
public class BookServiceImpl implements BookService {

    private final BookMapper bookMapper;

    @Autowired
    public BookServiceImpl(BookMapper bookMapper) {
        this.bookMapper = bookMapper;
    }

    // (可选) 设为 @Transactional，虽然在单条插入时不是必须的，
    // 但在 Service 层的方法上声明事务是个好习惯。
    @Transactional
    @Override
    public Book createBook(BookDTO bookDTO) {
        Book book = new Book();
        // 1. DTO -> Entity 转换
        book.setTitle(bookDTO.getTitle());
        book.setAuthor(bookDTO.getAuthor());
        book.setPublisher(bookDTO.getPublisher());
        book.setIsbn(bookDTO.getIsbn());
        book.setCategory(bookDTO.getCategory());
        book.setTotal(bookDTO.getTotal());

        // 业务逻辑：新书入库时，当前库存 (Stock) 等于总库存 (Total)
        book.setStock(bookDTO.getTotal());

        // 2. 调用 Mapper 保存
        bookMapper.insert(book);

        // 3. 返回包含新 ID 的实体
        // (因为 XML 中配置了 useGeneratedKeys="true" keyProperty="bookID")
        return book;
    }

    @Transactional
    @Override
    public Book updateBook(Integer bookId, BookDTO bookDTO) {
        // 1. 检查是否存在
        Book existingBook = getBookById(bookId); // 复用 getBookById

        // (在阶段五中，我们会用自定义异常替换)
        if (existingBook == null) {
            throw new RuntimeException("Book not found with id: " + bookId);
        }

        // 2. DTO -> Entity 转换
        existingBook.setTitle(bookDTO.getTitle());
        existingBook.setAuthor(bookDTO.getAuthor());
        existingBook.setPublisher(bookDTO.getPublisher());
        existingBook.setIsbn(bookDTO.getIsbn());
        existingBook.setCategory(bookDTO.getCategory());

        // 业务逻辑：更新 Total 时，Stock 可能也需要调整
        // 这是一个简化的逻辑：假设只是调整总数，不影响当前借出
        // 复杂的逻辑需要计算 (Total - (oldTotal - oldStock))
        // 我们先简化处理：
        int stockChange = bookDTO.getTotal() - existingBook.getTotal();
        existingBook.setTotal(bookDTO.getTotal());
        existingBook.setStock(existingBook.getStock() + stockChange);

        // 确保库存不会为负 (虽然数据库有 CHECK)
        if (existingBook.getStock() < 0) {
            throw new RuntimeException("Stock adjustment resulted in negative stock.");
        }

        // 3. 调用 Mapper 更新
        bookMapper.update(existingBook);
        return existingBook;
    }

    @Transactional
    @Override
    public void deleteBook(Integer bookId) {
        // (在实际项目中，你可能需要检查这本书是否还有人借阅)
        // (我们暂时只做简单删除)
        int affectedRows = bookMapper.deleteById(bookId);
        if (affectedRows == 0) {
            throw new RuntimeException("Book not found with id: " + bookId + " (or deletion failed)");
        }
    }

    @Override
    public Book getBookById(Integer bookId) {
        Book book = bookMapper.findById(bookId);
        if (book == null) {
            // (阶段五中替换为 ResourceNotFoundException)
            throw new RuntimeException("Book not found with id: " + bookId);
        }
        return book;
    }

    @Override
    public List<Book> getAllBooks() {
        return bookMapper.findAll();
    }
}