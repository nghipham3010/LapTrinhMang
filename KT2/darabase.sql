-- KẾ HOẠCH TẠO CƠ SỞ DỮ LIỆ
 CREATE DATABASE QuanLyBanHang;
 GO
 USE QuanLyBanHang;
 GO
-- Bước 3: Tạo các bảng

-- Bảng LOAI_MAT_HANG
CREATE TABLE LOAI_MAT_HANG (
    MALOAI NVARCHAR(50) PRIMARY KEY,
    TENLOAI NVARCHAR(255),
    VO_HIEU_HOA BIT DEFAULT 0 -- 0: hoạt động, 1: vô hiệu hóa
);

-- Bảng MAT_HANG
CREATE TABLE MAT_HANG (
    MAMH NVARCHAR(50) PRIMARY KEY,
    TENMH NVARCHAR(255),
    GIABAN DECIMAL(18, 2),
    DVT NVARCHAR(50), -- Đơn vị tính
    MALOAI NVARCHAR(50),
    MOTA NVARCHAR(MAX),
    VO_HIEU_HOA BIT DEFAULT 0,
    FOREIGN KEY (MALOAI) REFERENCES LOAI_MAT_HANG(MALOAI)
);

-- Bảng NHA_CUNG_CAP
CREATE TABLE NHA_CUNG_CAP (
    MANCC NVARCHAR(50) PRIMARY KEY,
    TENNCC NVARCHAR(255),
    DIACHI NVARCHAR(255),
    SDT NVARCHAR(20),
    EMAIL NVARCHAR(255),
    VO_HIEU_HOA BIT DEFAULT 0
);

-- Bảng KHACH_HANG
CREATE TABLE KHACH_HANG (
    MAKH NVARCHAR(50) PRIMARY KEY,
    TENKH NVARCHAR(255),
    GIOITINH NVARCHAR(10), -- Ví dụ: 'Nam', 'Nữ', 'Khác'
    SDT NVARCHAR(20),
    VO_HIEU_HOA BIT DEFAULT 0
);

-- Bảng NHAN_VIEN
CREATE TABLE NHAN_VIEN (
    MANHANVIEN NVARCHAR(50) PRIMARY KEY,
    TENNHANVIEN NVARCHAR(255),
    DIACHI NVARCHAR(255),
    GIOITINH NVARCHAR(10), -- Ví dụ: 'Nam', 'Nữ', 'Khác'
    NGAYSINH DATE,
    CMND_CCCD NVARCHAR(20),
    TENDANGNHAP NVARCHAR(50) UNIQUE, -- Tên đăng nhập phải là duy nhất
    MATKHAU NVARCHAR(255),
    VO_HIEU_HOA BIT DEFAULT 0,
    LA_QUAN_LY BIT DEFAULT 0 -- 0: nhân viên, 1: quản lý
);

-- Bảng DON_DAT_HANG
CREATE TABLE DON_DAT_HANG (
    MADDH NVARCHAR(50) PRIMARY KEY,
    MANCC NVARCHAR(50),
    NGAYDAT DATE,
    MANV NVARCHAR(50), -- Nhân viên đặt hàng
    FOREIGN KEY (MANCC) REFERENCES NHA_CUNG_CAP(MANCC),
    FOREIGN KEY (MANV) REFERENCES NHAN_VIEN(MANHANVIEN)
);

-- Bảng HOA_DON
CREATE TABLE HOA_DON (
    MAHD NVARCHAR(50) PRIMARY KEY,
    MADON NVARCHAR(50), -- Mã đơn đặt hàng hoặc mã khác liên quan
    NGAYLAP DATE,
    MANHANVIEN NVARCHAR(50), -- Nhân viên lập hóa đơn
    MAKH NVARCHAR(50),
    FOREIGN KEY (MANHANVIEN) REFERENCES NHAN_VIEN(MANHANVIEN),
    FOREIGN KEY (MAKH) REFERENCES KHACH_HANG(MAKH)
);


-- Bảng DONDATHANG_MATHANG (Bảng chi tiết đơn đặt hàng)
CREATE TABLE DONDATHANG_MATHANG (
    MADDH NVARCHAR(50),
    MAMH NVARCHAR(50),
    SOLUONG INT,
    DONGIA DECIMAL(18, 2),
    PRIMARY KEY (MADDH, MAMH),
    FOREIGN KEY (MADDH) REFERENCES DON_DAT_HANG(MADDH),
    FOREIGN KEY (MAMH) REFERENCES MAT_HANG(MAMH)
);

-- Bảng HOADON_MATHANG (Bảng chi tiết hóa đơn)
CREATE TABLE HOADON_MATHANG (
    MAHD NVARCHAR(50),
    MAMH NVARCHAR(50),
    SOLUONGMUA INT,
    THANHTIEN DECIMAL(18, 2),
    PRIMARY KEY (MAHD, MAMH),
    FOREIGN KEY (MAHD) REFERENCES HOA_DON(MAHD),
    FOREIGN KEY (MAMH) REFERENCES MAT_HANG(MAMH)
);
GO



-- 1. Thêm dữ liệu vào bảng LOAI_MAT_HANG
INSERT INTO LOAI_MAT_HANG (MALOAI, TENLOAI, VO_HIEU_HOA) VALUES
('L001', N'Đồ Gia Dụng', 0),
('L002', N'Điện Tử', 0),
('L003', N'Thực Phẩm', 0),
('L004', N'Văn Phòng Phẩm', 0),
('L005', N'Thời Trang', 1); -- Loại này đã vô hiệu hóa
GO

