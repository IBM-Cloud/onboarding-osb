#!/bin/bash

EMPTY='""'
echo ""
echo "---------- Checking configuration ----------"
if [ -z $ICR_IMAGE ] || [ $ICR_IMAGE == $EMPTY ] || [ -z $ONBOARDING_IAM_API_KEY ] || [ $ONBOARDING_IAM_API_KEY == $EMPTY ] || [ -z $GC_OBJECT_ID ] || [ $GC_OBJECT_ID == $EMPTY ] || [ -z $BROKER_ICR_NAMESPACE_URL ] || [ $BROKER_ICR_NAMESPACE_URL == $EMPTY ] || [ -z $DEPLOYMENT_IAM_API_KEY ] || [ $DEPLOYMENT_IAM_API_KEY == $EMPTY ]; 
then 
	echo ""
	echo "*******************************************************************************"
	echo "build config properties not set !" ;
	echo "refer README to set values" ;
	echo "Exiting..." ;
	echo "*******************************************************************************"
	echo ""
	exit 1;
else
	echo "Ok"
fi
