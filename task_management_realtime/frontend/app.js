const socket = io('http://localhost:5000');

// WebSocket event listeners
socket.on('connect', () => {
    console.log('WebSocket connected');
});

socket.on('disconnect', () => {
    console.log('WebSocket disconnected');
});

socket.on('connect_error', (error) => {
    console.error('WebSocket connection error:', error);
});

socket.on('task_update', (data) => {
    console.log('Nhận được cập nhật tác vụ từ server:', data);
    
    // Xử lý member completion notification
    if (data.data && data.data.action === 'member_completion') {
        const userRole = localStorage.getItem('user_role');
        if (userRole === 'admin') {
            // Admin nhận thông báo member đã hoàn thành
            const memberId = data.data.member_id;
            const isCompleted = data.data.is_completed;
            const message = data.data.message;
            
            console.log('Admin notification:', message);
            
            // Hiển thị thông báo cho admin
            const notification = document.createElement('div');
            notification.className = 'admin-notification';
            notification.innerHTML = `
                <div class="notification-content">
                    <i class="fas fa-bell"></i>
                    <span>${message}</span>
                    <button onclick="this.parentElement.parentElement.remove()" class="notification-close">
                        <i class="fas fa-times"></i>
                    </button>
                </div>
            `;
            
            // Thêm notification vào page
            const container = document.querySelector('.admin-container, .member-container');
            if (container) {
                container.appendChild(notification);
                
                // Tự động xóa sau 5 giây
                setTimeout(() => {
                    if (notification.parentElement) {
                        notification.remove();
                    }
                }, 5000);
            }
        }
    }
    
    // Refresh task list
    fetchTasks();
});

// Kiểm tra quyền truy cập
function checkAccess() {
    const userRole = localStorage.getItem('user_role');
    const currentPath = window.location.pathname;
    
    // Nếu chưa đăng nhập, chuyển về trang chủ
    if (!userRole) {
        window.location.href = '/frontend/index.html';
        return false;
    }
    
    // Kiểm tra quyền truy cập trang admin
    if (currentPath.includes('/admin/') && userRole !== 'admin') {
        alert('Bạn không có quyền truy cập trang admin!');
        window.location.href = '/frontend/member/index.html';
        return false;
    }
    
    // Kiểm tra quyền truy cập trang member
    if (currentPath.includes('/member/') && userRole !== 'member') {
        alert('Bạn không có quyền truy cập trang member!');
        window.location.href = '/frontend/admin/index.html';
        return false;
    }
    
    return true;
}

// Toggle user menu
function toggleUserMenu() {
    console.log('Toggle user menu');
    const menu = document.getElementById('userMenu');
    if (menu) {
        menu.classList.toggle('show');
        console.log('Menu toggled:', menu.classList.contains('show'));
    } else {
        console.log('Menu not found');
    }
}

// Toggle sidebar
function toggleSidebar() {
    const sidebar = document.querySelector('.sidebar');
    const mainContent = document.querySelector('.admin-container, .member-container');
    
    if (sidebar) {
        sidebar.classList.toggle('collapsed');
        if (mainContent) {
            mainContent.classList.toggle('sidebar-collapsed');
        }
    }
}

// Navigation functions
function showDashboard() {
    document.querySelectorAll('.nav-link, .sidebar-item').forEach(link => link.classList.remove('active'));
    document.querySelector('.nav-link').classList.add('active');
    document.querySelector('.sidebar-item').classList.add('active');
    // Có thể thêm logic hiển thị dashboard ở đây
}

function showTasks() {
    document.querySelectorAll('.nav-link, .sidebar-item').forEach(link => link.classList.remove('active'));
    const taskLink = document.querySelectorAll('.nav-link')[1];
    const taskSidebarLink = document.querySelectorAll('.sidebar-item')[1];
    if (taskLink) taskLink.classList.add('active');
    if (taskSidebarLink) taskSidebarLink.classList.add('active');
    fetchTasks();
}

function showUsers() {
    window.location.href = 'user_management.html';
}

