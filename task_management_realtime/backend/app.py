from flask import Flask, request, jsonify
from flask_socketio import SocketIO
from db import get_connection
from models import Task, TaskHistory, User
from websocket_manager import socketio, notify_task_update
from utils import dict_from_cursor
import datetime
import re

from flask_cors import CORS

app = Flask(__name__)
CORS(app)  # Cho phép CORS để frontend có thể gọi API
socketio.init_app(app)


# API: Lấy danh sách tác vụ
def fetch_tasks():
    conn = get_connection()
    cursor = conn.cursor()
    cursor.execute('SELECT * FROM Tasks')
    tasks = [dict_from_cursor(cursor, row) for row in cursor.fetchall()]
    conn.close()
    return tasks

@app.route('/api/tasks', methods=['GET'])
def get_tasks():
    return jsonify(fetch_tasks())

# API: Thêm tác vụ mới
@app.route('/api/tasks', methods=['POST'])
def add_task():
    try:
        data = request.get_json()
        
        if not data:
            return jsonify({'error': 'No data provided'}), 400
        
        # Lấy thông tin user tạo task từ request
        created_by = data.get('created_by')  # ID của user tạo task
        assigned_to = data.get('assigned_to')  # ID của user được assign
        
        if not assigned_to:
            return jsonify({'error': 'assigned_to is required'}), 400
        
        # Kiểm tra user tạo task có phải admin không
        if created_by:
            conn = get_connection()
            cursor = conn.cursor()
            cursor.execute('SELECT role FROM Users WHERE user_id=?', (created_by,))
            row = cursor.fetchone()
            
            if not row:
                conn.close()
                return jsonify({'error': 'User tạo task không tồn tại'}), 403
            
            creator_role = row[0]
            
            if creator_role != 'admin':
                conn.close()
                return jsonify({'error': 'Chỉ admin được thêm tác vụ'}), 403
            conn.close()
        
        # Kiểm tra user được assign có tồn tại không
        conn = get_connection()
        cursor = conn.cursor()
        cursor.execute('SELECT user_id FROM Users WHERE user_id=?', (assigned_to,))
        assigned_user = cursor.fetchone()
        
        if not assigned_user:
            conn.close()
            return jsonify({'error': 'User được assign không tồn tại'}), 403
        
        # Tạo task 

        title = data.get('title')
        description = data.get('description')
        status = data.get('status')
        deadline = data.get('deadline')
        
        # Insert vào database (SSMS) trước để lấy task_id
        cursor.execute('INSERT INTO Tasks (title, description, status, deadline, assigned_to) VALUES (?, ?, ?, ?, ?)',
                       (title, description, status, deadline, assigned_to))
        
        # Tạo thông báo cho user được assign task 
        cursor.execute('INSERT INTO Notifications (user_id, content, is_read, created_at) VALUES (?, ?, 0, GETDATE())', 
                    assigned_to, f'Bạn có tác vụ mới: "{title}" - Deadline: {deadline}')
        
        conn.commit()
        conn.close()
        
        # Gửi thông báo realtime cho member
        notify_task_update(None, {
            'action': 'new_task',
            'user_id': assigned_to,
            'content': f'Bạn có tác vụ mới: "{title}" - Deadline: {deadline}'
        })

        return jsonify({'success': True})
        
    except Exception as e:
        print("ERROR in add_task:", str(e))
        import traceback
        traceback.print_exc()
        return jsonify({'error': f'Server error: {str(e)}'}), 500

