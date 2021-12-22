#!/bin/bash

echo ""
echo "---------- Logging in ibmcloud ----------"

BUILD_NUMBER=`./deploy/generate_build_number.sh`
EMPTY='""'


IBM_API_ENDPOINT="https://cloud.ibm.com"
ICR_BASE=icr.io
if [ "$ONBOARDING_ENV" = "stage" ] || [ "$ONBOARDING_ENV" = "STAGE" ]; then
	ICR_BASE=stg.icr.io
	IBM_API_ENDPOINT="https://test.cloud.ibm.com"
fi

IAM_ENDPOINT_TEST="https://iam.test.cloud.ibm.com"
IAM_ENDPOINT_PROD="https://iam.cloud.ibm.com"
USAGE_ENDPOINT_TEST="https://billing.test.cloud.ibm.com"
USAGE_ENDPOINT_PROD="https://billing.cloud.ibm.com"

if [ "$ONBOARDING_ENV" = "stage" ] || [ "$ONBOARDING_ENV" = "STAGE" ]; then
	IAM_ENDPOINT=$IAM_ENDPOINT_TEST
	USAGE_ENDPOINT=$USAGE_ENDPOINT_TEST
else
	IAM_ENDPOINT=$IAM_ENDPOINT_PROD
	USAGE_ENDPOINT=$USAGE_ENDPOINT_PROD
fi

./deploy/ce/handle_ce_project.sh

ibmcloud config --check-version=false
ibmcloud login --apikey $DEPLOYMENT_IAM_API_KEY -a $IBM_API_ENDPOINT -r $CE_REGION -g $CE_RESOURCE_GROUP
ibmcloud target -r $CE_REGION -g $CE_RESOURCE_GROUP
ibmcloud ce project select -n $CE_PROJECT

echo ""
echo "---------- Checking in ce registry secret ----------"

echo "checking ce registry. new registry will be created if failed to find registry with name $CE_REGISTRY_SECRET_NAME"
GET_CE_REG=`ibmcloud ce registry get -n $CE_REGISTRY_SECRET_NAME`

if [[ $GET_CE_REG == *"OK"* ]]; then
	echo "updating $CE_REGISTRY_SECRET_NAME...";
	ibmcloud ce registry update -s $ICR_BASE -n $CE_REGISTRY_SECRET_NAME -p $DEPLOYMENT_IAM_API_KEY -u iamapikey
else
	echo "creating $CE_REGISTRY_SECRET_NAME...";
	ibmcloud ce registry create -s $ICR_BASE -n $CE_REGISTRY_SECRET_NAME -p $DEPLOYMENT_IAM_API_KEY -u iamapikey
fi

echo ""
echo "---------- Deploying to Code Engine ----------"
RESULT=""

echo "
Trying to find application on Code Engine"
echo "new application will be created if failed to find application with name $APP_NAME"
APP_EXISTS="`ibmcloud ce application get -n $APP_NAME`"

if [[ $APP_EXISTS == *"OK"* ]]; then
	echo "Found
Updating Application
This might take some time...";
	if [ -z $METERING_API_KEY ] || [ $METERING_API_KEY == $EMPTY ];
	then
		RESULT=`ibmcloud ce application update --name $APP_NAME --image $BROKER_ICR_NAMESPACE_URL/$ICR_IMAGE:latest --min 1 --env BROKER_USERNAME=$BROKER_USERNAME --env BROKER_PASSWORD=$BROKER_PASSWORD --env BUILD_NUMBER=$BUILD_NUMBER --env IAM_ENDPOINT=$IAM_ENDPOINT --env USAGE_ENDPOINT=$USAGE_ENDPOINT --env PC_URL=$PC_URL --rs $CE_REGISTRY_SECRET_NAME`
	else
		RESULT=`ibmcloud ce application update --name $APP_NAME --image $BROKER_ICR_NAMESPACE_URL/$ICR_IMAGE:latest --min 1 --env BROKER_USERNAME=$BROKER_USERNAME --env BROKER_PASSWORD=$BROKER_PASSWORD --env BUILD_NUMBER=$BUILD_NUMBER --env IAM_ENDPOINT=$IAM_ENDPOINT --env USAGE_ENDPOINT=$USAGE_ENDPOINT --env PC_URL=$PC_URL --env METERING_API_KEY=$METERING_API_KEY --rs $CE_REGISTRY_SECRET_NAME`
	fi
else
	echo "Do not terminate...
Creating new application...
This might take some time... ";
	if [ -z $METERING_API_KEY ] || [ $METERING_API_KEY == $EMPTY ];
	then
		RESULT=`ibmcloud ce application create --name $APP_NAME --image $BROKER_ICR_NAMESPACE_URL/$ICR_IMAGE:latest --min 1 --env BROKER_USERNAME=$BROKER_USERNAME --env BROKER_PASSWORD=$BROKER_PASSWORD --env BUILD_NUMBER=$BUILD_NUMBER --env IAM_ENDPOINT=$IAM_ENDPOINT --env USAGE_ENDPOINT=$USAGE_ENDPOINT --env PC_URL=$PC_URL --rs $CE_REGISTRY_SECRET_NAME`
	else
		RESULT=`ibmcloud ce application create --name $APP_NAME --image $BROKER_ICR_NAMESPACE_URL/$ICR_IMAGE:latest --min 1 --env BROKER_USERNAME=$BROKER_USERNAME --env BROKER_PASSWORD=$BROKER_PASSWORD --env BUILD_NUMBER=$BUILD_NUMBER --env IAM_ENDPOINT=$IAM_ENDPOINT --env USAGE_ENDPOINT=$USAGE_ENDPOINT --env PC_URL=$PC_URL --env METERING_API_KEY=$METERING_API_KEY --rs $CE_REGISTRY_SECRET_NAME`
	fi
fi

if [[ $RESULT == *"OK"* ]]; then
	APP_URL=`ibmcloud ce application get -n $APP_NAME -o url`
	if [ -z $METERING_API_KEY ] || [ $METERING_API_KEY == $EMPTY ];
	then
		UPDATE_RESULT=`ibmcloud ce application update --name $APP_NAME --image $BROKER_ICR_NAMESPACE_URL/$ICR_IMAGE:latest --min 1 --env BROKER_USERNAME=$BROKER_USERNAME --env BROKER_PASSWORD=$BROKER_PASSWORD --env BUILD_NUMBER=$BUILD_NUMBER --env IAM_ENDPOINT=$IAM_ENDPOINT --env USAGE_ENDPOINT=$USAGE_ENDPOINT --env PC_URL=$PC_URL --env BROKER_URL=$APP_URL --env DASHBOARD_URL=$APP_URL --env METERING_API_KEY=$METERING_API_KEY --rs $CE_REGISTRY_SECRET_NAME`
	else
		UPDATE_RESULT=`ibmcloud ce application update --name $APP_NAME --image $BROKER_ICR_NAMESPACE_URL/$ICR_IMAGE:latest --min 1 --env BROKER_USERNAME=$BROKER_USERNAME --env BROKER_PASSWORD=$BROKER_PASSWORD --env BUILD_NUMBER=$BUILD_NUMBER --env IAM_ENDPOINT=$IAM_ENDPOINT --env USAGE_ENDPOINT=$USAGE_ENDPOINT --env PC_URL=$PC_URL --env BROKER_URL=$APP_URL --env DASHBOARD_URL=$APP_URL --rs $CE_REGISTRY_SECRET_NAME`
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
