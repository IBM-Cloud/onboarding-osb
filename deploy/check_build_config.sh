#!/bin/bash

CONFIG_FILE=deploy/build.config.properties
DEPLOYMENT_IAM_API_KEY=`cat ${CONFIG_FILE} | grep DEPLOYMENT_IAM_API_KEY | cut -d'=' -f2`
BROKER_ICR_NAMESPACE_URL=`cat ${CONFIG_FILE} | grep BROKER_ICR_NAMESPACE_URL | cut -d'=' -f2`
GC_OBJECT_ID=`cat ${CONFIG_FILE} | grep GC_OBJECT_ID | cut -d'=' -f2`
ONBOARDING_IAM_API_KEY=`cat ${CONFIG_FILE} | grep ONBOARDING_IAM_API_KEY | cut -d'=' -f2`
ICR_IMAGE=`cat ${CONFIG_FILE} | grep ICR_IMAGE | cut -d'=' -f2`

EMPTY='""'
echo ""
echo "---------- Checking configuration ----------"