# API: Cập nhật trạng thái tác vụ
@app.route('/api/tasks/<int:task_id>', methods=['PUT'])
def update_task(task_id):
    data = request.get_json()
    if not data:
        return jsonify({'error': 'No data provided'}), 400
    user_id = data.get('changed_by')
    if not user_id:
        return jsonify({'error': 'changed_by is required'}), 400
    conn = get_connection()
    cursor = conn.cursor()
    cursor.execute('SELECT role FROM Users WHERE user_id=?', user_id)
    row = cursor.fetchone()
    if not row:
        conn.close()
        return jsonify({'error': 'User not found'}), 403
    role = row[0]
    if role != 'admin':
        # Chỉ cho phép member cập nhật tác vụ mình được giao
        cursor.execute('SELECT assigned_to FROM Tasks WHERE task_id=?', task_id)
        assigned_row = cursor.fetchone()
        if not assigned_row or str(assigned_row[0]) != str(user_id):
            conn.close()
            return jsonify({'error': 'Bạn chỉ được cập nhật tác vụ của mình'}), 403
    # Lưu lịch sử thay đổi
    cursor.execute('UPDATE Tasks SET status=? WHERE task_id=?', data.get('status'), task_id)
    cursor.execute('INSERT INTO TaskHistory (task_id, changed_by, old_status, new_status, changed_at) VALUES (?, ?, ?, ?, ?)',
                   task_id, user_id, data.get('old_status'), data.get('status'), datetime.datetime.now());
    
    # Lấy thông tin task để tạo thông báo
    cursor.execute('SELECT title, assigned_to FROM Tasks WHERE task_id=?', task_id)
    task_info = cursor.fetchone()
    
    if task_info:
        title, assigned_to = task_info
        new_status = data.get('status')
        old_status = data.get('old_status')
        
        # Tạo thông báo cho admin khi member cập nhật trạng thái
        if role == 'member':
            cursor.execute('SELECT username FROM Users WHERE user_id=?', user_id)
            member_name = cursor.fetchone()[0]
            cursor.execute('INSERT INTO Notifications (user_id, content, is_read, created_at) VALUES (?, ?, 0, GETDATE())', 
                        1, f'Member {member_name} đã cập nhật trạng thái tác vụ "{title}" từ {old_status} thành {new_status}')
        
        # Tạo thông báo cho member khi admin cập nhật trạng thái
        elif role == 'admin' and assigned_to != user_id:
            cursor.execute('INSERT INTO Notifications (user_id, content, is_read, created_at) VALUES (?, ?, 0, GETDATE())', 
                        assigned_to, f'Admin đã cập nhật trạng thái tác vụ "{title}" thành {new_status}')
    
    conn.commit()
    conn.close()
    notify_task_update(task_id, {'action': 'update', 'status': data.get('status')})
    return jsonify({'success': True})

# API: Xóa tác vụ (chỉ admin)
@app.route('/api/tasks/<int:task_id>', methods=['DELETE'])
def delete_task(task_id):
    admin_id = request.args.get('admin_id')
    if not admin_id:
        return jsonify({'error': 'admin_id is required'}), 400
    
    conn = get_connection()
    cursor = conn.cursor()
    
    # Kiểm tra quyền admin
    cursor.execute('SELECT role FROM Users WHERE user_id=?', (admin_id,))
    row = cursor.fetchone()
    if not row or row[0] != 'admin':
        conn.close()
        return jsonify({'error': 'Chỉ admin được xóa tác vụ'}), 403
    
    # Kiểm tra tác vụ có tồn tại không
    cursor.execute('SELECT title, assigned_to FROM Tasks WHERE task_id=?', (task_id,))
    task_row = cursor.fetchone()
    if not task_row:
        conn.close()
        return jsonify({'error': 'Tác vụ không tồn tại'}), 404
    
    task_title, assigned_to = task_row
    
    # Xóa tác vụ
    cursor.execute('DELETE FROM Tasks WHERE task_id=?', (task_id,))
    
    # Xóa lịch sử tác vụ
    cursor.execute('DELETE FROM TaskHistory WHERE task_id=?', (task_id,))
    
    # Tạo thông báo cho member được giao tác vụ
    if assigned_to:
        cursor.execute('INSERT INTO Notifications (user_id, content, is_read, created_at) VALUES (?, ?, 0, GETDATE())', 
                    assigned_to, f'Tác vụ "{task_title}" đã được xóa bởi admin')
    
    conn.commit()
    conn.close()
    
    # Gửi thông báo realtime
    notify_task_update(task_id, {
        'action': 'delete',
        'user_id': assigned_to,
        'content': f'Tác vụ "{task_title}" đã được xóa'
    })
    
    return jsonify({'success': True})

