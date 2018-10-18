# -*- coding: utf-8 -*-
import cv2,pika,datetime
import numpy as np
from keras.models import load_model
from keras.preprocessing.image import ImageDataGenerator
import MySQLdb

connection = pika.BlockingConnection(pika.ConnectionParameters(host='localhost'))
channel = connection.channel()
channel.queue_declare(queue='sendImage')

picDir = 'temp'
batch_size = 1
model = load_model('myModel2nd.h5')
data_gen = ImageDataGenerator(rescale=1. / 255)

index = 0
def callback(ch, method, properties, body):
    jpg = cv2.imdecode(np.fromstring(body, dtype=np.uint8), cv2.IMREAD_COLOR)
    cv2.imwrite(picDir+'/0/image.jpg',jpg) #存储为图像
    testGenerator = data_gen.flow_from_directory(picDir,
                                                 color_mode='grayscale',
                                                 target_size=(240, 320),
                                                 batch_size=batch_size,
                                                 class_mode=None,
                                                 shuffle=False)    
    
    predicted = model.predict_generator(generator=testGenerator)
    print(predicted[0])
    now = datetime.datetime.now()
    db = MySQLdb.connect("192.168.0.126", "root", "root", "app",charset='utf8')
    cursor = db.cursor()
    if predicted[0][1] > 0.7:
        #now = datetime.datetime.now()
        print('it`s not ok!')
        cv2.imwrite('/media/pi/U/picTemp/1/' + now.strftime('%y%m%d_%H%M%S') + '.jpg',jpg)
        cursor.execute("update notice set alert = 1")
        db.commit()
    elif predicted[0][0] > 0.49:
        print('it`s ok.')
        cv2.imwrite('/media/pi/U/picTemp/0/' + now.strftime('%y%m%d_%H%M%S') + '.jpg',jpg)
        cursor.execute("update notice set alert = 0")
        db.commit()
    elif predicted[0][2] > 0.7:
        print('it`s fire')
        cv2.imwrite('/media/pi/U/picTemp/2/' + now.strftime('%y%m%d_%H%M%S') + '.jpg',jpg)
        cursor.execute("update notice set alert = 2")
        db.commit()
    else:
        print('I don`t know')
        
    db.close    

channel.basic_consume(callback,
                      queue='sendImage',
                      no_ack=True)

print(' [*] Waiting for messages. To exit press CTRL+C')
channel.start_consuming()