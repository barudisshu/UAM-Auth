#!/bin/bash

java -version
BASEDIR=$(cd $(dirname "$0") && pwd)
. ${BASEDIR}/common.sh

workdir=$(echo "$BASEDIR" | sed -e "s/\/bin//g")
LOG_PATH=$(echo "${workdir}/logs")
echo "LOG_PATH:${LOG_PATH}"
PID_FILE=${workdir}/pid
NOHUP_FILE=${workdir}/nohup.out

JAVA_OPTS="\
-DLOG_PATH=${LOG_PATH} \
-server \
-Xms512m \
-Xmx512m \
-XX:PermSize=256m \
-XX:MaxPermSize=256m \
-XX:-UseGCOverheadLimit"


pid=$( check_pid $PID_FILE )

if [[ ! -z "$pid" && "$pid" != " " ]];then
    echo "uam auth is already running."
else
    nohup java ${JAVA_OPTS} -jar ${workdir}/lib/uam-auth-1.0.0-dev-SNAPSHOT.jar --spring.config.location=${workdir}/conf/ --logging.config=${workdir}/conf/log4j2.xml --logging.path=${LOG_PATH} > ${NOHUP_FILE} < /dev/null 2>&1 &
    pid=$!
    sleep 1
    echo "start uam auth successfully!"
fi
echo $pid
echo ${pid} > ${PID_FILE}
