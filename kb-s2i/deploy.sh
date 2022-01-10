#!/usr/bin/env bash

cp -- /tmp/src/conf/ocp/logback.xml "$CONF_DIR/logback.xml"
# There are normally two configurations: core and environment
cp -- /tmp/src/conf/poc-backend-*.yaml "$CONF_DIR/"
 
ln -s -- "$TOMCAT_APPS/poc-backend.xml" "$DEPLOYMENT_DESC_DIR/poc-backend.xml"
