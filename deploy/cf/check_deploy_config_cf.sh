#!/bin/bash

CONFIG_FILE=deploy/cf/cf.config.properties
APP_NAME=`cat ${CONFIG_FILE} | grep APP_NAME | cut -d'=' -f2`
CF_API=`cat ${CONFIG_FILE} | grep CF_API | cut -d'=' -f2`
CF_ORGANIZATION=`cat ${CONFIG_FILE} | grep CF_ORGANIZATION | cut -d'=' -f2`
CF_SPACE=`cat ${CONFIG_FILE} | grep CF_SPACE | cut -d'=' -f2`
BROKER_USERNAME=`cat ${CONFIG_FILE} | grep BROKER_USERNAME | cut -d'=' -f2`
BROKER_PASSWORD=`cat ${CONFIG_FILE} | grep BROKER_PASSWORD | cut -d'=' -f2`
DEPLOYMENT_IAM_API_KEY=`cat ${CONFIG_FILE} | grep DEPLOYMENT_IAM_API_KEY | cut -d'=' -f2`
BROKER_ICR_NAMESPACE_URL=`cat ${CONFIG_FILE} | grep BROKER_ICR_NAMESPACE_URL | cut -d'=' -f2`
ICR_IMAGE=`cat ${CONFIG_FILE} | grep ICR_IMAGE | cut -d'=' -f2`

EMPTY='""'

echo ""
echo "---------- Checking configuration ----------"

if [ -z $ICR_IMAGE ] || [ $ICR_IMAGE == $EMPTY ] || [ -z $BROKER_USERNAME ] || [ $BROKER_USERNAME == $EMPTY ] || [ -z $BROKER_PASSWORD ] || [ $BROKER_PASSWORD == $EMPTY ] || [ -z $BROKER_ICR_NAMESPACE_URL ] || [ $BROKER_ICR_NAMESPACE_URL == $EMPTY ] || [ -z $DEPLOYMENT_IAM_API_KEY ] || [ $DEPLOYMENT_IAM_API_KEY == $EMPTY ] || [ -z $CF_API ] || [ $CF_API == $EMPTY ] || [ -z $APP_NAME ]|| [ $APP_NAME == $EMPTY ] || [ -z $CF_ORGANIZATION ] || [ $CF_ORGANIZATION == $EMPTY ] || [ -z $CF_SPACE ] || [ $CF_SPACE == $EMPTY ];
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