# API: Lấy danh sách user
@app.route('/api/users', methods=['GET'])
def get_users():
    conn = get_connection()
    cursor = conn.cursor()
    cursor.execute('SELECT user_id, username, full_name, email, dob, phone, gender, role FROM Users')
    users = [dict_from_cursor(cursor, row) for row in cursor.fetchall()]
    conn.close()
    return jsonify(users)

# API: Lấy chi tiết user
@app.route('/api/users/<int:user_id>', methods=['GET'])
def get_user_detail(user_id):
    conn = get_connection()
    cursor = conn.cursor()
    cursor.execute('SELECT user_id, username, full_name, email, dob, phone, gender, role FROM Users WHERE user_id=?', (user_id,))
    row = cursor.fetchone()
    conn.close()
    if row:
        return jsonify(dict_from_cursor(cursor, row))
    else:
        return jsonify({'error': 'User not found'}), 404

# API: Thêm user (chỉ admin)
@app.route('/api/users', methods=['POST'])
def admin_add_user():
    data = request.get_json()
    admin_id = data.get('admin_id')
    if not admin_id:
        return jsonify({'error': 'admin_id is required'}), 400
    conn = get_connection()
    cursor = conn.cursor()
    cursor.execute('SELECT role FROM Users WHERE user_id=?', (admin_id,))
    row = cursor.fetchone()
    if not row or row[0] != 'admin':
        conn.close()
        return jsonify({'error': 'Chỉ admin được thêm user'}), 403
    username = data.get('username')
    password = data.get('password')
    full_name = data.get('full_name')
    email = data.get('email')
    dob = data.get('dob')
    phone = data.get('phone')
    gender = data.get('gender')
    role = data.get('role', 'member')
    if not username or not password or not full_name or not email or not dob or not phone or not gender:
        conn.close()
        return jsonify({'error': 'Thiếu thông tin'}), 400
    if not is_valid_password(password):
        conn.close()
        return jsonify({'error': 'Mật khẩu phải tối thiểu 8 ký tự, gồm chữ và số.'}), 400
    cursor.execute('SELECT user_id FROM Users WHERE username=? OR email=?', (username, email))
    if cursor.fetchone():
        conn.close()
        return jsonify({'error': 'Tên đăng nhập hoặc email đã tồn tại'}), 409
    cursor.execute('INSERT INTO Users (username, password, role, full_name, email, dob, phone, gender) VALUES (?, ?, ?, ?, ?, ?, ?, ?)', 
                    (username, password, role, full_name, email, dob, phone, gender))
    conn.commit()
    conn.close()
    return jsonify({'success': True, 'message': 'Thêm user thành công!'})

# API: Sửa user (chỉ admin)
@app.route('/api/users/<int:user_id>', methods=['PUT'])
def admin_update_user(user_id):
    data = request.get_json()
    admin_id = data.get('admin_id') if data else None
    if not admin_id:
        return jsonify({'error': 'admin_id is required'}), 400
    conn = get_connection()
    cursor = conn.cursor()
    cursor.execute('SELECT role FROM Users WHERE user_id=?', (admin_id,))
    row = cursor.fetchone()
    if not row or row[0] != 'admin':
        conn.close()
        return jsonify({'error': 'Chỉ admin được sửa user'}), 403
    # Chỉ cho phép sửa các trường cơ bản, không sửa user_id
    fields = ['username', 'password', 'full_name', 'email', 'dob', 'phone', 'gender', 'role']
    updates = []
    values = []
    for field in fields:
        if field in data:
            updates.append(f"{field}=?")
            values.append(data[field])
    if not updates:
        conn.close()
        return jsonify({'error': 'Không có trường nào để cập nhật'}), 400
    values.append(user_id)
    cursor.execute(f'UPDATE Users SET {", ".join(updates)} WHERE user_id=?', tuple(values))
    conn.commit()
    conn.close()
    return jsonify({'success': True, 'message': 'Cập nhật user thành công!'})

