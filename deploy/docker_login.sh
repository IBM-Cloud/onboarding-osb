#!/bin/bash

echo ""
echo "Logging in docker..."

ICR_BASE=icr.io
if [ "$ONBOARDING_ENV" = "stage" ] || [ "$ONBOARDING_ENV" = "STAGE" ]; then
	ICR_BASE=stg.icr.io
fi

CONFIG_FILE=deploy/build.config.properties
C_BROKER_ICR_NAMESPACE_URL=$ICR_BASE/`cat ${CONFIG_FILE} | grep PREFIX | cut -d'=' -f2`-namespace
C_ICR_IMAGE=`cat ${CONFIG_FILE} | grep PREFIX | cut -d'=' -f2`-img

EMPTY='""'

getVar()
{
	cVarName="C_$1"
	if [ -z ${!1} ] || [ ${!1} == $EMPTY ]
	then
		echo "${!cVarName}"
	else
		echo "${!1}"
	fi
}

getGenVar()
{
	cVarName="C_$1"
	echo "${!cVarName}"
}

sudo docker logout
sudo docker login -u iamapikey -p $DEPLOYMENT_IAM_API_KEY $(getGenVar BROKER_ICR_NAMESPACE_URL)/$(getGenVar ICR_IMAGE)
