#!/bin/bash

CONFIG_FILE=deploy/ce/ce.config.properties
APP_NAME=`cat ${CONFIG_FILE} | grep APP_NAME | cut -d'=' -f2`
BROKER_USERNAME=`cat ${CONFIG_FILE} | grep BROKER_USERNAME | cut -d'=' -f2`
BROKER_PASSWORD=`cat ${CONFIG_FILE} | grep BROKER_PASSWORD | cut -d'=' -f2`
BROKER_ICR_NAMESPACE_URL=`cat ${CONFIG_FILE} | grep BROKER_ICR_NAMESPACE_URL | cut -d'=' -f2`
CE_REGION=`cat ${CONFIG_FILE} | grep CE_REGION | cut -d'=' -f2`
CE_RESOURCE_GROUP=`cat ${CONFIG_FILE} | grep CE_RESOURCE_GROUP | cut -d'=' -f2`
CE_PROJECT=`cat ${CONFIG_FILE} | grep CE_PROJECT | cut -d'=' -f2`
CE_REGISTRY_SECRET_NAME=`cat ${CONFIG_FILE} | grep CE_REGISTRY_SECRET_NAME | cut -d'=' -f2`
ICR_IMAGE=`cat ${CONFIG_FILE} | grep ICR_IMAGE | cut -d'=' -f2`

EMPTY='""'

echo ""
echo "---------- Checking configuration ----------"

if [ -z $ICR_IMAGE ] || [ $ICR_IMAGE == $EMPTY ] || [ -z $CE_REGISTRY_SECRET_NAME ] || [ $CE_REGISTRY_SECRET_NAME == $EMPTY ] || [ -z $BROKER_USERNAME ] || [ $BROKER_USERNAME == $EMPTY ] || [ -z $BROKER_PASSWORD ] || [ $BROKER_PASSWORD == $EMPTY ] || [ -z $BROKER_ICR_NAMESPACE_URL ] || [ $BROKER_ICR_NAMESPACE_URL == $EMPTY ] || [ -z $CE_REGION ] || [ $CE_REGION == $EMPTY ] || [ -z $APP_NAME ]|| [ $APP_NAME == $EMPTY ] || [ -z $CE_RESOURCE_GROUP ] || [ $CE_RESOURCE_GROUP == $EMPTY ] || [ -z $CE_PROJECT ] || [ $CE_PROJECT == $EMPTY ];
then 
	echo ""
	echo "*******************************************************************************"
	echo "deploy config properties not set !" ;
	echo "refer README to set values" ;
	echo "Exiting..." ;
	echo "*******************************************************************************"
	echo ""
	exit 1;
else
	echo "Ok"
fi

echo ""
echo "---------- Checking secrets ----------"
if [ -z $DEPLOYMENT_IAM_API_KEY ] || [ $DEPLOYMENT_IAM_API_KEY == $EMPTY ];
then
	echo ""
	echo "*******************************************************************************"
	echo "secrets not set !" ;
	echo "make sure DEPLOYMENT_IAM_API_KEY is provided!" ;
	echo "refer README to set values" ;
	echo "Exiting..." ;
	echo "*******************************************************************************"
	echo ""
	exit 1;
else
	echo "Ok"
fi