function showReports() {
    document.querySelectorAll('.nav-link, .sidebar-item').forEach(link => link.classList.remove('active'));
    const reportSidebarLink = document.querySelectorAll('.sidebar-item')[3];
    if (reportSidebarLink) reportSidebarLink.classList.add('active');
    alert('Tính năng báo cáo sẽ được phát triển sau!');
}

function showProfile() {
    document.querySelectorAll('.nav-link, .sidebar-item').forEach(link => link.classList.remove('active'));
    const profileSidebarLink = document.querySelectorAll('.sidebar-item')[document.querySelectorAll('.sidebar-item').length - 2];
    if (profileSidebarLink) profileSidebarLink.classList.add('active');
    alert('Tính năng hồ sơ sẽ được phát triển sau!');
}

function showSettings() {
    document.querySelectorAll('.nav-link, .sidebar-item').forEach(link => link.classList.remove('active'));
    const settingsSidebarLink = document.querySelectorAll('.sidebar-item')[document.querySelectorAll('.sidebar-item').length - 1];
    if (settingsSidebarLink) settingsSidebarLink.classList.add('active');
    alert('Tính năng cài đặt sẽ được phát triển sau!');
}

function fetchTasks() {
    fetch('http://localhost:5000/api/tasks')
        .then(res => res.json())
        .then(renderTasks)
        .catch(error => {
            console.error('Lỗi khi tải danh sách tác vụ:', error);
            alert('Không thể tải danh sách tác vụ');
        });
}

