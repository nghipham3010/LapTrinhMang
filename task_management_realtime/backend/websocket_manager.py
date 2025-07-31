from flask_socketio import SocketIO, emit, join_room, leave_room

socketio = SocketIO(cors_allowed_origins="*")
 
# Gửi thông báo trạng thái tác vụ thay đổi
def notify_task_update(task_id, data):
    socketio.emit('task_update', {'task_id': task_id, 'data': data}) 

# Gửi thông báo trạng thái tác vụ được tạo
def notify_task_created(task_id, data):
    socketio.emit('task_created', {'task_id': task_id, 'data': data})

# Gửi thông báo trạng thái tác vụ được xóa
def notify_task_deleted(task_id):
    socketio.emit('task_deleted', {'task_id': task_id})

# Gửi thông báo trạng thái tác vụ được cập nhật
def notify_task_status_updated(task_id, status):
    socketio.emit('task_status_updated', {'task_id': task_id, 'status': status})

# Gửi thông báo trạng thái tác vụ được gán
def notify_task_assigned(task_id, user_id):
    socketio.emit('task_assigned', {'task_id': task_id, 'user_id': user_id})

# Gửi thông báo trạng thái tác vụ được gỡ gán
def notify_task_unassigned(task_id, user_id):
    socketio.emit('task_unassigned', {'task_id': task_id, 'user_id': user_id})

# Gửi thông báo trạng thái tác vụ được cập nhật
def notify_task_updated(task_id, data):
    socketio.emit('task_updated', {'task_id': task_id, 'data': data})

