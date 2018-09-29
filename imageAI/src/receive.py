# -*- coding: utf-8 -*-
import cv2,pika,datetime
import numpy as np
from keras.models import load_model
from keras.preprocessing.image import ImageDataGenerator

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
    if predicted[0][1] > 0.7:
        now = datetime.datetime.now()
        print('it`s not ok!')
        cv2.imwrite('/media/pi/2F08-76A8/picTemp/' + now.strftime('%y%m%d_%H%M%S') + '.jpg',jpg)
    else:
        print('it`s ok.')
    

channel.basic_consume(callback,
                      queue='sendImage',
                      no_ack=True)

print(' [*] Waiting for messages. To exit press CTRL+C')
channel.start_consuming()