function renderTasks(tasks) {
    const tbody = document.querySelector('#tasksTable tbody');
    if (!tbody) return;
    
    tbody.innerHTML = '';
    const users = JSON.parse(localStorage.getItem('users') || '[]');
    const currentUserId = localStorage.getItem('user_id');
    const userRole = localStorage.getItem('user_role');
    
    // Lưu trạng thái hoàn thành tạm thời của member (chỉ trên client)
    let localMemberCompletions = JSON.parse(localStorage.getItem('localMemberCompletions') || '{}');
    
    // lọc tác vụ dựa trên vai trò người dùng
    let filteredTasks = tasks;
    if (userRole === 'member') {
        filteredTasks = tasks.filter(task => task.assigned_to == currentUserId);
    }
    
    // áp dụng bộ lọc trạng thái nếu tồn tại
    const statusFilter = document.getElementById('statusFilter');
    if (statusFilter && statusFilter.value) {
        filteredTasks = filteredTasks.filter(task => task.status === statusFilter.value);
    }
    
    // cập nhật thống kê
    updateStats(tasks, currentUserId, userRole);
    
    if (filteredTasks.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="6" style="text-align: center; padding: 2rem; color: #666;">
                    <i class="fas fa-inbox" style="font-size: 2rem; margin-bottom: 1rem; display: block;"></i>
                    Không có tác vụ nào
                </td>
            </tr>
        `;
        return;
    }
    
    filteredTasks.forEach(task => {
        let assignedName = task.assigned_to;
        const user = users.find(u => u.user_id == task.assigned_to);
        if (user) assignedName = user.username + ' (' + user.role + ')';
        
        // dùng để hiển thị trạng thái của task (member: use local, admin: use DB)
        let displayStatus = task.status;
        if (userRole === 'member' && localMemberCompletions[task.task_id] !== undefined) {
            displayStatus = localMemberCompletions[task.task_id] ? 'done' : 'pending';
        }
        
        const tr = document.createElement('tr');
        if (displayStatus === 'done') {
            tr.classList.add('task-done-row');
        }
        
        let actionColumn = '';
        if (userRole === 'admin') {
            actionColumn = `
                <button onclick="updateStatus(${task.task_id}, '${task.status}')" class="btn-update">
                    <i class="fas fa-${task.status === 'pending' ? 'check' : 'undo'}"></i>
                    ${task.status === 'pending' ? 'Hoàn thành' : 'Chuyển về chờ'}
                </button>
            `;
        } else if (userRole === 'member') {
            const isChecked = displayStatus === 'done';
            actionColumn = `
                <div class="task-completion">
                    <input type="checkbox" 
                           id="task_${task.task_id}" 
                           ${isChecked ? 'checked' : ''} 
                           onchange="toggleTaskCompletion(${task.task_id}, this.checked, '${task.status}')"
                           class="task-checkbox"
                           ${isChecked ? 'disabled' : ''}>
                    <label for="task_${task.task_id}" class="task-checkbox-label">
                        <i class="fas fa-${isChecked ? 'check-circle' : 'circle'}"></i>
                        ${isChecked ? 'Đã hoàn thành' : 'Chưa hoàn thành'}
                    </label>
                </div>
            `;
        } else {
            actionColumn = '<span class="no-permission">Không có quyền</span>';
        }
        
        const timeRemaining = calculateTimeRemaining(task.deadline);
        
        tr.innerHTML = `
            <td><strong>${task.title}</strong></td>
            <td>${task.description}</td>
            ${userRole === 'admin' ? `<td>${assignedName}</td>` : ''}
            <td><span class="status-${displayStatus}">${displayStatus === 'pending' ? 'Đang chờ' : 'Hoàn thành'}</span></td>
            <td>${formatDate(task.deadline)}</td>
            ${userRole === 'member' ? `<td class="time-remaining ${timeRemaining.class}">${timeRemaining.text}</td>` : ''}
            <td>${actionColumn}</td>
        `;
        tbody.appendChild(tr);
    });
}

// cập nhật thống kê cho dashboard
function updateStats(tasks, currentUserId, userRole) {
    let myTasks = tasks;
    if (userRole === 'member') {
        myTasks = tasks.filter(task => task.assigned_to == currentUserId);
    }
    
    const totalTasks = myTasks.length;
    const pendingTasks = myTasks.filter(task => task.status === 'pending').length;
    const completedTasks = myTasks.filter(task => task.status === 'done').length;
    
    // cập nhật thống kê cho admin
    if (userRole === 'admin') {
        const totalUsers = JSON.parse(localStorage.getItem('users') || '[]').length;
        
        document.getElementById('totalTasks').textContent = totalTasks;
        document.getElementById('pendingTasks').textContent = pendingTasks;
        document.getElementById('completedTasks').textContent = completedTasks;
        document.getElementById('totalUsers').textContent = totalUsers;
    }
    
    // cập nhật thống kê cho member
    if (userRole === 'member') {
        const overdueTasks = myTasks.filter(task => {
            const deadline = new Date(task.deadline);
            const today = new Date();
            return task.status === 'pending' && deadline < today;
        }).length;
        
        const todayTasks = myTasks.filter(task => {
            const deadline = new Date(task.deadline);
            const today = new Date();
            return task.status === 'pending' && 
                   deadline.toDateString() === today.toDateString();
        }).length;
        
        document.getElementById('myTotalTasks').textContent = totalTasks;
        document.getElementById('myPendingTasks').textContent = pendingTasks;
        document.getElementById('myCompletedTasks').textContent = completedTasks;
        document.getElementById('overdueTasks').textContent = overdueTasks;
        document.getElementById('todayTasks').textContent = todayTasks;
    }
}

// tính thời gian còn lại cho deadline
function calculateTimeRemaining(deadline) {
    const deadlineDate = new Date(deadline);
    const now = new Date();
    const diffTime = deadlineDate - now;
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
    
    if (diffDays < 0) {
        return {
            text: `Quá hạn ${Math.abs(diffDays)} ngày`,
            class: 'urgent'
        };
    } else if (diffDays === 0) {
        return {
            text: 'Hôm nay',
            class: 'urgent'
        };
    } else if (diffDays <= 3) {
        return {
            text: `Còn ${diffDays} ngày`,
            class: 'warning'
        };
    } else {
        return {
            text: `Còn ${diffDays} ngày`,
            class: 'normal'
        };
    }
}

// định dạng ngày tháng năm
function formatDate(dateString) {
    const date = new Date(dateString);
    return date.toLocaleDateString('vi-VN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit'
    });
}

// dùng để lọc tác vụ theo trạng thái
function filterTasks() {
    fetchTasks();
}

// Chỉ hiển thị form tạo task cho admin
function setupTaskForm() {
    const taskForm = document.getElementById('taskForm');
    const userRole = localStorage.getItem('user_role');
    
    if (taskForm) {
        if (userRole === 'admin') {
            taskForm.style.display = 'block';
            taskForm.onsubmit = handleCreateTask;
        } else {
            taskForm.style.display = 'none';
        }
    }
}

function handleCreateTask(e) {
    e.preventDefault();
    const user_id = localStorage.getItem('user_id');
    if (!user_id) {
        alert('Bạn phải đăng nhập!');
        return;
    }
    
    const title = document.getElementById('title').value.trim();
    const description = document.getElementById('description').value.trim();
    const deadline = document.getElementById('deadline').value;
    const assigned_to = document.getElementById('assigned_to').value;
    
    if (!title || !description || !deadline || !assigned_to) {
        alert('Vui lòng điền đầy đủ thông tin!');
        return;
    }
    
    console.log('Creating task with data:', {
        title, description, deadline, assigned_to, created_by: user_id
    });
    
    const data = {
        title: title,
        description: description,
        assigned_to: assigned_to,
        deadline: deadline,
        status: 'pending',
        created_by: user_id  // ID của user tạo task
    };
    
    fetch('http://localhost:5000/api/tasks', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(data)
    })
    .then(res => {
        console.log('Response status:', res.status);
        return res.json();
    })
    .then(resp => {
        console.log('Response:', resp);
        if (resp.error) {
            alert(resp.error);
        } else {
            alert('Tạo tác vụ thành công!');
            document.getElementById('taskForm').reset();
            fetchTasks();
        }
    })
    .catch(error => {
        console.error('Lỗi khi tạo tác vụ:', error);
        alert('Không thể tạo tác vụ');
    });
}

function updateStatus(id, oldStatus) {
    const user_id = localStorage.getItem('user_id');
    if (!user_id) {
        alert('Bạn phải đăng nhập!');
        return;
    }
    
    const newStatus = oldStatus === 'pending' ? 'done' : 'pending';
    const statusText = newStatus === 'done' ? 'hoàn thành' : 'chờ xử lý';
    
    if (!confirm(`Bạn có chắc muốn chuyển trạng thái sang "${statusText}"?`)) {
        return;
    }
    
    fetch(`http://localhost:5000/api/tasks/${id}`, {
        method: 'PUT',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({status: newStatus, changed_by: user_id, old_status: oldStatus})
    })
    .then(res => res.json())
    .then(resp => {
        if (resp.error) {
            alert(resp.error);
        } else {
            alert('Cập nhật trạng thái thành công!');
            fetchTasks();
        }
    })
    .catch(error => {
        console.error('Lỗi khi cập nhật trạng thái:', error);
        alert('Không thể cập nhật trạng thái');
    });
}

