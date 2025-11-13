package com.chaos.schoollib.mapper;

import com.chaos.schoollib.entity.Book;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 职责：
 * 1. 声明为 @Mapper，让 MyBatis-Spring-Boot-Starter 能够扫描到它。
 * 2. (更新) 添加图书管理的 CRUD (增删改查) 接口方法。
 */
@Mapper
public interface BookMapper {

    /**
     * 新增一本书
     * @param book 图书对象
     * @return 受影响的行数
     */
    int insert(Book book);

    /**
     * 根据 ID 更新一本书的信息
     * @param book 图书对象
     * @return 受影响的行数
     */
    int update(Book book);

    /**
     * 根据 ID 删除一本书
     * @param bookId 图书 ID
     * @return 受影响的行数
     */
    int deleteById(@Param("bookId") Integer bookId);

    /**
     * 根据 ID 查找一本书
     * @param bookId 图书 ID
     * @return 图书对象, 找不到则返回 null
     */
    Book findById(@Param("bookId") Integer bookId);

    /**
     * 查找所有图书
     * @return 图书列表
     */
    List<Book> findAll();

    // 阶段四中我们将在这里添加 decreaseStock 方法
}