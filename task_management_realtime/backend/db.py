import pyodbc

def get_connection():
    conn = pyodbc.connect(
        'DRIVER={ODBC Driver 17 for SQL Server};'
        'SERVER=LAPTOP-VHLOQ1G2\\SQLEXPRESS;'
        'DATABASE=TaskManagement;'
        'Trusted_Connection=yes;'
    )
    return conn 