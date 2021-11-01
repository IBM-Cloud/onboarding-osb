#!/bin/bash

CONFIG_FILE=deploy/ce/ce.config.properties
C_APP_NAME=`cat ${CONFIG_FILE} | grep APP_NAME | cut -d'=' -f2`
C_BROKER_USERNAME=`cat ${CONFIG_FILE} | grep BROKER_USERNAME | cut -d'=' -f2`
C_BROKER_PASSWORD=`cat ${CONFIG_FILE} | grep BROKER_PASSWORD | cut -d'=' -f2`
C_BROKER_ICR_NAMESPACE_URL=`cat ${CONFIG_FILE} | grep BROKER_ICR_NAMESPACE_URL | cut -d'=' -f2`
C_CE_REGION=`cat ${CONFIG_FILE} | grep CE_REGION | cut -d'=' -f2`
C_CE_RESOURCE_GROUP=`cat ${CONFIG_FILE} | grep CE_RESOURCE_GROUP | cut -d'=' -f2`
C_CE_PROJECT=`cat ${CONFIG_FILE} | grep CE_PROJECT | cut -d'=' -f2`
C_CE_REGISTRY_SECRET_NAME=`cat ${CONFIG_FILE} | grep CE_REGISTRY_SECRET_NAME | cut -d'=' -f2`
C_ICR_IMAGE=`cat ${CONFIG_FILE} | grep ICR_IMAGE | cut -d'=' -f2`
C_ONBOARDING_ENV=`cat ${CONFIG_FILE} | grep ONBOARDING_ENV | cut -d'=' -f2`

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

echo "APP_NAME=$(getVar APP_NAME)
BROKER_USERNAME=$(getVar BROKER_USERNAME)
BROKER_PASSWORD=$(getVar BROKER_PASSWORD)
BROKER_ICR_NAMESPACE_URL=$(getVar BROKER_ICR_NAMESPACE_URL)
ICR_IMAGE=$(getVar ICR_IMAGE)
CE_PROJECT=$(getVar CE_PROJECT)
CE_REGION=$(getVar CE_REGION)
CE_RESOURCE_GROUP=$(getVar CE_RESOURCE_GROUP)
CE_REGISTRY_SECRET_NAME=$(getVar CE_REGISTRY_SECRET_NAME)
ONBOARDING_ENV=$(getVar ONBOARDING_ENV)" > deploy/ce/ce.config.properties
