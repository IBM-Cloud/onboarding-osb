#!/bin/bash

source deploy/colorcodes.sh

CONFIG_FILE=deploy/build.config.temp.properties
GC_OBJECT_ID=`cat ${CONFIG_FILE} | grep GC_OBJECT_ID | cut -d'=' -f2`
ONBOARDING_ENV=`cat ${CONFIG_FILE} | grep ONBOARDING_ENV | cut -d'=' -f2`
DEPLOYMENT_ENV=`cat ${CONFIG_FILE} | grep DEPLOYMENT_ENV | cut -d'=' -f2`
PREFIX=`cat ${CONFIG_FILE} | grep PREFIX | cut -d'=' -f2`

EMPTY='""'
echo ""
echo "---------- Checking configuration ----------"
if [ -z $PREFIX ] || [ $PREFIX == $EMPTY ] || [ -z $ONBOARDING_ENV ] || [ $ONBOARDING_ENV == $EMPTY ] || [ -z $DEPLOYMENT_ENV ] || [ $DEPLOYMENT_ENV == $EMPTY ] || [ -z $GC_OBJECT_ID ] || [ $GC_OBJECT_ID == $EMPTY ];
then
	echo -e "${Red}"
	echo "*******************************************************************************"
	echo "build config properties not set !" ;
	echo "refer README to set values" ;
	echo "Exiting..." ;
	echo "*******************************************************************************"
	echo -e "${RCol}"
	exit 1;
else
	echo -e "${Gre}Ok${RCol}"
fi

echo ""
echo "---------- Checking secrets ----------"
if [ -z $DEPLOYMENT_IAM_API_KEY ] || [ $DEPLOYMENT_IAM_API_KEY == $EMPTY ] || [ -z $ONBOARDING_IAM_API_KEY ] || [ $ONBOARDING_IAM_API_KEY == $EMPTY ];
then
	echo -e "${Red}"
	echo "*******************************************************************************"
	echo "secrets not set !" ;
	echo "make sure these values are provided!" ;
	echo "DEPLOYMENT_IAM_API_KEY, ONBOARDING_IAM_API_KEY" ;
	echo "refer README to set values" ;
	echo "Exiting..." ;
	echo "*******************************************************************************"
	echo -e "${RCol}"
	exit 1;
else
	echo -e "${Gre}Ok${RCol}"
fi
