def dict_from_cursor(cursor, row):
    return {col[0]: row[idx] for idx, col in enumerate(cursor.description)} 