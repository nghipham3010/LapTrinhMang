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
            cursor.execute('SELECT role FROM Users WHERE id=?', (created_by,))
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
        cursor.execute('SELECT id FROM Users WHERE id=?', (assigned_to,))
        assigned_user = cursor.fetchone()
        
        if not assigned_user:
            conn.close()
            return jsonify({'error': 'User được assign không tồn tại'}), 403
        
        # Tạo task
        title = data.get('title')
        description = data.get('description')
        status = data.get('status')
        deadline = data.get('deadline')
        
        cursor.execute('INSERT INTO Tasks (title, description, status, deadline, assigned_to) VALUES (?, ?, ?, ?, ?)',
                       (title, description, status, deadline, assigned_to))
        conn.commit()
        conn.close()
        
        # Thông báo real-time update
        notify_task_update(None, {'action': 'add'})
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
    cursor.execute('SELECT role FROM Users WHERE id=?', user_id)
    row = cursor.fetchone()
    if not row:
        conn.close()
        return jsonify({'error': 'User not found'}), 403
    role = row[0]
    if role != 'admin':
        # Chỉ cho phép member cập nhật tác vụ mình được giao
        cursor.execute('SELECT assigned_to FROM Tasks WHERE id=?', task_id)
        assigned_row = cursor.fetchone()
        if not assigned_row or str(assigned_row[0]) != str(user_id):
            conn.close()
            return jsonify({'error': 'Bạn chỉ được cập nhật tác vụ của mình'}), 403
    # Lưu lịch sử thay đổi
    cursor.execute('UPDATE Tasks SET status=? WHERE id=?', data.get('status'), task_id)
    cursor.execute('INSERT INTO TaskHistory (task_id, changed_by, old_status, new_status, changed_at) VALUES (?, ?, ?, ?, ?)',
                   task_id, user_id, data.get('old_status'), data.get('status'), datetime.datetime.now())
    conn.commit()
    conn.close()
    notify_task_update(task_id, {'action': 'update', 'status': data.get('status')})
    return jsonify({'success': True})

# API: Lấy danh sách user
@app.route('/api/users', methods=['GET'])
def get_users():
    conn = get_connection()
    cursor = conn.cursor()
    cursor.execute('SELECT id, username, role FROM Users')
    users = [dict_from_cursor(cursor, row) for row in cursor.fetchall()]
    conn.close()
    return jsonify(users)

# API: Đăng nhập
@app.route('/api/login', methods=['POST'])
def login():
    data = request.get_json()
    username = data.get('username')
    password = data.get('password')
    conn = get_connection()
    cursor = conn.cursor()
    cursor.execute('SELECT id, username, role FROM Users WHERE username=? AND password=?', username, password)
    row = cursor.fetchone()
    conn.close()
    if row:
        return jsonify({'id': row[0], 'username': row[1], 'role': row[2]})
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
    cursor.execute('SELECT id FROM Users WHERE username=? OR email=?', username, email)
    if cursor.fetchone():
        conn.close()
        return jsonify({'error': 'Tên đăng nhập hoặc email đã tồn tại'}), 409
    cursor.execute('''
        INSERT INTO Users (username, password, role, full_name, email, dob, phone, gender)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?)
    ''', username, password, 'member', full_name, email, dob, phone, gender)
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
        cursor.execute('SELECT assigned_to FROM Tasks WHERE id=?', task_id)
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

# WebSocket: Lắng nghe kết nối
@socketio.on('connect')
def handle_connect():
    print('Client connected')

@socketio.on('disconnect')
def handle_disconnect():
    print('Client disconnected')

if __name__ == '__main__':
    socketio.run(app, host='0.0.0.0', port=5000, debug=True)