#!/bin/bash

ICR_BASE=icr.io
if [ "$DEPLOYMENT_ENV" = "stage" ] || [ "$DEPLOYMENT_ENV" = "STAGE" ]; then
	ICR_BASE=stg.icr.io
fi


CONFIG_FILE=deploy/ce/ce.config.properties
C_APP_NAME=`cat ${CONFIG_FILE} | grep PREFIX | cut -d'=' -f2`-app
C_BROKER_USERNAME=`cat ${CONFIG_FILE} | grep BROKER_USERNAME | cut -d'=' -f2`
C_BROKER_PASSWORD=`cat ${CONFIG_FILE} | grep BROKER_PASSWORD | cut -d'=' -f2`
C_BROKER_ICR_NAMESPACE_URL=$ICR_BASE/`cat ${CONFIG_FILE} | grep PREFIX | cut -d'=' -f2`-namespace
C_CE_REGION=`cat ${CONFIG_FILE} | grep CE_REGION | cut -d'=' -f2`
C_CE_RESOURCE_GROUP=`cat ${CONFIG_FILE} | grep CE_RESOURCE_GROUP | cut -d'=' -f2`
C_CE_PROJECT=`cat ${CONFIG_FILE} | grep PREFIX | cut -d'=' -f2`-project
C_CE_REGISTRY_SECRET_NAME=`cat ${CONFIG_FILE} | grep PREFIX | cut -d'=' -f2`-registry-secret
C_ICR_IMAGE=`cat ${CONFIG_FILE} | grep PREFIX | cut -d'=' -f2`-img
C_ONBOARDING_ENV=`cat ${CONFIG_FILE} | grep ONBOARDING_ENV | cut -d'=' -f2`
C_DEPLOYMENT_ENV=`cat ${CONFIG_FILE} | grep DEPLOYMENT_ENV | cut -d'=' -f2`
C_PC_URL=`cat ${CONFIG_FILE} | grep PC_URL | cut -d'=' -f2`
C_PREFIX=`cat ${CONFIG_FILE} | grep PREFIX | cut -d'=' -f2`

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

echo "APP_NAME=$(getGenVar APP_NAME)
BROKER_USERNAME=$(getVar BROKER_USERNAME)
BROKER_PASSWORD=$(getVar BROKER_PASSWORD)
BROKER_ICR_NAMESPACE_URL=$(getGenVar BROKER_ICR_NAMESPACE_URL)
ICR_IMAGE=$(getGenVar ICR_IMAGE)
CE_PROJECT=$(getGenVar CE_PROJECT)
CE_REGION=$(getVar CE_REGION)
CE_RESOURCE_GROUP=$(getVar CE_RESOURCE_GROUP)
CE_REGISTRY_SECRET_NAME=$(getGenVar CE_REGISTRY_SECRET_NAME)
ONBOARDING_ENV=$(getVar ONBOARDING_ENV)
DEPLOYMENT_ENV=$(getVar DEPLOYMENT_ENV)
PC_URL=$(getVar PC_URL)
PREFIX=$(getVar PREFIX)" > deploy/ce/ce.config.temp.properties
