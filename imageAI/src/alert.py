# -*- coding: utf-8 -*-
import winsound
import MySQLdb
import time

while True:
    db = MySQLdb.connect("127.0.0.1", "root", "root", "app",charset='utf8')
    cursor = db.cursor()
    cursor.execute("SELECT alert FROM notice")
    results = cursor.fetchone()

    if results[0] == 0:
        print('ok')
    elif results[0] == 1:
        print('got you!')   
        winsound.PlaySound('alert.wav',flags=1)
        time.sleep(5)
    db.close
    time.sleep(2)