# API: Xóa user (chỉ admin)
@app.route('/api/users/<int:user_id>', methods=['DELETE'])
def admin_delete_user(user_id):
    # Lấy admin_id từ query params để xác thực quyền
    data = request.get_json()
    admin_id = data.get('admin_id') if data else None 
    if not admin_id:
        return jsonify({'error': 'admin_id is required'}), 400
    conn = get_connection()
    cursor = conn.cursor()
    cursor.execute('SELECT role FROM Users WHERE user_id=?', (admin_id,))
    row = cursor.fetchone()
    if not row or row[0] != 'admin':
        conn.close()
        return jsonify({'error': 'Chỉ admin được xóa user'}), 403
    cursor.execute('DELETE FROM Users WHERE user_id=?', (user_id,))
    conn.commit()
    conn.close()
    return jsonify({'success': True, 'message': 'Xóa user thành công!'})

# API: Đăng nhập
@app.route('/api/login', methods=['POST'])
def login():
    data = request.get_json()
    username = data.get('username')
    password = data.get('password')
    conn = get_connection()
    cursor = conn.cursor()
    cursor.execute('SELECT user_id, username, role FROM Users WHERE username=? AND password=?', username, password)
    row = cursor.fetchone()
    conn.close()
    if row:
        return jsonify({'user_id': row[0], 'username': row[1], 'role': row[2]})
    else:
        return jsonify({'error': 'Sai tài khoản hoặc mật khẩu'}), 401

def is_valid_password(password):
    # Tối thiểu 8 ký tự, gồm chữ và số, cho phép ký tự đặc biệt
    return bool(re.match(r'^(?=.*[A-Za-z])(?=.*\d).{8,}$', password))

# API: Đăng ký tài khoản
@app.route('/api/register', methods=['POST'])
def register():
    data = request.get_json()
    username = data.get('username')
    password = data.get('password')
    full_name = data.get('full_name')
    email = data.get('email')
    dob = data.get('dob')
    phone = data.get('phone')
    gender = data.get('gender')
    if not username or not password or not full_name or not email or not dob or not phone or not gender:
        return jsonify({'error': 'Thiếu thông tin'}), 400
    if not is_valid_password(password):
        return jsonify({'error': 'Mật khẩu phải tối thiểu 8 ký tự, gồm chữ và số.'}), 400
    conn = get_connection()
    cursor = conn.cursor()
    cursor.execute('SELECT user_id FROM Users WHERE username=? OR email=?', username, email)
    if cursor.fetchone():
        conn.close()
        return jsonify({'error': 'Tên đăng nhập hoặc email đã tồn tại'}), 409
    cursor.execute('INSERT INTO Users (username, password, role, full_name, email, dob, phone, gender) VALUES (?, ?, ?, ?, ?, ?, ?, ?)', 
                    username, password, 'member', full_name, email, dob, phone, gender)
    # Lấy user_id vừa tạo
    cursor.execute('SELECT user_id FROM Users WHERE username=?', (username,))
    new_user = cursor.fetchone()
    if new_user:
        new_user_id = new_user[0]
        # Thêm thông báo chào mừng
        welcome_msg = 'Chào mừng bạn đến với hệ thống quản lý tác vụ!'
        cursor.execute('INSERT INTO Notifications (user_id, content, is_read, created_at) VALUES (?, ?, 0, GETDATE())', (new_user_id, welcome_msg))
    conn.commit()
    conn.close()
    return jsonify({'success': True, 'message': 'Đăng ký thành công!'})

# API: Member báo cáo hoàn thành task (chỉ thông báo, không cập nhật DB)
@app.route('/api/tasks/<int:task_id>/member-completion', methods=['POST'])
def member_completion(task_id):
    try:
        data = request.get_json()
        member_id = data.get('member_id')
        is_completed = data.get('is_completed')
        
        if not member_id:
            return jsonify({'error': 'member_id is required'}), 400
        
        # Kiểm tra task có tồn tại và được assign cho member này không
        conn = get_connection()
        cursor = conn.cursor()
        cursor.execute('SELECT assigned_to FROM Tasks WHERE task_id=?', task_id)
        task = cursor.fetchone()
        
        if not task:
            conn.close()
            return jsonify({'error': 'Task không tồn tại'}), 404
        
        if task[0] != member_id:
            conn.close()
            return jsonify({'error': 'Bạn chỉ được báo cáo task của mình'}), 403
        
        conn.close()
        
        # Gửi thông báo real-time cho admin
        completion_status = 'hoàn thành' if is_completed else 'chưa hoàn thành'
        notify_task_update(task_id, {
            'action': 'member_completion',
            'member_id': member_id,
            'is_completed': is_completed,
            'message': f'Member đã báo cáo task {completion_status}'
        })
        
        return jsonify({'success': True, 'message': f'Đã báo cáo {completion_status}'})
        
    except Exception as e:
        print("ERROR in member_completion:", str(e))
        import traceback
        traceback.print_exc()
        return jsonify({'error': f'Server error: {str(e)}'}), 500

