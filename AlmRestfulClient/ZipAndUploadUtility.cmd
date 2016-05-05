@echo off

java -cp .\target\classes -DalmHost=sw-hpalm -DalmPort=8080 -DalmProtocol=http com.stevenckwong.AlmRestfulClient.ZipUploadUtility 