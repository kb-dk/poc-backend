#!/usr/bin/env bash

cd /tmp/src

cp -rp -- /tmp/src/target/poc-backend-*.war "$TOMCAT_APPS/poc-backend.war"
cp -- /tmp/src/conf/ocp/poc-backend.xml "$TOMCAT_APPS/poc-backend.xml"

export WAR_FILE=$(readlink -f "$TOMCAT_APPS/poc-backend.war")
