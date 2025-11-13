package com.chaos.schoollib.service.impl;

import com.chaos.schoollib.common.convention.errorcode.BorrowErrorCode;
import com.chaos.schoollib.common.convention.exception.ClientException;
import com.chaos.schoollib.entity.BorrowRecord;
import com.chaos.schoollib.mapper.BookMapper;
import com.chaos.schoollib.mapper.BorrowRecordMapper;
import com.chaos.schoollib.service.BorrowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * (重构) BorrowService
 * - 抛出 ClientException
 */
@Service
public class BorrowServiceImpl implements BorrowService {

    private final BookMapper bookMapper;
    private final BorrowRecordMapper borrowRecordMapper;

    @Autowired
    public BorrowServiceImpl(BookMapper bookMapper, BorrowRecordMapper borrowRecordMapper) {
        this.bookMapper = bookMapper;
        this.borrowRecordMapper = borrowRecordMapper;
    }

    @Transactional
    @Override
    public BorrowRecord borrowBook(Integer userId, Integer bookId) {

        // 1. (更新) 检查是否已借阅
        BorrowRecord activeRecord = borrowRecordMapper.findActiveRecordByUserAndBook(userId, bookId, "borrowed");
        if (activeRecord != null) {
            throw new ClientException(BorrowErrorCode.ALREADY_BORROWED);
        }

        // 2. (更新) 尝试原子化减库存
        int affectedRows = bookMapper.decreaseStock(bookId);
        if (affectedRows == 0) {
            throw new ClientException(BorrowErrorCode.STOCK_NOT_SUFFICIENT);
        }

        // 3. 减库存成功，创建借阅记录
        BorrowRecord record = new BorrowRecord();
        record.setUserID(userId);
        record.setBookID(bookId);
        record.setBorrowDate(LocalDateTime.now());
        record.setDueDate(LocalDateTime.now().plusDays(30)); // 默认 30 天
        record.setStatus("borrowed");

        borrowRecordMapper.insert(record);
        return record;
    }

    @Transactional
    @Override
    public BorrowRecord returnBook(Integer userId, Integer recordId) {
        // 1. (更新) 查找借阅记录
        BorrowRecord record = borrowRecordMapper.findById(recordId);
        if (record == null) {
            throw new ClientException(BorrowErrorCode.RECORD_NOT_FOUND);
        }

        // 2. (更新) 验证：确保是本人还书
        if (!record.getUserID().equals(userId)) {
            throw new ClientException(BorrowErrorCode.NO_PERMISSION_FOR_RECORD);
        }

        // 3. (更新) 验证：确保是"已借出"状态
        if (!record.getStatus().equals("borrowed")) {
            throw new ClientException(BorrowErrorCode.INVALID_RETURN);
        }

        // 4. 更新记录状态
        record.setStatus("returned");
        record.setReturnDate(LocalDateTime.now());
        borrowRecordMapper.update(record);

        // 5. 原子化加库存
        bookMapper.increaseStock(record.getBookID());
        return record;
    }

    @Override
    public List<BorrowRecord> getMyRecords(Integer userId) {
        return borrowRecordMapper.findByUserId(userId);
    }

    @Override
    public List<BorrowRecord> getAllRecords() {
        return borrowRecordMapper.findAll();
    }
}