# encoding: utf-8
'''
Created on 2015年6月11日

@author: Johnny
'''
import sys
from zipfile import ZipFile

def addComment(zipPath, comment):
    z = ZipFile(zipPath, mode="a")
    info = z.getinfo("assets/jump")
    info.comment = comment
    z._didModify = True
    z.close()

def readComment(zipPath):
    z = ZipFile(zipPath, mode="r")
    info = z.getinfo("assets/jump")
    print "comment = " + info.comment
    z.close()

if __name__ == '__main__':
    zipPath = sys.argv[1]
    comment = sys.argv[2]
    print "zipPath = " + zipPath + ", comment = " + comment
    addComment(zipPath, comment)
    readComment(zipPath)
