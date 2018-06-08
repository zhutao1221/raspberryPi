from aip import AipFace
import base64
import json
from urllib import parse

def get_file_content(filePath):  
    with open(filePath, 'rb') as fp:  
        return str(base64.b64encode(fp.read()), encoding='utf-8')  

APP_ID = 'MyFaceApp'
API_KEY = 'YupM4GGtRLUxiiqHGwkeDGZY'
SECRET_KEY = 'IcZ8MqZkGwZTp7XY5u1ZIXpMZBniw2Nx'
imageType = 'BASE64'

client = AipFace(APP_ID, API_KEY, SECRET_KEY)


# 读取图片  
filePath = r'/home/zhutao/dev/gitCode/raspberryPi/pic/zhutao.jpg'
  
# # 定义参数变量 
# """ 如果有可选参数 """
# options = {}
# options["face_field"] = "age"
# options["max_face_num"] = 2
# options["face_type"] = "LIVE" 
# 
# result = client.detect(get_file_content(filePath),imageType,options)
# print(result)

groupIdList = "hz"
""" 带参数调用人脸搜索 """
result = client.search(get_file_content(filePath), imageType, groupIdList)
print(result)