# -*- coding: utf-8 -*-
import cv2,pika
import urllib,base64,time,datetime
import numpy as np

connection = pika.BlockingConnection(pika.ConnectionParameters(host='localhost'))
channel = connection.channel()

stream=urllib.urlopen('http://admin:admin@192.168.0.143:8081/video')
bytes=''

index = 0
while True:
    bytes+=stream.read(1024*8)
    a = bytes.find('\xff\xd8')
    b = bytes.find('\xff\xd9')
    
    if a!=-1 and b!=-1:
        index += 1
        jpg = bytes[a:b+2]
        bytes= bytes[b+2:]        
       
        if cv2.waitKey(1) ==27:
            exit(0)
        if index%1000 == 0:            
            #cv2.imwrite('temp/imageAI/image.jpg',ipcam) #存储为图像
            channel.queue_declare(queue='sendImage')
            channel.basic_publish(exchange='',
                                  routing_key='sendImage',
                                  body=jpg)
            now = datetime.datetime.now()
            print(" [x] Sent " + now.strftime('%y%m%d_%H%M%S'))
            index = 1