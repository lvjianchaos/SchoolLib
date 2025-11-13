package com.chaos.schoollib.service;

import com.chaos.schoollib.dto.BookDTO;
import com.chaos.schoollib.entity.Book;

import java.util.List;

/**
 * 图书管理业务逻辑接口
 */
public interface BookService {

    /**
     * 创建一本新书
     * @param bookDTO 来自 Controller 的 DTO
     * @return 已创建并包含 BookID 的实体
     */
    Book createBook(BookDTO bookDTO);

    /**
     * 更新图书信息
     * @param bookId 要更新的图书 ID
     * @param bookDTO 包含新数据的 DTO
     * @return 更新后的实体
     */
    Book updateBook(Integer bookId, BookDTO bookDTO);

    /**
     * 删除一本书
     * @param bookId 图书 ID
     */
    void deleteBook(Integer bookId);

    /**
     * 根据 ID 获取图书
     * @param bookId 图书 ID
     * @return 图书实体
     */
    Book getBookById(Integer bookId);

    /**
     * 获取所有图书
     * @return 图书实体列表
     */
    List<Book> getAllBooks();
}