// Function để member tick checkbox hoàn thành task
function toggleTaskCompletion(taskId, isChecked, oldStatus) {
    const user_id = localStorage.getItem('user_id');
    if (!user_id) {
        alert('Bạn phải đăng nhập!');
        return;
    }
    // Nếu bỏ chọn (uncheck), hỏi xác nhận
    if (!isChecked) {
        if (!confirm('Bạn có chắc chắn muốn chuyển task này về trạng thái chưa hoàn thành?')) {
            setTimeout(() => {
                const checkbox = document.getElementById(`task_${taskId}`);
                if (checkbox) checkbox.checked = true;
            }, 0);
            return;
        }
    }

    
    // Gửi yêu cầu cập nhật trạng thái lên server (giống admin)
    const newStatus = isChecked ? 'done' : 'pending';
    fetch(`http://localhost:5000/api/tasks/${taskId}`, {
        method: 'PUT',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({status: newStatus, changed_by: user_id, old_status: oldStatus})
    })
    .then(res => res.json())
    .then(resp => {
        if (resp.error) {
            alert(resp.error);
            setTimeout(() => {
                const checkbox = document.getElementById(`task_${taskId}`);
                if (checkbox) checkbox.checked = !isChecked;
            }, 0);
        } // Không cần fetchTasks ở đây, sẽ tự động cập nhật qua WebSocket
    })
    .catch(error => {
        alert('Không thể cập nhật trạng thái');
        setTimeout(() => {
            const checkbox = document.getElementById(`task_${taskId}`);
            if (checkbox) checkbox.checked = !isChecked;
        }, 0);
    });
}

