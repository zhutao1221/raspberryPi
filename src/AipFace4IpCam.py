# -*- coding: utf-8 -*-
import cv2
import urllib,time
import numpy as np
from aip import AipFace
import base64


# reTemp = str(base64.b64encode(bs)
      
stream=urllib.urlopen('http://admin:admin@192.168.0.143:8081/video')
bytes=''

APP_ID = 'MyFaceApp'
API_KEY = 'YupM4GGtRLUxiiqHGwkeDGZY'
SECRET_KEY = 'IcZ8MqZkGwZTp7XY5u1ZIXpMZBniw2Nx'
imageType = 'BASE64'
groupIdList = "hz"
  
client = AipFace(APP_ID, API_KEY, SECRET_KEY)
index = 0
while True:
    index = index + 1
    bytes+=stream.read(1024*8)
    a = bytes.find('\xff\xd8')
    b = bytes.find('\xff\xd9')
     
    if a!=-1 and b!=-1:
        jpg = bytes[a:b+2]
        jpg2 = base64.b64encode(jpg)        
        bytes= bytes[b+2:]
        ipcam = cv2.imdecode(np.fromstring(jpg, dtype=np.uint8), cv2.IMREAD_COLOR)
        cv2.imshow('ipcam',ipcam)        
        if cv2.waitKey(1) ==27:
            exit(0)
        if index%25 == 0:    
            result = client.search(jpg2, imageType, groupIdList)
            print(result)

