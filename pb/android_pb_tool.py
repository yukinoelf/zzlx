#!/usr/bin/python
# -*- coding: utf-8 -*-

import os

localPath=r"..\..\android\app\src\main\java\com"

def copyFile():
    os.system(r"mkdir compile")

    #预处理文件名
    os.system(r"copy /Y .\IM.BaseDefine.proto .\compile\IM.BaseDefine.proto")
    os.system(r"copy /Y .\IM.BaseDefine.proto .\compile\IMBaseDefine.proto")
    os.system(r"copy /Y .\IM.Buddy.proto .\compile\IMBuddy.proto")
    os.system(r"copy /Y .\IM.Cost.proto .\compile\IMCost.proto")
    os.system(r"copy /Y .\IM.File.proto .\compile\IMFile.proto")
    os.system(r"copy /Y .\IM.Group.proto .\compile\IMGroup.proto")
    os.system(r"copy /Y .\IM.Login.proto .\compile\IMLogin.proto")
    os.system(r"copy /Y .\IM.Message.proto .\compile\IMMessage.proto")
    os.system(r"copy /Y .\IM.Other.proto .\compile\IMOther.proto")
    os.system(r"copy /Y .\IM.SwitchService.proto .\compile\IMSwitchService.proto")
    
    os.system(r"copy /Y .\protoc.exe .\compile\protoc.exe")

    #编译pb文件
    #os.system(r"cd compile")
    os.system(r"cd compile & protoc.exe --java_out=./ IMBaseDefine.proto")
    os.system(r"cd compile & protoc.exe --java_out=./ IMBuddy.proto")
    os.system(r"cd compile & protoc.exe --java_out=./ IMCost.proto")
    os.system(r"cd compile & protoc.exe --java_out=./ IMFile.proto")
    os.system(r"cd compile & protoc.exe --java_out=./ IMGroup.proto")
    os.system(r"cd compile & protoc.exe --java_out=./ IMLogin.proto")
    os.system(r"cd compile & protoc.exe --java_out=./ IMMessage.proto")
    os.system(r"cd compile & protoc.exe --java_out=./ IMOther.proto")
    os.system(r"cd compile & protoc.exe --java_out=./ IMSwitchService.proto")

    #复制生成的文件到指定目录
    os.system(r"cd compile & xcopy /Y/E/I .\com "+localPath)


def clearCompile():
    os.system(r"rd /s/q compile")

if __name__ == '__main__':
    copyFile()
    clearCompile();
    print("pb ok")
