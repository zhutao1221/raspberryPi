from aip import AipFace
import base64
import time
import os,sys

APP_ID = 'MyFaceApp'
API_KEY = 'YupM4GGtRLUxiiqHGwkeDGZY'
SECRET_KEY = 'IcZ8MqZkGwZTp7XY5u1ZIXpMZBniw2Nx'
imageType = 'BASE64'
groupId = "hz"

client = AipFace(APP_ID, API_KEY, SECRET_KEY)


# def findLast(strA,strB):
#     lastPosition=-1
#     while True:
#         position=strA.find(strB,lastPosition+1)
#         if position==-1:
#             return lastPosition
#         lastPosition=position

path='/home/zhutao/dev/gitCode/raspberryPi/person'
for dirpath,dirnames,filenames in os.walk(path):
    for file in filenames:
        fullpath=os.path.join(dirpath,file)
        print(fullpath)
        #print(file[0:9])
        openImage = open(fullpath,"rb")
        image = base64.b64encode(openImage.read())
        openImage.close()

        client.addUser(image, imageType, groupId, file[0:9]);