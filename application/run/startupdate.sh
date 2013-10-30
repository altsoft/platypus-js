#!/bin/sh
JRE_PATH=java
PLATYPUS_HOME=$(cd `dirname $0` && pwd)/../
UPDATER_PATH=$PLATYPUS_HOME/lib/own/Updater.jar
URL_CONFIG=http://research.office.altsoft.biz/platypus/client/updates/NightlyBuild/update.xml
URL_UPDATE=http://research.office.altsoft.biz/platypus/client/updates/NightlyBuild/application.zip
CONFIG_NAME=$PLATYPUS_HOME/run/update.xml
TMP_UPDATE_NAME=$PLATYPUS_HOME/app.zip
RUN_COMMAND=$PLATYPUS_HOME/run/Platypus.sh
EXT_CLASSES=$PLATYPUS_HOME/ext/*
MAIN_CLASS=com.eas.client.updater.Updater
LAF_CLASS=de.muntjak.tinylookandfeel.TinyLookAndFeel
ONLY_UPDATE="update"
$JRE_PATH -cp $UPDATER_PATH:$EXT_CLASSES $MAIN_CLASS newversion -laf $LAF_CLASS -curl $URL_CONFIG -uurl $URL_UPDATE -cname $CONFIG_NAME -uname $TMP_UPDATE_NAME -path $PLATYPUS_HOME -wrun $RUN_COMMAND
if [ $? -eq 10 ]; then 
 $JRE_PATH -cp $UPDATER_PATH:$EXT_CLASSES $MAIN_CLASS update -laf $LAF_CLASS -curl $URL_CONFIG -uurl $URL_UPDATE -cname $CONFIG_NAME -uname $TMP_UPDATE_NAME -path $PLATYPUS_HOME -wrun $RUN_COMMAND 
 if [ -f $PLATYPUS_HOME/lib/own/Updater-new.jar ]; then
  rm $PLATYPUS_HOME/lib/own/Updater.jar 
  mv -f $PLATYPUS_HOME/lib/own/Updater-new.jar $PLATYPUS_HOME/lib/own/Updater.jar;
 fi
fi	
if [ $# -ne 0 ]; then
 if [ $1 != $ONLY_UPDATE ]; then $PLATYPUS_HOME/run/Platypus.sh; fi
else
 $PLATYPUS_HOME/run/Platypus.sh;
fi
