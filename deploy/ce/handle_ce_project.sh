#!/bin/bash

ibmcloud config --check-version=false

IBM_API_ENDPOINT="https://cloud.ibm.com"
if [ "$DEPLOYMENT_ENV" = "stage" ] || [ "$DEPLOYMENT_ENV" = "STAGE" ]; then
	IBM_API_ENDPOINT="https://test.cloud.ibm.com"
fi

LOGIN_RESULT="`ibmcloud login --apikey $DEPLOYMENT_IAM_API_KEY -a $IBM_API_ENDPOINT -r $CE_REGION -g $CE_RESOURCE_GROUP`"
if [[ $LOGIN_RESULT == *"FAILED"* ]]; then
	echo "$LOGIN_RESULT"
	echo "Error with ibmcloud login. check the logs above."
	exit 1
else
	echo "$LOGIN_RESULT"
	echo ""
fi
TARGET_RESULT="`ibmcloud target -r $CE_REGION -g $CE_RESOURCE_GROUP`"
if [[ $TARGET_RESULT == *"FAILED"* ]]; then
	echo "$TARGET_RESULT"
	echo "Error with ibmcloud target. check the logs above."
	exit 1
else
	echo "$TARGET_RESULT"
	echo ""
fi
echo "checking project. new project will be created if failed to find project with name $CE_PROJECT"
get_project="`ibmcloud ce project get -n $CE_PROJECT`"

if [[ $get_project == *"OK"* ]]; then
	echo "
	Project found."
else
	echo "
	$CE_PROJECT does not exist."
	echo "
	creating $CE_PROJECT."
	create_project=`ibmcloud ce project create -n $CE_PROJECT`
	if [[ $create_project == *"OK"* ]]; then
		echo "
		Project created."
	else
		echo "
		$create_project"
		echo "Error with project creation. check the logs above."
		exit 1
	fi
fi
