class Task:
    def __init__(self, id, title, description, status, deadline, assigned_to):
        self.id = id
        self.title = title
        self.description = description
        self.status = status
        self.deadline = deadline
        self.assigned_to = assigned_to

class TaskHistory:
    def __init__(self, id, task_id, changed_by, old_status, new_status, changed_at):
        self.id = id
        self.task_id = task_id
        self.changed_by = changed_by
        self.old_status = old_status
        self.new_status = new_status
        self.changed_at = changed_at

class User:
    def __init__(self, id, username, role):
        self.id = id
        self.username = username
        self.role = role 