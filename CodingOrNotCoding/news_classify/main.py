# -*- coding: utf-8 -*-
"""
Created on Thu Jun  6 15:52:50 2019

@author: Administrator
"""
import os,csv
from jieba import analyse

def copyFiles(sourceFile,targetFile): 
    targetDir = os.path.dirname(targetFile)   
    if not os.path.exists(targetDir):  
        os.makedirs(targetDir)  
    if not os.path.exists(targetFile) or (os.path.exists(targetFile) and (os.path.getsize(targetFile) != os.path.getsize(sourceFile))):  
        open(targetFile, "wb").write(open(sourceFile, "rb").read())
        
def findType(tags):
    dic_csv = open('type.csv','r',encoding='utf-8')
    dic_reader = csv.DictReader(dic_csv)
    key_types = []    
    for row_dic in dic_reader:
        type_dic = row_dic['TYPE']
        keyword_dic = row_dic['KEYWORD']
        keyword_dic_list = keyword_dic.split('|')
        
        for tag in tags:
            if tag in keyword_dic_list:
                key_types.append(type_dic)
                break
    return key_types
    
    
#main
for dirpath,dirnames,filenames in os.walk('news'):
    hashlist = []
    for file in filenames:
        fullpath=os.path.join(dirpath,file)

        with open(fullpath,'r',encoding='utf-8') as f:
            reader = csv.reader(f)
            for row in reader:
                if row[4] !='': 
                    file_hash = hash(row[4])
                    if file_hash not in hashlist:
                        hashlist.append(file_hash)
                        filename = os.path.basename(fullpath)
                        targetFile = 'temp/notnull/' + filename
                        copyFiles(fullpath,targetFile)
            
for dirpath,dirnames,filenames in os.walk('temp/notnull'):
    for file in filenames:
        fullpath=os.path.join(dirpath,file)

        with open(fullpath,'r',encoding='utf-8') as f:
            reader = csv.reader(f)
            #print(fullpath)
            for row in reader:
                tags1 = analyse.extract_tags(row[4],topK=10,allowPOS=('n'))
                tags2 = analyse.textrank(row[4],topK=10,allowPOS=('n'))
                
                type1 = findType(tags1)
                type2 = findType(tags2)
                
                type_all = list(set(type1 + type2))
                if len(type_all) > 0:
                    print(fullpath)
                    filename = os.path.basename(fullpath)
                    for index in type_all: 
                        targetFile = 'temp/' + index + '/' + filename
                        copyFiles(fullpath,targetFile)       