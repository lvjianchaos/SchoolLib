-- 创建数据库
create database library_db;

use library_db;

-- 1. 用户表 (User)
CREATE TABLE User (
                      UserID INT AUTO_INCREMENT PRIMARY KEY,
                      Username VARCHAR(50) NOT NULL UNIQUE,
                      Password VARCHAR(255) NOT NULL, -- 存储加密后的密码
                      Role ENUM('student', 'teacher', 'admin') NOT NULL,
                      Contact VARCHAR(100),
                      RegistrationDate DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 2. 图书表 (Book)
CREATE TABLE Book (
                      BookID INT AUTO_INCREMENT PRIMARY KEY,
                      Title VARCHAR(100) NOT NULL,
                      Author VARCHAR(50),
                      Publisher VARCHAR(50),
                      ISBN VARCHAR(20) UNIQUE,
                      Category VARCHAR(50),
                      Stock INT DEFAULT 0,
                      Total INT DEFAULT 0,
                      CHECK (Stock >= 0) -- 增加一个检查约束，确保库存不会为负
);

-- 3. 借阅记录表 (BorrowRecord)
CREATE TABLE BorrowRecord (
                              RecordID INT AUTO_INCREMENT PRIMARY KEY,
                              UserID INT NOT NULL,
                              BookID INT NOT NULL,
                              BorrowDate DATETIME DEFAULT CURRENT_TIMESTAMP,
                              DueDate DATETIME,
                              ReturnDate DATETIME,
                              Status ENUM('borrowed', 'returned', 'overdue') DEFAULT 'borrowed',
                              FOREIGN KEY (UserID) REFERENCES User(UserID),
                              FOREIGN KEY (BookID) REFERENCES Book(BookID)
);