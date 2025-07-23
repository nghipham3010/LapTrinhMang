from flask_socketio import SocketIO, emit, join_room, leave_room

socketio = SocketIO(cors_allowed_origins="*")
 
# Gửi thông báo trạng thái tác vụ thay đổi
def notify_task_update(task_id, data):
    socketio.emit('task_update', {'task_id': task_id, 'data': data}) 