function fetchUsers() {
    console.log('Fetching users from API...');
    fetch('http://localhost:5000/api/users')
        .then(res => {
            console.log('Response status:', res.status);
            return res.json();
        })
        .then(users => {
            console.log('Users loaded:', users);
            
            // Lưu danh sách user vào localStorage để render tên
            localStorage.setItem('users', JSON.stringify(users));
            
            // Tìm element assigned_to
            const select = document.getElementById('assigned_to');
            console.log('Select element:', select);
            
            if (select) {
                select.innerHTML = '<option value="">Chọn người dùng</option>';
                users.forEach(u => {
                    const option = document.createElement('option');
                    option.value = u.user_id;
                    option.textContent = `${u.username} (${u.role})`;
                    select.appendChild(option);
                    console.log('Added option:', u.username, u.role);
                });
                console.log('Users dropdown populated with', users.length, 'users');
            } else {
                console.log('Select element not found - assigned_to, retrying in 500ms...');
                // Retry sau 500ms nếu element chưa có
                setTimeout(() => {
                    const retrySelect = document.getElementById('assigned_to');
                    if (retrySelect) {
                        retrySelect.innerHTML = '<option value="">Chọn người dùng</option>';
                        users.forEach(u => {
                            const option = document.createElement('option');
                            option.value = u.user_id;
                            option.textContent = `${u.username} (${u.role})`;
                            retrySelect.appendChild(option);
                            console.log('Added option (retry):', u.username, u.role);
                        });
                        console.log('Users dropdown populated (retry) with', users.length, 'users');
                    } else {
                        console.log('Select element still not found after retry');
                    }
                }, 500);
            }
        })
        .catch(error => {
            console.error('Lỗi khi tải danh sách người dùng:', error);
            alert('Không thể tải danh sách người dùng. Vui lòng kiểm tra backend server.');
        });
}

// Function để refresh dropdown users
function refreshUsersDropdown() {
    const users = JSON.parse(localStorage.getItem('users') || '[]');
    const select = document.getElementById('assigned_to');
    
    if (select && users.length > 0) {
        select.innerHTML = '<option value="">Chọn người dùng</option>';
        users.forEach(u => {
            const option = document.createElement('option');
            option.value = u.user_id;
            option.textContent = `${u.username} (${u.role})`;
            select.appendChild(option);
        });
        console.log('Users dropdown refreshed with', users.length, 'users');
    }
}

// Function để test dropdown
// function testDropdown() {
//     console.log('=== Testing Dropdown ===');
//     const select = document.getElementById('assigned_to');
//     console.log('Select element:', select);
    
//     if (select) {
//         console.log('Select options count:', select.options.length);
//         console.log('Select innerHTML:', select.innerHTML);
        
//         // Test với dữ liệu mẫu
//         const testUsers = [
//             {id: 2, username: 'Phúc', role: 'member'},
//             {id: 3, username: 'admin', role: 'admin'}
//         ];
        
//         select.innerHTML = '<option value="">Chọn người dùng</option>';
//         testUsers.forEach(u => {
//             const option = document.createElement('option');
//             option.value = u.id;
//             option.textContent = `${u.username} (${u.role})`;
//             select.appendChild(option);
//             console.log('Added test option:', u.username, u.role);
//         });
        
//         console.log('Test dropdown populated');
//         alert('Test dropdown completed! Check console for details.');
//     } else {
//         console.log('Select element not found!');
//         alert('Select element not found! Check console for details.');
//     }
// }

// Function để test tạo task
// function testCreateTask() {
//     console.log('=== Testing Create Task ===');
    
//     const testData = {
//         title: 'Test Task',
//         description: 'This is a test task',
//         deadline: '2025-07-25',
//         assigned_to: '2', // Phúc (member)
//         status: 'pending',
//         created_by: '3' // admin
//     };
    
//     console.log('Test data:', testData);
    
