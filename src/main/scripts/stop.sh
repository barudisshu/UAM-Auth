#!/bin/bash

BASEDIR=$(cd $(dirname "$0") && pwd)
. ${BASEDIR}/common.sh
workdir=$(echo "$BASEDIR" | sed -e "s/\/bin//g")

PID_FILE=${workdir}/pid

pid=$( check_pid ${PID_FILE} )

if [[ ! -z "$pid" && "$pid" != " " ]]; then
    echo ${pid}
    kill ${pid}
    if [[ $? -eq 0 ]]; then
        rm -f ${PID_FILE}
        echo "stop uam auth successful!"
    else
        echo "stop uam auth fail!"
    fi
else
    echo "uam auth is not running."
fi

