#!/bin/bash

echo ""
echo "---------- building project image ----------"

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
