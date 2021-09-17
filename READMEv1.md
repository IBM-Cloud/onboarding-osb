# Open Service Broker Reference App

## Overview
This repository provides a reference Open Service Broker (OSB) service brokers for providers to quickly deploy and test their services with IBM Cloud. These samples conform to the OSB specification (https://github.com/openservicebrokerapi/servicebroker/blob/v2.12/spec.md). The samples are to be used with the IBM Cloud Resource Management Console (RMC) (https://console.cloud.ibm.com/onboarding).

These samples include OSB logic for catalog (GET), provision (PUT), bind (PUT), unbind (DELETE), unprovision (DELETE), update service (PATCH) and get service last operation (GET). These samples also include logic for the IBM Cloud extensions of enable (PUT) and state (GET).


## Pre-requisites
1. Java 1.8 or greater

## Download the catalog.json from RMC 
1. Go to https://console.cloud.ibm.com/onboarding
2. {Steps to find and downlaod catalog.json go here}

## Deploying the Broker to Cloud Foundry
1. Clone the git repo https://github.ibm.com/dwedge/osb-sdk/tree/dev

2. Replace the sample catalog.json in the source with your service's catalog.json here   https://github.ibm.com/dwedge/osb-sdk/blob/dev/src/main/resources/data/catalog.json

3. Build and deploy to IBM CloudFoundry

        mvn clean install
        ibmcloud login --sso
        ibmcloud target -r us-south -o edge@us.ibm.com -s dev
        ibmcloud cf push osb-sdk -b https://github.com/cloudfoundry/java-buildpack.git -p target/osb-sdk.jar https://osb-sdk.mybluemix.net/v2/
    
## Deploying the Broker to Code Engine
1. Clone the git repo https://github.ibm.com/dwedge/osb-sdk/tree/dev

2. Replace the sample catalog.json in the source with your service's catalog.json here   https://github.ibm.com/dwedge/osb-sdk/blob/dev/src/main/resources/data/catalog.json

3. a. Build and deploy to CodeEngine from source

        mvn clean install
        docker build --no-cache -t osb-sdk:1 .
        docker login
        docker push
        <deployment to code engine steps go here>
    
 
## Testing the broker
Download the [postman_collection.json](postman_collection.json) and import into your postman to test out the apis

## Register the broker with RMC
Once deployed and verified, you are ready to register  the broker in RMC.
