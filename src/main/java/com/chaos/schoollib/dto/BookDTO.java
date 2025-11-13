package com.chaos.schoollib.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * 图书数据传输对象
 * 用于创建（Create）和更新（Update）图书时，接收前端传来的数据。
 * 使用 DTO 避免直接暴露 'Book' 实体，
 * 并且可以对输入进行校验。
 */
@Data
public class BookDTO {

    @NotBlank(message = "书名不能为空") // JSR 303 Validation
    private String title;

    private String author;

    private String publisher;

    private String isbn;

    private String category;

    @NotNull(message = "总库存不能为空")
    @PositiveOrZero(message = "总库存不能为负")
    private Integer total;

    // 注意：DTO 中通常不包含 'stock'（当前库存），
    // 因为 'stock' 应该在添加新书时由业务逻辑（例如 stock = total）或借还书时自动管理，
    // 而不是由用户在创建时手动指定。
}