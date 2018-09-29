# -*- coding: utf-8 -*-
from urllib import request
#stream=request.urlopen('http://admin:admin@192.168.0.143:8081/video')
#stream=request.urlopen('http://www.baidu.com')
#while True:
#    bytes+=stream.read(1024*8)
#    a = bytes.find('\xff\xd8')
#    b = bytes.find('\xff\xd9')
    
#    if a!=-1 and b!=-1:
#        jpg = bytes[a:b+2]
#        bytes= bytes[b+2:]
response = request.urlopen('http://admin:admin@192.168.0.143:8081/video')
page = response.read()
page = page.decode('utf-8')
print(page)