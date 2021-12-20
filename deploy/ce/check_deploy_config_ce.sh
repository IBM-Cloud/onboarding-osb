#!/bin/bash

CONFIG_FILE=deploy/ce/ce.config.temp.properties
BROKER_USERNAME=`cat ${CONFIG_FILE} | grep BROKER_USERNAME | cut -d'=' -f2`
BROKER_PASSWORD=`cat ${CONFIG_FILE} | grep BROKER_PASSWORD | cut -d'=' -f2`
CE_REGION=`cat ${CONFIG_FILE} | grep CE_REGION | cut -d'=' -f2`
CE_RESOURCE_GROUP=`cat ${CONFIG_FILE} | grep CE_RESOURCE_GROUP | cut -d'=' -f2`

EMPTY='""'

echo ""
echo "---------- Checking configuration ----------"

if [ -z $BROKER_USERNAME ] || [ $BROKER_USERNAME == $EMPTY ] || [ -z $BROKER_PASSWORD ] || [ $BROKER_PASSWORD == $EMPTY ] || [ -z $CE_REGION ] || [ $CE_REGION == $EMPTY ] || [ -z $CE_RESOURCE_GROUP ] || [ $CE_RESOURCE_GROUP == $EMPTY ];
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

echo ""
echo "---------- Checking METERING_API_KEY ----------"
if [ -z $METERING_API_KEY ] || [ $METERING_API_KEY == $EMPTY ];
then
	echo ""
	echo "*******************************************************************************"
	echo "METERING_API_KEY is not provided!" ;
	echo "send metric option will not be available" ;
	echo "refer README to set values" ;
	echo "Exiting..." ;
	echo "*******************************************************************************"
	echo ""
else
	echo "Ok"
fi
