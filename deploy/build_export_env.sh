#!/bin/bash

ICR_BASE=icr.io
if [ "$ONBOARDING_ENV" = "stage" ] || [ "$ONBOARDING_ENV" = "STAGE" ]; then
	ICR_BASE=stg.icr.io
fi

CONFIG_FILE=deploy/build.config.properties
C_BROKER_ICR_NAMESPACE_URL=$ICR_BASE/`cat ${CONFIG_FILE} | grep PREFIX | cut -d'=' -f2`-namespace
C_GC_OBJECT_ID=`cat ${CONFIG_FILE} | grep GC_OBJECT_ID | cut -d'=' -f2`
C_ICR_IMAGE=`cat ${CONFIG_FILE} | grep PREFIX | cut -d'=' -f2`-img
C_ONBOARDING_ENV=`cat ${CONFIG_FILE} | grep ONBOARDING_ENV | cut -d'=' -f2`
C_ICR_NAMESPACE_REGION=`cat ${CONFIG_FILE} | grep ICR_NAMESPACE_REGION | cut -d'=' -f2`
C_ICR_RESOURCE_GROUP=`cat ${CONFIG_FILE} | grep ICR_RESOURCE_GROUP | cut -d'=' -f2`

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

getGenVar()
{
	cVarName="C_$1"
	echo "${!cVarName}"
}

echo "ONBOARDING_ENV=$(getVar ONBOARDING_ENV)
GC_OBJECT_ID=$(getVar GC_OBJECT_ID)
BROKER_ICR_NAMESPACE_URL=$(getGenVar BROKER_ICR_NAMESPACE_URL)
ICR_IMAGE=$(getGenVar ICR_IMAGE)
ICR_NAMESPACE_REGION=$(getVar ICR_NAMESPACE_REGION)
ICR_RESOURCE_GROUP=$(getVar ICR_RESOURCE_GROUP)" > deploy/build.config.temp.properties
