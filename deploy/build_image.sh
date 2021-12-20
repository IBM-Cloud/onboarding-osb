#!/bin/bash

echo ""
echo "---------- building project image ----------"

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
BROKER_ICR_NAMESPACE_URL=$(getGenVar BROKER_ICR_NAMESPACE_URL)
ICR_IMAGE=$(getGenVar ICR_IMAGE)

sudo docker build -f Dockerfile -t $ICR_IMAGE $1
sudo docker tag $ICR_IMAGE $BROKER_ICR_NAMESPACE_URL/$ICR_IMAGE
RESULT="`sudo docker push $BROKER_ICR_NAMESPACE_URL/$ICR_IMAGE`"

if [[ $RESULT == *"Pushed"* ]]; then
	echo ""
	echo "*******************************************************************************"
	echo "|                                                                   "
	echo "|Image is successfully pushed on [$BROKER_ICR_NAMESPACE_URL/$ICR_IMAGE]"
	echo "|                                                                   "
	echo "*******************************************************************************"
	echo ""
else
	echo "$RESULT"
	echo ""
	echo "*******************************************************************************"
	echo "|                                                                   "
	echo "|Error while deploying image. check the logs above."
	echo "|                                                                   "
	echo "*******************************************************************************"
	echo ""
fi
