#!/bin/bash

LOGIN_RESULT=""
TARGET_RESULT=""
if [ $ICR_NAMESPACE_REGION == "global" ] ||  [ $ICR_NAMESPACE_REGION == "Global" ]; then
	ibmcloud config --check-version=false
	LOGIN_RESULT="`ibmcloud login --apikey $DEPLOYMENT_IAM_API_KEY --no-region`"
	if [[ $LOGIN_RESULT == *"FAILED"* ]]; then
		echo "$LOGIN_RESULT"
		echo "Error with ibmcloud login. check the logs above."
		exit 1
	else
		echo "$LOGIN_RESULT"
		echo ""
	fi
	TARGET_RESULT="`ibmcloud target -g $ICR_RESOURCE_GROUP`"
	if [[ $TARGET_RESULT == *"FAILED"* ]]; then
		echo "$TARGET_RESULT"
		echo "Error with ibmcloud target. check the logs above."
		exit 1
	else
		echo "$TARGET_RESULT"
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
	TARGET_RESULT="`ibmcloud target -r $ICR_NAMESPACE_REGION -g $ICR_RESOURCE_GROUP`"
	if [[ $TARGET_RESULT == *"FAILED"* ]]; then
		echo "$TARGET_RESULT"
		echo "Error with ibmcloud target. check the logs above."
		exit 1
	else
		echo "$TARGET_RESULT"
		echo ""
	fi
fi


readarray -d / -t strarr <<<"$BROKER_ICR_NAMESPACE_URL"
NAMESPACE=${strarr[1]}

echo "checking namespace."
echo "new namespace will be created if failed to find namespace with name $NAMESPACE"
create_namespace=`ibmcloud cr namespace-add -g $ICR_RESOURCE_GROUP $NAMESPACE`

if [[ $create_namespace == *"OK"* ]]; then
	echo "OK
	"
else
	echo "$create_namespace"
	echo "Error with namespace creation. check the logs above."
	exit 1
fi
