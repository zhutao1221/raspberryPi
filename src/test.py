# encoding:utf-8
from urllib import request
'''
人脸注册
'''

request_url = "https://aip.baidubce.com/rest/2.0/face/v3/faceset/user/add"


params = "{\"image\":\"027d8308a2ec665acb1bdf63e513bcb9\",\"image_type\":\"FACE_TOKEN\",\"group_id\":\"group_repeat\",\"user_id\":\"user1\",\"user_info\":\"abc\",\"quality_control\":\"LOW\",\"liveness_control\":\"NORMAL\"}"

access_token = '[调用鉴权接口获取的token]'
request_url = request_url + "?access_token=" + access_token
myRequest = request.Request(url=request_url, data=params)
myRequest.add_header('Content-Type', 'application/json')
response = request.urlopen(myRequest)
content = response.read()
if content:
    print(content)