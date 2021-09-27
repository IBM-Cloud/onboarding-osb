#!/bin/bash
ibmcloud config --check-version=false
ibmcloud login --apikey $DEPLOYMENT_IAM_API_KEY -r $ICR_NAMESPACE_REGION
ibmcloud target -r $ICR_NAMESPACE_REGION

readarray -d / -t strarr <<<"$BROKER_ICR_NAMESPACE_URL"
NAMESPACE=${strarr[1]}

echo "rg: cr namespace-add -g $ICR_RESOURCE_GROUP $NAMESPACE"
create_namespace=`ibmcloud cr namespace-add -g $ICR_RESOURCE_GROUP $NAMESPACE`

if [[ $create_namespace == *"OK"* ]]; then
	echo ""
else
	echo "$create_namespace"
	echo "Error with namespace creation. check the logs above."
fi
