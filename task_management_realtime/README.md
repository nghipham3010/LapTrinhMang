# Task Management System - Hệ thống Quản lý Tác vụ

## Mô tả
Hệ thống quản lý tác vụ realtime với giao diện web hiện đại, hỗ trợ đa người dùng và thông báo realtime.

## Tính năng chính

### Cho Admin:
- **Dashboard**: Xem tổng quan hệ thống
- **Quản lý Tác vụ**: 
  - Thêm tác vụ mới
  - Chỉnh sửa tác vụ
  - Xóa tác vụ
  - Lọc tác vụ theo trạng thái, người thực hiện
  - Tìm kiếm tác vụ theo tiêu đề
  - Xem danh sách tất cả tác vụ
- **Quản lý Người dùng**: Thêm, sửa, xóa người dùng
- **Thông báo**: Nhận thông báo realtime khi có thay đổi
- **Hồ sơ**: Quản lý thông tin cá nhân

### Cho Member:
- Xem danh sách tác vụ được giao
- Cập nhật trạng thái tác vụ
- Nhận thông báo khi có tác vụ mới
- Quản lý hồ sơ cá nhân

## Cấu trúc dự án

```
task_management_realtime/
├── backend/
│   ├── app.py              # API server chính
│   ├── db.py               # Kết nối database
│   ├── models.py           # Định nghĩa models
│   ├── websocket_manager.py # Quản lý WebSocket
│   ├── utils.py            # Tiện ích
│   ├── requirements.txt    # Dependencies
│   └── create_db.sql       # Script tạo database
├── frontend/
│   ├── admin/              # Giao diện admin
│   │   ├── index.html      # Dashboard
│   │   ├── tasks.html      # Quản lý tác vụ
│   │   ├── user_management.html # Quản lý người dùng
│   │   ├── profile.html    # Hồ sơ
│   │   ├── layout.html     # Layout chung
│   │   └── style.css       # CSS chung
│   ├── member/             # Giao diện member
│   ├── auth/               # Đăng nhập/đăng ký
│   └── index.html          # Trang chủ
└── README.md
```

## Cài đặt và chạy

### Backend (Python Flask)
```bash
cd backend
pip install -r requirements.txt
python app.py
```

### Frontend
Mở file `frontend/admin/index.html` hoặc `frontend/member/index.html` trong trình duyệt.

## API Endpoints

### Tác vụ
- `GET /api/tasks` - Lấy danh sách tác vụ
- `POST /api/tasks` - Thêm tác vụ mới (admin)
- `PUT /api/tasks/<id>` - Cập nhật tác vụ
- `DELETE /api/tasks/<id>` - Xóa tác vụ (admin)

### Người dùng
- `GET /api/users` - Lấy danh sách người dùng
- `POST /api/users` - Thêm người dùng (admin)
- `PUT /api/users/<id>` - Cập nhật người dùng (admin)
- `DELETE /api/users/<id>` - Xóa người dùng (admin)

### Xác thực
- `POST /api/login` - Đăng nhập
- `POST /api/register` - Đăng ký

### Thông báo
- `GET /api/notifications/<user_id>` - Lấy thông báo
- `PUT /api/notifications/<id>/read` - Đánh dấu đã đọc
- `PUT /api/notifications/<user_id>/read-all` - Đánh dấu tất cả đã đọc

## Tính năng mới: Quản lý Tác vụ

### Chức năng chính:
1. **Thêm tác vụ mới**: Admin có thể tạo tác vụ và giao cho member
2. **Chỉnh sửa tác vụ**: Cập nhật thông tin tác vụ
3. **Xóa tác vụ**: Xóa tác vụ khỏi hệ thống
4. **Lọc và tìm kiếm**:
   - Lọc theo trạng thái (Chờ xử lý, Đang thực hiện, Hoàn thành)
   - Lọc theo người thực hiện
   - Tìm kiếm theo tiêu đề tác vụ
5. **Hiển thị thông tin**:
   - Tiêu đề và mô tả tác vụ
   - Trạng thái với màu sắc phân biệt
   - Người được giao tác vụ
   - Hạn chót và ngày tạo

### Giao diện:
- Layout responsive với sidebar
- Modal form cho thêm/sửa tác vụ
- Card layout cho danh sách tác vụ
- Thông báo realtime khi có thay đổi

## Database Schema

### Bảng Tasks
- task_id (Primary Key)
- title (Tiêu đề tác vụ)
- description (Mô tả)
- status (Trạng thái)
- deadline (Hạn chót)
- assigned_to (Người được giao)
- created_at (Ngày tạo)

### Bảng Users
- user_id (Primary Key)
- username (Tên đăng nhập)
- password (Mật khẩu)
- role (Vai trò: admin/member)
- full_name, email, phone, etc.

### Bảng Notifications
- notification_id (Primary Key)
- user_id (Người nhận)
- content (Nội dung thông báo)
- is_read (Đã đọc chưa)
- created_at (Thời gian tạo)

## Công nghệ sử dụng

### Backend:
- Python Flask
- SQL Server (SSMS)
- Flask-SocketIO (WebSocket)
- Flask-CORS

### Frontend:
- HTML5, CSS3, JavaScript
- Font Awesome (Icons)
- Socket.IO Client
- Responsive Design

## Hướng dẫn sử dụng

1. **Đăng nhập**: Sử dụng tài khoản admin để truy cập quản lý tác vụ
2. **Tạo tác vụ**: Click "Thêm Tác vụ" và điền thông tin
3. **Quản lý tác vụ**: Sử dụng các nút Sửa/Xóa trên từng tác vụ
4. **Lọc tác vụ**: Sử dụng các bộ lọc ở đầu trang
5. **Tìm kiếm**: Nhập từ khóa vào ô tìm kiếm

## Lưu ý
- Chỉ admin có quyền thêm, sửa, xóa tác vụ
- Member chỉ có thể cập nhật trạng thái tác vụ được giao
- Hệ thống tự động gửi thông báo khi có thay đổi
- Dữ liệu được lưu trữ trong SQL Server 