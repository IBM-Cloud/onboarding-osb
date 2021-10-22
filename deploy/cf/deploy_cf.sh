#!/bin/bash

echo ""
echo "---------- Logging in cloudfoundry and ibmcloud ----------"

BUILD_NUMBER=`./deploy/generate_build_number.sh`

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
