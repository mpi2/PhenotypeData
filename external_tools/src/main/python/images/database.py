#!/usr/bin/python
import mysql.connector
from mysql.connector import errorcode

def getDbConnection(komp2Host, komp2Port, komp2db, komp2User, komp2Pass):
    print 'connnecting to host='+komp2Host+ ' port:'+komp2Port+' db:'+komp2db+' komp2User:'+komp2User
    try:
        cnx = mysql.connector.connect(host=komp2Host, # your host, usually localhost
                                port=komp2Port,
                             user=komp2User, # your username
                              passwd=komp2Pass, # your password
                              db=komp2db) # name of the data base
    except mysql.connector.Error as err:
        if err.errno == errorcode.ER_ACCESS_DENIED_ERROR:
            print("Something is wrong with your user name or password")
        elif err.errno == errorcode.ER_BAD_DB_ERROR:
            print("Database does not exists")
        else:
            print("error in connection"+ str(err))
    return cnx

 #get the images we have in theory already downloaded - these should have a full res file path allocated       
def getFullResolutionFilePaths(cnx):
    fullResPathsAlreadyHave=set()
    print "getting full res paths for images"#these have been set on xml load now so just because the file path is there doesn't mean we have downloaded the image 
    #print 'need to store the new url here with observation_id='+str(observation_id)+' download_file_path= '+downloadFilePath
    try:
        cur = cnx.cursor(buffered=True)
        #SQL query to INSERT a record into the table FACTRESTTBL.
        cur.execute("""SELECT FULL_RESOLUTION_FILE_PATH FROM image_record_observation where omero_id!=0""")
        #if cur.rowcount != 1: #note that if the uri has already been added the row count will be 0
           # print("error affected rows = {}".format(cur.rowcount)+ downloadFilePath)
        data=cur.fetchall()
        for row in data:
            
            #print "adding "+row[0]
            fullResPathsAlreadyHave.add(row[0])
            
        cur.close()
    except mysql.connector.Error as err:
        print(err)
    print "Number of fullResPathsAlreadyHave with omero_ids="+str(len(fullResPathsAlreadyHave))
    return fullResPathsAlreadyHave