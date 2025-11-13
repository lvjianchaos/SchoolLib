package com.chaos.schoollib.entity;

import lombok.Data;

/**
 * 图书实体类
 * 对应数据库中的 'Book' 表
 */
@Data
public class Book {

    private Integer bookID;
    private String title;
    private String author;
    private String publisher;
    private String isbn;
    private String category;
    private Integer stock;
    private Integer total;

}