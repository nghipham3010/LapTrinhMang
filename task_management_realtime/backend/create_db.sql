-- Tạo database
CREATE DATABASE TaskManagement;
GO
USE TaskManagement;
GO

-- Bảng Users (người dùng)
CREATE TABLE Users (
    user_id INT PRIMARY KEY IDENTITY,                
    full_name NVARCHAR(100) NOT NULL,           
    email NVARCHAR(100) NOT NULL UNIQUE,        
    username NVARCHAR(50) NOT NULL UNIQUE,      
    password NVARCHAR(255) NOT NULL,            
    dob DATE NOT NULL,                 -- ngày sinh         
    phone NVARCHAR(20) NOT NULL,                
    gender NVARCHAR(10) NOT NULL,               
    role NVARCHAR(20) NOT NULL DEFAULT 'member' 
);

-- Bảng Profiles (hồ sơ cá nhân)
CREATE TABLE Profiles (
    profile_id INT PRIMARY KEY IDENTITY,                
    user_id INT FOREIGN KEY REFERENCES Users(user_id),      
    avatar NVARCHAR(255),                       
    bio NVARCHAR(255),                    -- mô tả cá nhân
    contact NVARCHAR(100),                  -- thông tin liên hệ
    address NVARCHAR(255)                       
);

-- Bảng Tasks (tác vụ)
CREATE TABLE Tasks (
    task_id INT PRIMARY KEY IDENTITY,                
    title NVARCHAR(100) NOT NULL,               
    description NVARCHAR(255),                  
    status NVARCHAR(20) NOT NULL,               
    deadline DATE,                              
    assigned_to INT FOREIGN KEY REFERENCES Users(user_id) 
);

-- Bảng TaskHistory (lịch sử tác vụ)
CREATE TABLE TaskHistory (
    task_history_id INT PRIMARY KEY IDENTITY,                
    task_id INT FOREIGN KEY REFERENCES Tasks(task_id),      
    changed_by INT FOREIGN KEY REFERENCES Users(user_id),   
    old_status NVARCHAR(20),                    
    new_status NVARCHAR(20),                    
    changed_at DATETIME DEFAULT GETDATE()  -- lấy thời gian hiện tại
);

-- Bảng Notifications (thông báo)
CREATE TABLE Notifications (
    notification_id INT PRIMARY KEY IDENTITY,                
    user_id INT FOREIGN KEY REFERENCES Users(user_id),      
    content NVARCHAR(255) NOT NULL,             
    is_read BIT DEFAULT 0,                      
    created_at DATETIME DEFAULT GETDATE()       -- lấy thời gian hiện tại
);

-- Bảng Reports (báo cáo)
CREATE TABLE Reports (
    report_id INT PRIMARY KEY IDENTITY,                
    report_type NVARCHAR(50) NOT NULL,          -- loại báo cáo
    created_by INT FOREIGN KEY REFERENCES Users(user_id),     -- người tạo báo cáo
    created_at DATETIME DEFAULT GETDATE(),      -- lấy thời gian hiện tại
    content NVARCHAR(MAX)                       
);
GO

drop table Tasks;
drop table TaskHistory;

INSERT INTO Users (full_name, email, username, password, dob, phone, gender, role) 
VALUES ('Admin', 'admin@gmail.com', 'admin', 'Qq12345!', '1990-01-01', '0909090909', 'male', 'admin');

-- Thêm thông báo mẫu
INSERT INTO Notifications (user_id, content, is_read, created_at) 
VALUES 
(1, 'Chào mừng bạn đến với hệ thống quản lý tác vụ!', 0, GETDATE()),
(1, 'Bạn có 3 tác vụ cần hoàn thành trong tuần này.', 0, DATEADD(minute, -30, GETDATE())),
(1, 'Tác vụ "Cập nhật website" đã được giao cho bạn.', 0, DATEADD(hour, -2, GETDATE()));

-- Thêm thông báo mẫu cho member (nếu có member với id = 2)
INSERT INTO Notifications (user_id, content, is_read, created_at) 
VALUES 
(2, 'Chào mừng member! Bạn có tác vụ mới cần xử lý.', 0, GETDATE()),
(2, 'Deadline của tác vụ "Thiết kế giao diện" sắp đến hạn.', 0, DATEADD(hour, -1, GETDATE()));