//     fetch('http://localhost:5000/api/tasks', {
//         method: 'POST',
//         headers: {'Content-Type': 'application/json'},
//         body: JSON.stringify(testData)
//     })
//     .then(res => {
//         console.log('Response status:', res.status);
//         return res.json();
//     })
//     .then(resp => {
//         console.log('Response:', resp);
//         if (resp.error) {
//             alert('Error: ' + resp.error);
//         } else {
//             alert('Test task created successfully!');
//             fetchTasks();
//         }
//     })
//     .catch(error => {
//         console.error('Error:', error);
//         alert('Error creating test task: ' + error.message);
//     });
// }

function login(event) {
    event.preventDefault();
    const username = document.getElementById('loginUsername').value.trim();
    const password = document.getElementById('loginPassword').value;
    
    if (!username || !password) {
        alert('Vui lòng nhập đầy đủ thông tin!');
        return;
    }
    
    fetch('http://localhost:5000/api/login', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({username, password})
    })
    .then(res => res.json())
    .then(user => {
        if (user.error) {
            alert(user.error);
        } else {
            localStorage.setItem('user_id', user.user_id);
            localStorage.setItem('user_role', user.role);
            localStorage.setItem('username', user.username);
            
            // Redirect dựa trên role
            if (user.role === 'admin') {
                window.location.href = '/frontend/admin/index.html';
            } else {
                window.location.href = '/frontend/member/index.html';
            }
        }
    })
    .catch(error => {
        console.error('Lỗi đăng nhập:', error);
        alert('Không thể kết nối đến server');
    });
}

function logout() {
    if (confirm('Bạn có chắc muốn đăng xuất?')) {
        localStorage.clear();
        window.location.href = "/frontend/index.html";
    }
}

function register(event) {
    event.preventDefault();
    const password = document.getElementById('registerPassword').value;
    const confirmPassword = document.getElementById('registerConfirmPassword').value;

    if (password !== confirmPassword) {
        alert('Mật khẩu xác nhận không khớp!');
        return;
    }

    const data = {
        username: document.getElementById('registerUsername').value.trim(),
        password: password,
        full_name: document.getElementById('registerFullName').value.trim(),
        email: document.getElementById('registerEmail').value.trim(),
        dob: document.getElementById('registerDob').value,
        phone: document.getElementById('registerPhone').value.trim(),
        gender: document.getElementById('registerGender').value
    };
    
    // Validation 
    if (!data.full_name || !data.email || !data.username || !data.password || 
        !data.dob || !data.phone || !data.gender) {
        alert('Vui lòng điền đầy đủ thông tin!');
        return;
    }
    
    if (data.password.length < 8) {
        alert('Mật khẩu phải có ít nhất 8 ký tự!');
        return;
    }
    
    fetch('http://localhost:5000/api/register', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(data)
    })
    .then(res => res.json())
    .then(result => {
        if (result.error) {
            alert(result.error);
        } else {
            alert(result.message);
            window.location.href = 'login.html';
        }
    })
    .catch(error => {
        console.error('Lỗi đăng ký:', error);
        alert('Không thể kết nối đến server');
    });
}

// Notification functions
let notifications = [];
let unreadCount = 0;

// Toggle notification menu
function toggleNotificationMenu() {
    const menu = document.getElementById('notificationMenu');
    if (menu) {
        menu.classList.toggle('show');
        if (menu.classList.contains('show')) {
            loadNotifications();
        }
    }
}

// Load notifications from server
function loadNotifications() {
    const userId = localStorage.getItem('user_id');
    if (!userId) {
        console.log('No user ID found, skipping notification load');
        return;
    }

    console.log('Loading notifications for user:', userId);

    fetch(`http://localhost:5000/api/notifications/${userId}`)
        .then(res => res.json())
        .then(data => {
            console.log('Notifications loaded:', data);
            if (Array.isArray(data)) {
                notifications = data;
                unreadCount = notifications.filter(n => !n.is_read).length;
                console.log('Unread count:', unreadCount);
                renderNotifications();
                updateNotificationBadge();
            }
        })
        .catch(error => {
            console.error('Error loading notifications:', error);
        });
}

