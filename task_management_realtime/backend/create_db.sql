-- Tạo database
CREATE DATABASE TaskManagement;
GO
USE TaskManagement;
GO

-- Bảng Users
CREATE TABLE Users (
    id INT PRIMARY KEY IDENTITY,
    full_name NVARCHAR(100) NOT NULL,
    email NVARCHAR(100) NOT NULL UNIQUE,
    username NVARCHAR(50) NOT NULL UNIQUE,
    password NVARCHAR(255) NOT NULL,
    dob DATE NOT NULL,
    phone NVARCHAR(20) NOT NULL,
    gender NVARCHAR(10) NOT NULL,
    role NVARCHAR(20) NOT NULL DEFAULT 'member'
);

-- Bảng Tasks
CREATE TABLE Tasks (
    id INT PRIMARY KEY IDENTITY,
    title NVARCHAR(100) NOT NULL,
    description NVARCHAR(255),
    status NVARCHAR(20) NOT NULL,
    deadline DATE,
    assigned_to INT FOREIGN KEY REFERENCES Users(id)
);

-- Bảng TaskHistory
CREATE TABLE TaskHistory (
    id INT PRIMARY KEY IDENTITY,
    task_id INT FOREIGN KEY REFERENCES Tasks(id),
    changed_by INT FOREIGN KEY REFERENCES Users(id),
    old_status NVARCHAR(20),
    new_status NVARCHAR(20),
    changed_at DATETIME DEFAULT GETDATE()
);
GO

drop table Tasks;
drop table TaskHistory;

INSERT INTO Users (full_name, email, username, password, dob, phone, gender, role) 
VALUES ('Admin', 'admin@gmail.com', 'admin', 'Qq12345!', '1990-01-01', '0909090909', 'male', 'admin');