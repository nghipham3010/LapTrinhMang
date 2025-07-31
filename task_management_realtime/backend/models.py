# file models.py này chứa các class để tạo các đối tượng cho các bảng trong database

class Task:
    def __init__(self, task_id, title, description, status, deadline, assigned_to):
        self.task_id = task_id
        self.title = title
        self.description = description
        self.status = status
        self.deadline = deadline
        self.assigned_to = assigned_to

class TaskHistory:
    def __init__(self, task_history_id, task_id, changed_by, old_status, new_status, changed_at):
        self.task_history_id = task_history_id
        self.task_id = task_id
        self.changed_by = changed_by
        self.old_status = old_status
        self.new_status = new_status
        self.changed_at = changed_at

class User:
    def __init__(self, user_id, username, role):
        self.user_id = user_id
        self.username = username
        self.role = role 

class Profile:
    def __init__(self, profile_id, user_id, full_name, email, dob, phone, gender):
        self.profile_id = profile_id
        self.user_id = user_id
        self.full_name = full_name
        self.email = email
        self.dob = dob
        self.phone = phone
        self.gender = gender

class Notification:
    def __init__(self, notification_id, user_id, content, is_read, created_at):
        self.notification_id = notification_id
        self.user_id = user_id
        self.content = content
        self.is_read = is_read
        self.created_at = created_at

class Report:
    def __init__(self, report_id, user_id, content, created_at):
        self.report_id = report_id
        self.user_id = user_id
        self.content = content
        self.created_at = created_at

