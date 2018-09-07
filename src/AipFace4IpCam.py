# -*- coding: utf-8 -*-
import cv2
import MySQLdb
import urllib
import numpy as np
from aip import AipFace
import base64
import multiprocessing
import time,datetime
      
stream=urllib.urlopen('http://admin:admin@192.168.0.143:8081/video')
bytes=''

APP_ID = 'MyFaceApp'
API_KEY = 'YupM4GGtRLUxiiqHGwkeDGZY'
SECRET_KEY = 'IcZ8MqZkGwZTp7XY5u1ZIXpMZBniw2Nx'
imageType = 'BASE64'
groupIdList = "hz"

tStr = 'error_code'
errorCodeKey = tStr.decode('unicode-escape')
tStr = 'result'
resultKey = tStr.decode('unicode-escape')
tStr = 'user_list'
userListKey = tStr.decode('unicode-escape')
tStr = 'user_id'
userIdKey = tStr.decode('unicode-escape')
tStr = 'score'
scoreKey = tStr.decode('unicode-escape')
 
client = AipFace(APP_ID, API_KEY, SECRET_KEY)

def execClient(ijpg,imeeting_id):
    result = client.search(ijpg, imageType, groupIdList)
    if result[errorCodeKey] == 0:
        print(result[resultKey][userListKey][0])
        checkinId = result[resultKey][userListKey][0][userIdKey].encode("utf-8")
        score = result[resultKey][userListKey][0][scoreKey]

        if score > 80.0:
            idb = MySQLdb.connect("192.168.0.126", "root", "root", "app")
            icursor = idb.cursor()
            icursor.execute("select count(1) from person_checkin where meeting_id = '" + imeeting_id + "' and person_id = '" + checkinId + "'")
            iresults = icursor.fetchone()
            icount = int(iresults[0])

            if icount == 0:
                try:
                    strDate = datetime.datetime.now().strftime("%Y%m%d%H%M%S")
                    #icursor.execute("insert into person_checkin (id,meeting_id,person_id,status,checkin_time) " \
                    #+ "values ('" + strDate + "','" + imeeting_id + "','" + checkinId + "','0',now())")
                    icursor.execute("insert into person_checkin (meeting_id,person_id,status,checkin_time) " \
                    + "values ('" + imeeting_id + "','" + checkinId + "','0',now())")                    
                    idb.commit()
                except:
                    idb.rollback()
            idb.close

flag = False
index = 0
while True:
    index = index + 1 
    id = 0
    if index%100 == 0:
        db = MySQLdb.connect("192.168.0.126", "root", "root", "app")
        cursor = db.cursor()       
        cursor.execute("select meeting_id from meeting_list where status = '1' or status = '2'")      
        results = cursor.fetchall()
        if len(results) == 1:
            meeting_id = results[0][0]
            flag = True
            print('checkinStart')
        elif len(results) > 1:
            flag = False    
            print('tasklist数据异常')
        else:
            flag = False
        db.close()

    if flag == False:
        time.sleep(0.02)
        continue           

    bytes+=stream.read(1024*8)
    a = bytes.find('\xff\xd8')
    b = bytes.find('\xff\xd9')
    
    if a!=-1 and b!=-1:
        jpg = bytes[a:b+2]
        jpg4AipFace = base64.b64encode(jpg)        
        bytes= bytes[b+2:]
        ipcam = cv2.imdecode(np.fromstring(jpg, dtype=np.uint8), cv2.IMREAD_COLOR)
        #cv2.imshow('ipcam',ipcam)        
        if cv2.waitKey(1) ==27:
            exit(0)
        if index%30 == 0:
            t = multiprocessing.Process(target=execClient,args=(jpg4AipFace,meeting_id))
            t.daemon=True#将daemon设置为True，则主线程不比等待子进程，主线程结束则所有结束
            t.start()
            #cv2.imwrite('temp/imageAI/image.jpg',ipcam) #存储为图像
