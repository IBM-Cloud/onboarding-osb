#!/bin/bash

echo ""
echo "---------- Logging in ibmcloud ----------"

BUILD_NUMBER=`./deploy/generate_build_number.sh`
EMPTY='""'

./deploy/ce/handle_ce_project.sh

ibmcloud config --check-version=false
ibmcloud login --apikey $DEPLOYMENT_IAM_API_KEY -r $CE_REGION -g $CE_RESOURCE_GROUP
ibmcloud target -r $CE_REGION -g $CE_RESOURCE_GROUP
ibmcloud ce project select -n $CE_PROJECT

echo ""
echo "---------- Checking in ce registry secret ----------"

GET_CE_REG=`ibmcloud ce registry get -n $CE_REGISTRY_SECRET_NAME`

if [[ $GET_CE_REG == *"OK"* ]]; then
	echo "updating $CE_REGISTRY_SECRET_NAME...";
	ibmcloud ce registry update -n $CE_REGISTRY_SECRET_NAME -p $DEPLOYMENT_IAM_API_KEY -u iamapikey
else
	echo "creating $CE_REGISTRY_SECRET_NAME...";
	ibmcloud ce registry create -n $CE_REGISTRY_SECRET_NAME -p $DEPLOYMENT_IAM_API_KEY -u iamapikey
fi

echo ""
echo "---------- Deploying to Code Engine ----------"
RESULT=""

echo "
Trying to find application on Code Engine"
APP_EXISTS="`ibmcloud ce application get -n $APP_NAME`"

if [[ $APP_EXISTS == *"OK"* ]]; then
	echo "Found
Updating Application
This might take some time...";
	if [ -z $METERING_API_KEY ] || [ $METERING_API_KEY == $EMPTY ];
	then
		RESULT=`ibmcloud ce application update --name $APP_NAME --image $BROKER_ICR_NAMESPACE_URL/$ICR_IMAGE:latest --min 1 --env BROKER_USERNAME=$BROKER_USERNAME --env BROKER_PASSWORD=$BROKER_PASSWORD --env BUILD_NUMBER=$BUILD_NUMBER --rs $CE_REGISTRY_SECRET_NAME`
	else
		RESULT=`ibmcloud ce application update --name $APP_NAME --image $BROKER_ICR_NAMESPACE_URL/$ICR_IMAGE:latest --min 1 --env BROKER_USERNAME=$BROKER_USERNAME --env BROKER_PASSWORD=$BROKER_PASSWORD --env BUILD_NUMBER=$BUILD_NUMBER --env METERING_API_KEY=$METERING_API_KEY --rs $CE_REGISTRY_SECRET_NAME`
	fi
else
	echo "Do not terminate...
Creating new application...
This might take some time... ";
	if [ -z $METERING_API_KEY ] || [ $METERING_API_KEY == $EMPTY ];
	then
		RESULT=`ibmcloud ce application create --name $APP_NAME --image $BROKER_ICR_NAMESPACE_URL/$ICR_IMAGE:latest --min 1 --env BROKER_USERNAME=$BROKER_USERNAME --env BROKER_PASSWORD=$BROKER_PASSWORD --env BUILD_NUMBER=$BUILD_NUMBER --rs $CE_REGISTRY_SECRET_NAME`
	else
		RESULT=`ibmcloud ce application create --name $APP_NAME --image $BROKER_ICR_NAMESPACE_URL/$ICR_IMAGE:latest --min 1 --env BROKER_USERNAME=$BROKER_USERNAME --env BROKER_PASSWORD=$BROKER_PASSWORD --env BUILD_NUMBER=$BUILD_NUMBER --env METERING_API_KEY=$METERING_API_KEY --rs $CE_REGISTRY_SECRET_NAME`
	fi
fi

if [[ $RESULT == *"OK"* ]]; then
	APP_URL=`ibmcloud ce application get -n $APP_NAME -o url`
	if [ -z $METERING_API_KEY ] || [ $METERING_API_KEY == $EMPTY ];
	then
		UPDATE_RESULT=`ibmcloud ce application update --name $APP_NAME --image $BROKER_ICR_NAMESPACE_URL/$ICR_IMAGE:latest --min 1 --env BROKER_USERNAME=$BROKER_USERNAME --env BROKER_PASSWORD=$BROKER_PASSWORD --env BUILD_NUMBER=$BUILD_NUMBER --env BROKER_URL=$APP_URL --env DASHBOARD_URL=$APP_URL --env METERING_API_KEY=$METERING_API_KEY --rs $CE_REGISTRY_SECRET_NAME`
	else
		UPDATE_RESULT=`ibmcloud ce application update --name $APP_NAME --image $BROKER_ICR_NAMESPACE_URL/$ICR_IMAGE:latest --min 1 --env BROKER_USERNAME=$BROKER_USERNAME --env BROKER_PASSWORD=$BROKER_PASSWORD --env BUILD_NUMBER=$BUILD_NUMBER --env BROKER_URL=$APP_URL --env DASHBOARD_URL=$APP_URL --rs $CE_REGISTRY_SECRET_NAME`
	fi

	if [[ $UPDATE_RESULT == *"OK"* ]]; then
		echo ""
		echo "*******************************************************************************"
		echo "Congratulations your broker is deployed!                          "
		echo "                                                                   "
		echo "Service is deployed on:                      "
		echo "$APP_URL                                                             "
		echo "                                                                   "
		echo "Use the broker url to the register in Partner Center.                       "
		echo "*******************************************************************************"
		echo ""
	else
		echo "$UPDATE_RESULT"
		echo ""
		echo "*******************************************************************************"
		echo "|                         "
		echo "|                                                                   "
		echo "|	Something went wrong. check the logs above.                       "
		echo "|                                                                   "
		echo "|	                       "
		echo "*******************************************************************************"
		echo ""
	fi
else
	echo "$RESULT"
	echo ""
	echo "*******************************************************************************"
	echo "|                         "
	echo "|                                                                   "
	echo "|	Something went wrong. check the logs above.                       "
	echo "|                                                                   "
	echo "|	                       "
	echo "*******************************************************************************"
	echo ""
fi
