#!/bin/bash

#以下是上傳到ftp
HOST=iwow.systex.com.tw
USER=appstore
PASS=systex6214.

APPNAME=ChinaAir

sudo cp ./app/build/outputs/apk/prodMobile30/release/* ./app/build/outputs/apk/
sudo cp ./app/build/outputs/apk/prodPremobile30/release/* ./app/build/outputs/apk/
sudo cp ./app/build/outputs/apk/uatMobile30/debug/* ./app/build/outputs/apk/
#sudo cp ./app/build/outputs/apk/prodMobile30/unsignRelease/* ./app/build/outputs/apk/

cd ./app/build/outputs/apk/

for entry in *.apk
do
  filename="$entry"
  break
done

tmp=${filename#*[}
num=${tmp%]*}

upload_name=${APPNAME}-${num}

echo "Output file name = $filename"
echo "Version = $num"
echo "Upload file name = $upload_name"

mkdir ~/Desktop/Release_Apk/
rm -rf ~/Desktop/Release_Apk/${APPNAME}
mkdir ~/Desktop/Release_Apk/${APPNAME}
mkdir ~/Desktop/Release_Apk/${APPNAME}/${upload_name}/
sudo cp * ~/Desktop/Release_Apk/${APPNAME}/${upload_name}/
cd ~/Desktop/Release_Apk/${APPNAME}/${upload_name}/

ftp -n -i <<!
open ${HOST}
user ${USER} ${PASS}
passive
bin
cd Louis
mkdir ${APPNAME}
cd ${APPNAME}
mkdir ${upload_name}
cd ${upload_name}
mput *
bye

EOF