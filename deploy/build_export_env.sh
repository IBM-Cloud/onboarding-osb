#!/bin/bash

CONFIG_FILE=deploy/build.config.properties
C_DEPLOYMENT_IAM_API_KEY=`cat ${CONFIG_FILE} | grep DEPLOYMENT_IAM_API_KEY | cut -d'=' -f2`
C_BROKER_ICR_NAMESPACE_URL=`cat ${CONFIG_FILE} | grep BROKER_ICR_NAMESPACE_URL | cut -d'=' -f2`
C_GC_OBJECT_ID=`cat ${CONFIG_FILE} | grep GC_OBJECT_ID | cut -d'=' -f2`
C_ONBOARDING_IAM_API_KEY=`cat ${CONFIG_FILE} | grep ONBOARDING_IAM_API_KEY | cut -d'=' -f2`
C_ICR_IMAGE=`cat ${CONFIG_FILE} | grep ICR_IMAGE | cut -d'=' -f2`

EMPTY='""'

getVar()
{
	cVarName="C_$1"
	if [ -z ${!1} ] || [ ${!1} == $EMPTY ]
	then
		echo "${!cVarName}"
	else
		echo "${!1}"
	fi
}

echo "
DEPLOYMENT_IAM_API_KEY=$(getVar DEPLOYMENT_IAM_API_KEY)
BROKER_ICR_NAMESPACE_URL=$(getVar BROKER_ICR_NAMESPACE_URL)
GC_OBJECT_ID=$(getVar GC_OBJECT_ID)
ONBOARDING_IAM_API_KEY=$(getVar ONBOARDING_IAM_API_KEY)
ICR_IMAGE=$(getVar ICR_IMAGE)" > deploy/build.config.properties
