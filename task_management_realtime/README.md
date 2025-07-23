# Hệ Thống Quản Lý Tác Vụ Thời Gian Thực

## Mô tả
Hệ thống quản lý tác vụ với tính năng realtime, hỗ trợ phân quyền admin và member, với giao diện hiện đại và bảo mật cao.

## Tính năng chính

### 🔐 Xác thực và Phân quyền
- **Đăng ký tài khoản**: Form đăng ký với validation đầy đủ
- **Đăng nhập**: Xác thực người dùng với phân quyền admin/member
- **Phân quyền truy cập**: 
  - **Kiểm tra URL**: Ngăn chặn truy cập trái phép bằng cách thay đổi URL
  - **Script bảo vệ**: Kiểm tra quyền ngay khi trang load
  - **Redirect tự động**: Chuyển hướng về đúng trang theo quyền
- **Đăng xuất**: Xóa session và chuyển về trang chủ

### 🎨 Giao diện hiện đại (Layout Roblox)
- **Top Navigation Bar**: Thanh điều hướng ngang cố định ở đầu trang
  - **Brand Logo**: Logo với icon và tên hệ thống
  - **Menu Items**: Dashboard, Tác vụ, Người dùng (chỉ admin)
  - **User Dropdown**: Ở góc phải với thông tin user và menu đăng xuất
- **Sidebar Navigation**: Thanh điều hướng dọc bên trái
  - **User Profile**: Hiển thị avatar và thông tin user
  - **Navigation Links**: Menu items với icons
  - **Collapsible**: Có thể thu gọn sidebar
  - **Role-based**: Hiển thị menu khác nhau cho admin/member
- **User Dropdown Features**:
  - **Hover dropdown**: Hiển thị khi hover vào user button
  - **User info**: Avatar lớn, username, role
  - **Menu items**: Hồ sơ, Cài đặt, Đăng xuất
  - **Visual feedback**: Hover effects và animations
- **Responsive Design**: Tương thích mobile và desktop

### 📋 Quản lý tác vụ
- **Tạo tác vụ** (chỉ Admin): Giao tác vụ cho member
- **Xem danh sách tác vụ**: Hiển thị tất cả tác vụ với thông tin chi tiết
- **Cập nhật trạng thái**: Chuyển đổi giữa pending/done
- **Phân quyền cập nhật**: 
  - Admin: Cập nhật tất cả tác vụ
  - Member: Chỉ cập nhật tác vụ được giao
- **Filter tác vụ**: Lọc theo trạng thái
- **Time management**: Hiển thị thời gian còn lại với màu sắc cảnh báo

### ⚡ Realtime
- **WebSocket**: Cập nhật realtime khi có thay đổi tác vụ
- **Thông báo**: Alert khi có cập nhật từ server

### 📊 Thống kê Dashboard
- **Admin Dashboard**:
  - Tổng số tác vụ
  - Tác vụ đang chờ
  - Tác vụ hoàn thành
  - Tổng số người dùng
- **Member Dashboard**:
  - Tác vụ của tôi
  - Tác vụ đang chờ
  - Tác vụ hoàn thành
  - Tác vụ quá hạn
  - Tác vụ cần hoàn thành hôm nay

## Cải thiện bảo mật đã thực hiện

### 1. **Phân quyền URL**
- Kiểm tra quyền truy cập ngay khi trang load
- Script bảo vệ trong HTML head
- Redirect tự động về đúng trang theo quyền
- Ngăn chặn truy cập trái phép bằng cách thay đổi URL

### 2. **Session Management**
- Kiểm tra session trước khi thực hiện actions
- Validation dữ liệu đầu vào
- Phân quyền API calls

### 3. **Frontend Security**
- Kiểm tra quyền truy cập ở nhiều layer
- Validation form đầy đủ
- Error handling tốt hơn

## Cải thiện UI/UX (Layout Roblox)

