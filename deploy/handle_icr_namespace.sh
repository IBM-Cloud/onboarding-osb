#!/bin/bash

LOGIN_RESULT=""
REGION_RESULT=""
if [ $ICR_NAMESPACE_REGION == "global" ] ||  [ $ICR_NAMESPACE_REGION == "Global" ]; then
	ibmcloud config --check-version=false
	LOGIN_RESULT=`ibmcloud login --apikey $DEPLOYMENT_IAM_API_KEY --no-region`
	if [[ $LOGIN_RESULT == *"FAILED"* ]]; then
		echo "$LOGIN_RESULT"
		echo "Error with ibmcloud login. check the logs above."
		exit 1
	else
		echo "$LOGIN_RESULT"
		echo ""
	fi
	echo ""
else
	ibmcloud config --check-version=false
	LOGIN_RESULT="`ibmcloud login --apikey $DEPLOYMENT_IAM_API_KEY -r $ICR_NAMESPACE_REGION`"
	if [[ $LOGIN_RESULT == *"FAILED"* ]]; then
		echo "$LOGIN_RESULT"
		echo "Error with ibmcloud login. check the logs above."
		exit 1
	else
		echo "$LOGIN_RESULT"
		echo ""
	fi
	REGION_RESULT=`ibmcloud target -r $ICR_NAMESPACE_REGION`
	if [[ $REGION_RESULT == *"FAILED"* ]]; then
		echo "$REGION_RESULT"
		echo "Error with ibmcloud region. check the logs above."
		exit 1
	else
		echo "$REGION_RESULT"
		echo ""
	fi
fi


readarray -d / -t strarr <<<"$BROKER_ICR_NAMESPACE_URL"
NAMESPACE=${strarr[1]}

create_namespace=`ibmcloud cr namespace-add -g $ICR_RESOURCE_GROUP $NAMESPACE`

if [[ $create_namespace == *"OK"* ]]; then
	echo ""
else
	echo "$create_namespace"
	echo "Error with namespace creation. check the logs above."
	exit 1
fi