// Render notifications in the menu
function renderNotifications() {
    const list = document.getElementById('notificationList');
    if (!list) return;

    if (notifications.length === 0) {
        list.innerHTML = `
            <div class="empty-notifications">
                <i class="fas fa-bell-slash"></i>
                <p>Không có thông báo nào</p>
            </div>
        `;
        return;
    }

    list.innerHTML = notifications.map(notification => `
        <div class="notification-item${notification.is_read ? ' read' : ''}" 
             onclick="${!notification.is_read ? `markNotificationRead(${notification.notification_id})` : ''}">
            ${!notification.is_read ? `
                <div class="notification-icon">
                    <i class="fas fa-info-circle"></i>
                </div>
            ` : `<div class="notification-icon"></div>`}
            <div class="notification-content">
                <div class="notification-text">${notification.content}</div>
                <div class="notification-time">${formatNotificationTime(notification.created_at)}</div>
                <div class="notification-actions">
                    ${notification.is_read
                        ? `<span class="notification-read-label">Đã đọc</span>`
                        : `<button class="notification-action" onclick="event.stopPropagation(); markNotificationRead(${notification.notification_id})">
                                <i class="fas fa-check"></i> Đánh dấu đã đọc
                           </button>`
                    }
                    <button class="notification-action delete" onclick="event.stopPropagation(); deleteNotification(${notification.notification_id})">
                        <i class="fas fa-trash"></i> Xóa
                    </button>
                </div>
            </div>
        </div>
    `).join('');
}

// Update notification badge
function updateNotificationBadge() {
    const badge = document.getElementById('notificationBadge');
    console.log('Updating notification badge, unread count:', unreadCount);
    if (badge) {
        badge.textContent = unreadCount;
        badge.style.display = unreadCount > 0 ? 'flex' : 'none';
        console.log('Badge updated successfully');
    } else {
        console.log('Notification badge element not found');
    }
}

// Mark notification as read
function markNotificationRead(notificationId) {
    fetch(`http://localhost:5000/api/notifications/${notificationId}/read`, {
        method: 'PUT'
    })
    .then(res => res.json())
    .then(data => {
        if (data.success) {
            const notification = notifications.find(n => n.notification_id === notificationId);
            if (notification && !notification.is_read) {
                notification.is_read = true;
                unreadCount--;
                renderNotifications();
                updateNotificationBadge();
            }
        }
    })
    .catch(error => {
        console.error('Error marking notification as read:', error);
    });
}

// Mark all notifications as read
function markAllNotificationsRead() {
    const userId = localStorage.getItem('user_id');
    if (!userId) return;

    fetch(`http://localhost:5000/api/notifications/${userId}/read-all`, {
        method: 'PUT'
    })
    .then(res => res.json())
    .then(data => {
        if (data.success) {
            notifications.forEach(n => n.is_read = true);
            unreadCount = 0;
            renderNotifications();
            updateNotificationBadge();
        }
    })
    .catch(error => {
        console.error('Error marking all notifications as read:', error);
    });
}

// Delete notification
function deleteNotification(notificationId) {
    if (!confirm('Bạn có chắc muốn xóa thông báo này?')) return;

    fetch(`http://localhost:5000/api/notifications/${notificationId}`, {
        method: 'DELETE'
    })
    .then(res => res.json())
    .then(data => {
        if (data.success) {
            const index = notifications.findIndex(n => n.notification_id === notificationId);
            if (index > -1) {
                if (!notifications[index].is_read) {
                    unreadCount--;
                }
                notifications.splice(index, 1);
                renderNotifications();
                updateNotificationBadge();
            }
        }
    })
    .catch(error => {
        console.error('Error deleting notification:', error);
    });
}

// Format notification time
function formatNotificationTime(dateString) {
    const date = new Date(dateString);
    const now = new Date();
    const diff = now - date;
    
    const minutes = Math.floor(diff / 60000);
    const hours = Math.floor(diff / 3600000);
    const days = Math.floor(diff / 86400000);
    
    if (minutes < 1) return 'Vừa xong';
    if (minutes < 60) return `${minutes} phút trước`;
    if (hours < 24) return `${hours} giờ trước`;
    if (days < 7) return `${days} ngày trước`;
    
    return date.toLocaleDateString('vi-VN');
}