### 1. **Top Navigation Bar**
- Fixed navigation với dark theme (#2c3e50)
- Brand logo với icon và gradient color
- Menu items với hover effects
- User dropdown ở góc phải với avatar và thông tin

### 2. **Sidebar Navigation**
- Fixed sidebar với dark theme (#34495e)
- User profile section với avatar và thông tin
- Navigation links với icons và active states
- Collapsible functionality
- Footer với toggle button

### 3. **User Dropdown**
- Large avatar và user details
- Menu items với icons
- Divider line
- Logout button với red color
- Smooth animations và transitions

### 4. **Dashboard Design**
- Stats cards với icons và màu sắc
- Welcome message cho member
- Filter và search functionality
- Time remaining với color coding

### 5. **Interactive Elements**
- Hover effects và animations
- Confirm dialogs
- Loading states
- Empty states
- Smooth transitions

## Database Operations

### ✅ Tất cả thao tác đều được lưu vào database:

#### **Admin Operations:**
- ✅ **Tạo tác vụ** → Lưu vào bảng `Tasks`
- ✅ **Cập nhật trạng thái** → Lưu vào `Tasks` và `TaskHistory`
- ✅ **Xem danh sách users** → Đọc từ bảng `Users`
- ✅ **Xem thống kê** → Đọc từ `Tasks` và `Users`

#### **Member Operations:**
- ✅ **Cập nhật trạng thái tác vụ** → Lưu vào `Tasks` và `TaskHistory`
- ✅ **Xem tác vụ được giao** → Đọc từ bảng `Tasks`
- ✅ **Xem thống kê cá nhân** → Đọc từ `Tasks`

#### **Authentication:**
- ✅ **Đăng ký** → Lưu vào bảng `Users`
- ✅ **Đăng nhập** → Kiểm tra từ bảng `Users`
- ✅ **Session management** → Sử dụng localStorage

## Cấu trúc thư mục
```
task_management_realtime/
├── backend/
│   ├── app.py              # Flask API server
│   ├── models.py           # Data models
│   ├── db.py              # Database connection
│   ├── websocket_manager.py # WebSocket handling
│   ├── utils.py           # Utility functions
│   ├── create_db.sql      # Database schema
│   └── requirements.txt   # Python dependencies
├── frontend/
│   ├── index.html         # Welcome page
│   ├── app.js            # Main JavaScript logic
│   ├── style.css         # Global styles
│   ├── auth/
│   │   ├── login.html    # Login page
│   │   └── register.html # Register page
│   ├── admin/
│   │   ├── index.html    # Admin dashboard
│   │   └── style.css     # Admin styles
│   └── member/
│       ├── index.html    # Member dashboard
│       └── style.css     # Member styles
└── README.md
```

## Cài đặt và chạy

### Backend
```bash
cd backend
pip install -r requirements.txt
python app.py
```

### Frontend
Mở file `frontend/index.html` trong trình duyệt hoặc sử dụng live server.

## API Endpoints

- `GET /api/tasks` - Lấy danh sách tác vụ
- `POST /api/tasks` - Tạo tác vụ mới (Admin only)
- `PUT /api/tasks/<id>` - Cập nhật trạng thái tác vụ
- `GET /api/users` - Lấy danh sách người dùng
- `POST /api/login` - Đăng nhập
- `POST /api/register` - Đăng ký

## Database Schema

### Users
- id, username, password, role, full_name, email, dob, phone, gender

### Tasks  
- id, title, description, status, deadline, assigned_to

### TaskHistory
- id, task_id, changed_by, old_status, new_status, changed_at

## Tính năng bảo mật

### ✅ Đã triển khai
- [x] Phân quyền URL access
- [x] Session validation
- [x] Role-based access control
- [x] Input validation
- [x] Error handling
- [x] Secure redirects

### 🔄 Cần cải thiện
- [ ] JWT authentication
- [ ] Password hashing
- [ ] CSRF protection
- [ ] Rate limiting
- [ ] HTTPS enforcement

## Tính năng tương lai

- [ ] Email notifications
- [ ] File attachments
- [ ] Task comments
- [ ] Dashboard analytics
- [ ] Export reports
- [ ] Mobile app
- [ ] Push notifications 