# API: Lấy danh sách thông báo của user
@app.route('/api/notifications/<int:user_id>', methods=['GET'])
def get_notifications(user_id):
    try:
        conn = get_connection()
        cursor = conn.cursor()
        cursor.execute('SELECT notification_id, content, is_read, created_at FROM Notifications WHERE user_id = ? ORDER BY created_at DESC', (user_id,))
        notifications = [dict_from_cursor(cursor, row) for row in cursor.fetchall()]
        conn.close()
        return jsonify(notifications)
    except Exception as e:
        print("ERROR in get_notifications:", str(e))
        return jsonify({'error': f'Server error: {str(e)}'}), 500

# API: Đánh dấu thông báo đã đọc
@app.route('/api/notifications/<int:notification_id>/read', methods=['PUT'])
def mark_notification_read(notification_id):
    try:
        conn = get_connection()
        cursor = conn.cursor()
        cursor.execute('UPDATE Notifications SET is_read = 1 WHERE notification_id = ?', (notification_id,))
        conn.commit()
        conn.close()
        return jsonify({'success': True})
    except Exception as e:
        print("ERROR in mark_notification_read:", str(e))
        return jsonify({'error': f'Server error: {str(e)}'}), 500

# API: Đánh dấu tất cả thông báo đã đọc
@app.route('/api/notifications/<int:user_id>/read-all', methods=['PUT'])
def mark_all_notifications_read(user_id):
    try:
        conn = get_connection()
        cursor = conn.cursor()
        cursor.execute('UPDATE Notifications SET is_read = 1 WHERE user_id = ?', (user_id,))
        conn.commit()
        conn.close()
        return jsonify({'success': True})
    except Exception as e:
        print("ERROR in mark_all_notifications_read:", str(e))
        return jsonify({'error': f'Server error: {str(e)}'}), 500

# API: Xóa thông báo
@app.route('/api/notifications/<int:notification_id>', methods=['DELETE'])
def delete_notification(notification_id):
    try:
        conn = get_connection()
        cursor = conn.cursor()
        cursor.execute('DELETE FROM Notifications WHERE notification_id = ?', (notification_id,))
        conn.commit()
        conn.close()
        return jsonify({'success': True})
    except Exception as e:
        print("ERROR in delete_notification:", str(e))
        return jsonify({'error': f'Server error: {str(e)}'}), 500

# API: Tạo thông báo mới
@app.route('/api/notifications', methods=['POST'])
def create_notification():
    try:
        data = request.get_json()
        user_id = data.get('user_id')
        content = data.get('content')
        
        if not user_id or not content:
            return jsonify({'error': 'user_id và content là bắt buộc'}), 400
        
        conn = get_connection()
        cursor = conn.cursor()
        cursor.execute('INSERT INTO Notifications (user_id, content, is_read, created_at) VALUES (?, ?, 0, GETDATE())', (user_id, content))
        conn.commit()
        conn.close()
        
        return jsonify({'success': True})
    except Exception as e:
        print("ERROR in create_notification:", str(e))
        return jsonify({'error': f'Server error: {str(e)}'}), 500

# WebSocket: Lắng nghe kết nối
@socketio.on('connect')
def handle_connect():
    print('Client connected')

@socketio.on('disconnect')
def handle_disconnect():
    print('Client disconnected')

if __name__ == '__main__':
    socketio.run(app, host='0.0.0.0', port=5000, debug=True)