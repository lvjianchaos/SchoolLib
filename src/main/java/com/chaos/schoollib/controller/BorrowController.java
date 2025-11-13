package com.chaos.schoollib.controller;

import com.chaos.schoollib.common.result.Result;
import com.chaos.schoollib.common.result.Results;
import com.chaos.schoollib.dto.BorrowRequestDTO;
import com.chaos.schoollib.dto.ReturnRequestDTO;
import com.chaos.schoollib.entity.BorrowRecord;
import com.chaos.schoollib.entity.User;
import com.chaos.schoollib.service.BorrowService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * (重大更新) BorrowController
 * - 返回 Result<T>
 */
@RestController
@RequestMapping("/api")
public class BorrowController {

    private final BorrowService borrowService;

    @Autowired
    public BorrowController(BorrowService borrowService) {
        this.borrowService = borrowService;
    }

    /**
     * 1. 借书
     */
    @PostMapping("/borrow")
    public Result<BorrowRecord> borrowBook(
            @Valid @RequestBody BorrowRequestDTO borrowRequest,
            @AuthenticationPrincipal User currentUser
    ) {
        BorrowRecord record = borrowService.borrowBook(currentUser.getUserID(), borrowRequest.getBookId());
        return Results.success(record);
    }

    /**
     * 2. 还书
     */
    @PostMapping("/return")
    public Result<BorrowRecord> returnBook(
            @Valid @RequestBody ReturnRequestDTO returnRequest,
            @AuthenticationPrincipal User currentUser
    ) {
        BorrowRecord record = borrowService.returnBook(currentUser.getUserID(), returnRequest.getRecordId());
        return Results.success(record);
    }

    /**
     * 3. 获取我的借阅记录
     */
    @GetMapping("/me/records")
    public Result<List<BorrowRecord>> getMyRecords(
            @AuthenticationPrincipal User currentUser
    ) {
        List<BorrowRecord> records = borrowService.getMyRecords(currentUser.getUserID());
        return Results.success(records);
    }

    /**
     * 4. 管理员获取所有借阅记录
     */
    @GetMapping("/admin/records")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<List<BorrowRecord>> getAllRecords() {
        List<BorrowRecord> records = borrowService.getAllRecords();
        return Results.success(records);
    }
}