// Create notification (for testing)
function createNotification(userId, content) {
    fetch('http://localhost:5000/api/notifications', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({ user_id: userId, content: content })
    })
    .then(res => res.json())
    .then(data => {
        if (data.success) {
            loadNotifications();
        }
    })
    .catch(error => {
        console.error('Error creating notification:', error);
    });
}

// kiểm tra thông báo
function testNotification() {
    const userId = localStorage.getItem('user_id');
    if (!userId) {
        alert('Bạn cần đăng nhập để test thông báo!');
        return;
    }
    
    const testMessages = [
        'Đây là thông báo test từ hệ thống!',
        'Bạn có tác vụ mới cần xử lý.',
        'Deadline của tác vụ sắp đến hạn.',
        'Có cập nhật mới về dự án của bạn.'
    ];
    
    const randomMessage = testMessages[Math.floor(Math.random() * testMessages.length)];
    createNotification(userId, randomMessage);
}

// Check notification elements
function checkNotificationElements() {
    const notificationBtn = document.querySelector('.notification-btn');
    const notificationBadge = document.getElementById('notificationBadge');
    const notificationMenu = document.getElementById('notificationMenu');
    
    console.log('=== Notification Elements Check ===');
    console.log('Notification button:', notificationBtn);
    console.log('Notification badge:', notificationBadge);
    console.log('Notification menu:', notificationMenu);
    
    if (notificationBtn) {
        console.log('Notification button styles:', window.getComputedStyle(notificationBtn));
    }
    
    return {
        button: !!notificationBtn,
        badge: !!notificationBadge,
        menu: !!notificationMenu
    };
}

// Close notification menu when clicking outside
document.addEventListener('click', function(event) {
    const notificationDropdown = document.querySelector('.notification-dropdown');
    const notificationMenu = document.getElementById('notificationMenu');
    
    if (notificationDropdown && notificationMenu && !notificationDropdown.contains(event.target)) {
        notificationMenu.classList.remove('show');
    }
});

// Khởi tạo ứng dụng
window.onload = function() {
    console.log('Window loaded');
    const user_id = localStorage.getItem('user_id');
    const userRole = localStorage.getItem('user_role');
    const currentPath = window.location.pathname;
    
    console.log('User ID:', user_id);
    console.log('User Role:', userRole);
    console.log('Current Path:', currentPath);
    
    // Thêm event listener để đóng user menu khi click outside
    document.addEventListener('click', function(event) {
        const userDropdown = document.querySelector('.user-dropdown');
        const userMenu = document.getElementById('userMenu');
        
        if (userDropdown && userMenu && !userDropdown.contains(event.target)) {
            userMenu.classList.remove('show');
        }
    });
    
    // Kiểm tra quyền truy cập trước tiên
    if (user_id && userRole) {
        // Đã đăng nhập - kiểm tra quyền truy cập
        if (!checkAccess()) {
            console.log('Access denied, redirecting...');
            return; // checkAccess() sẽ tự redirect
        }
        
        console.log('Access granted, loading data...');
        
        // Đảm bảo DOM đã load xong trước khi gọi API
        setTimeout(() => {
            console.log('DOM ready, loading data...');
            fetchUsers();
            fetchTasks();
            setupTaskForm();
            loadNotifications(); // Load notifications
            
            // Kiểm tra notification elements
            setTimeout(() => {
                checkNotificationElements();
            }, 500);
        }, 100);
        
        // Hiển thị thông tin user
        const username = localStorage.getItem('username');
        const welcomeElement = document.querySelector('h1');
        if (welcomeElement && !welcomeElement.textContent.includes('Dashboard')) {
            welcomeElement.textContent = `Chào mừng ${username} (${userRole})!`;
        }
    } else {
        // Chưa đăng nhập
        if (currentPath.includes('/admin/') || currentPath.includes('/member/')) {
            // Nếu đang ở trang dashboard mà chưa đăng nhập
            alert('Bạn cần đăng nhập để truy cập trang này!');
            window.location.href = '/frontend/index.html';
            return;
        }
        
        // Nếu đang ở trang auth hoặc trang chủ thì OK
        if (!currentPath.includes('/auth/') && currentPath !== '/frontend/index.html') {
            window.location.href = '/frontend/index.html';
        }
    }
};