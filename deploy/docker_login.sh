#!/bin/bash

echo ""
echo "Logging in docker..."
sudo docker logout
sudo docker login -u iamapikey -p $DEPLOYMENT_IAM_API_KEY $BROKER_ICR_NAMESPACE_URL/$ICR_IMAGE