-- 2. Thêm dữ liệu vào bảng MAT_HANG
INSERT INTO MAT_HANG (MAMH, TENMH, GIABAN, DVT, MALOAI, MOTA, VO_HIEU_HOA) VALUES
('MH001', N'Nồi cơm điện', 500000.00, N'Cái', 'L001', N'Nồi cơm điện tử dung tích 1.8L', 0),
('MH002', N'Tivi LED 43 inch', 8500000.00, N'Cái', 'L002', N'Tivi thông minh độ phân giải Full HD', 0),
('MH003', N'Mì tôm Hảo Hảo', 5000.00, N'Gói', 'L003', N'Mì tôm hương vị tôm chua cay', 0),
('MH004', N'Bút bi Thiên Long', 3000.00, N'Cây', 'L004', N'Bút bi mực xanh', 0),
('MH005', N'Áo thun cotton', 150000.00, N'Cái', 'L005', N'Áo thun nam màu trắng', 0),
('MH006', N'Tủ lạnh Inverter', 12000000.00, N'Cái', 'L001', N'Tủ lạnh 2 cánh tiết kiệm điện', 0),
('MH007', N'Dầu ăn Neptune', 45000.00, N'Chai', 'L003', N'Dầu ăn tinh luyện 1 lít', 0);
GO

-- 3. Thêm dữ liệu vào bảng NHA_CUNG_CAP
INSERT INTO NHA_CUNG_CAP (MANCC, TENNCC, DIACHI, SDT, EMAIL, VO_HIEU_HOA) VALUES
('NCC01', N'Điện Máy Xanh', N'123 Nguyễn Huệ, Q.1, TP.HCM', '0901234567', 'dienmayxanh@example.com', 0),
('NCC02', N'Thế Giới Di Động', N'456 Lê Lợi, Q.1, TP.HCM', '0907654321', 'tgdd@example.com', 0),
('NCC03', N'Masco Food', N'789 Trường Chinh, Q.Tân Bình, TP.HCM', '0912345678', 'mascofood@example.com', 0);
GO

-- 4. Thêm dữ liệu vào bảng KHACH_HANG
INSERT INTO KHACH_HANG (MAKH, TENKH, GIOITINH, SDT, VO_HIEU_HOA) VALUES
('KH001', N'Nguyễn Văn A', N'Nam', '0909111222', 0),
('KH002', N'Trần Thị B', N'Nữ', '0909333444', 0),
('KH003', N'Lê Văn C', N'Nam', '0909555666', 0);
GO

-- 5. Thêm dữ liệu vào bảng NHAN_VIEN
INSERT INTO NHAN_VIEN (MANHANVIEN, TENNHANVIEN, DIACHI, GIOITINH, NGAYSINH, CMND_CCCD, TENDANGNHAP, MATKHAU, VO_HIEU_HOA, LA_QUAN_LY) VALUES
('NV001', N'Admin', N'10 Huỳnh Tấn Phát, Q.7, TP.HCM', N'Nam', '1990-05-15', '123456789012', 'admin', '123456', 0, 1),
('NV002', N'Nguyễn Đình Khoa', N'20 Cộng Hòa, Q.Tân Bình, TP.HCM', N'Nam', '1992-11-20', '234567890123', 'khoand', 'pass123', 0, 0),
('NV003', N'Đỗ Minh Quang', N'30 Cách Mạng Tháng 8, Q.3, TP.HCM', N'Nam', '1995-01-01', '345678901234', 'quangdm', 'pass123', 1, 0); -- Nhân viên này đã vô hiệu hóa
GO

-- 6. Thêm dữ liệu vào bảng DON_DAT_HANG
INSERT INTO DON_DAT_HANG (MADDH, MANCC, NGAYDAT, MANV) VALUES
('DDH001', 'NCC01', '2023-01-10', 'NV002'),
('DDH002', 'NCC03', '2023-01-15', 'NV002'),
('DDH003', 'NCC01', '2023-02-01', 'NV001');
GO

-- 7. Thêm dữ liệu vào bảng DONDATHANG_MATHANG
INSERT INTO DONDATHANG_MATHANG (MADDH, MAMH, SOLUONG, DONGIA) VALUES
('DDH001', 'MH001', 10, 450000.00),
('DDH001', 'MH006', 2, 11000000.00),
('DDH002', 'MH003', 100, 4000.00),
('DDH002', 'MH007', 50, 40000.00),
('DDH003', 'MH002', 5, 8000000.00);
GO

-- 8. Thêm dữ liệu vào bảng HOA_DON
INSERT INTO HOA_DON (MAHD, MADON, NGAYLAP, MANHANVIEN, MAKH) VALUES
('HD001', 'DDH_ABC', '2023-01-12', 'NV002', 'KH001'), -- MADON ở đây là một tham chiếu, không phải FK
('HD002', 'DDH_DEF', '2023-01-20', 'NV001', 'KH002'),
('HD003', 'DDH_GHI', '2023-02-05', 'NV002', 'KH001');
GO

-- 9. Thêm dữ liệu vào bảng HOADON_MATHANG
INSERT INTO HOADON_MATHANG (MAHD, MAMH, SOLUONGMUA, THANHTIEN) VALUES
('HD001', 'MH001', 1, 500000.00),
('HD001', 'MH004', 5, 15000.00),
('HD002', 'MH002', 1, 8500000.00),
('HD002', 'MH003', 10, 50000.00),
('HD003', 'MH001', 2, 1000000.00),
('HD003', 'MH007', 1, 45000.00);
GO
