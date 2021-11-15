#!/bin/bash

echo ""
echo "---------- Logging in cloudfoundry and ibmcloud ----------"

BUILD_NUMBER=`./deploy/generate_build_number.sh`
EMPTY='""'

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

cf login -a $CF_API -u apikey -p $DEPLOYMENT_IAM_API_KEY -o $CF_ORGANIZATION -s $CF_SPACE;

echo ""
echo "---------- Deploying to cloudfoundry ----------"
CF_DOCKER_PASSWORD=$DEPLOYMENT_IAM_API_KEY cf push $APP_NAME --docker-image $BROKER_ICR_NAMESPACE_URL/$ICR_IMAGE:latest --no-start --docker-username iamapikey

DASHBOARD_URL="https://$APP_NAME.mybluemix.net"

cf set-env $APP_NAME DASHBOARD_URL $DASHBOARD_URL
cf set-env $APP_NAME BROKER_USERNAME $BROKER_USERNAME
cf set-env $APP_NAME BROKER_PASSWORD $BROKER_PASSWORD
cf set-env $APP_NAME BUILD_NUMBER $BUILD_NUMBER
cf set-env $APP_NAME BROKER_URL $DASHBOARD_URL
cf set-env $APP_NAME IAM_ENDPOINT $IAM_ENDPOINT
cf set-env $APP_NAME USAGE_ENDPOINT $USAGE_ENDPOINT

if [ -z $METERING_API_KEY ] || [ $METERING_API_KEY == $EMPTY ];
then
	echo "METERING_API_KEY not provided"
else
	cf set-env $APP_NAME METERING_API_KEY $METERING_API_KEY
fi

if [ -z $PC_URL ] || [ $PC_URL == $EMPTY ];
then
	echo "METERING_API_KEY not provided"
else
	cf set-env $APP_NAME PC_URL $PC_URL
fi

RESULT="`cf start $APP_NAME`"
if [[ $RESULT == *"running"* ]]; then
	echo ""
	echo "*******************************************************************************"
	echo "|	Congratulations your broker is deployed!                          "
	echo "|                                                                   "
	echo "|	Broker URL: https://$APP_NAME.mybluemix.net                       "
	echo "|                                                                   "
	echo "|	Use the broker url to register in Partner Center                  "
	echo "*******************************************************************************"
	echo ""
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
