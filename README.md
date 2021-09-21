
# Open Service Broker Reference App

## Overview
This repository provides a reference [Open Service Broker](https://github.com/openservicebrokerapi/servicebroker/blob/v2.12/spec.md) (OSB)  broker for first party and third party service providers to quickly deploy and onboard their services on IBM Cloud. 

There are 2 way to onboard a service into IBM Cloud 
  - the traditional [Staging Resource Management Console](https://test.cloud.ibm.com/onboarding/dashboard) for first party and [Resource Management Console](https://cloud.ibm.com/onboarding/dashboard) for third party services 
  - the new [Partner Center](https://test.cloud.ibm.com/partner-center/sell) for third party services.

This broker project can be used with either of the onboarding approaches.

During the course of this setup, you would be using the CLI automation provided to: 

 - configure and build the Java OSB broker code  
 - create a docker container image of the broker, 
 - upload it to IBM Container Registry 
 - create an IBM Cloud Foundry or IBM Code Engine app using the OSB container image 

## Pre-requisites


### Software and Services
1.  [Docker](https://docs.docker.com/engine/install/) setup locally on your computer
### Privlilges

The following privliges are required 
1. Be a contributor in RMC if onboarding using RMC 
2. Be a user in IBM Cloud account where the service is being onboarded
3. Writer and Editor access to IBM Container Registry
3. Editor access to IBM Cloud Foundry or Code Engine


>  Note: The broker need not be deployed on the same IBM Cloud account on which the service is being onboarded.

## Creating an IBM Container Registry namespace

The CLI automation requires an IBM Container Registry namespace to be provided in the config property - `BROKER_ICR_NAMESPACE_URL`. This is the namespace into which the OSB container image will be uploaded. If you do not have access to such a namespace, please follow these [instructions](https://cloud.ibm.com/docs/Registry?topic=Registry-getting-started#gs_registry_namespace_add) to create one.


## Deploying the Broker to Cloud Foundry

1. Clone the repo 

        git clone https://github.com/IBM-Cloud/onboarding-osb.git

2. Fill out the 2 `build.config.properties` with the instructions provided below and export the file(s) to create OS environment variables 

   >   Note: If you do not want to export the properties as environment variables, the deploying will still work as long as the properties are set in this properties file(s):<br />
   > Note: Environment variables take precedence over properties set in the config.properties

    - build properties:  [deploy/build.config.properties](deploy/build.config.properties) <br />
    - deploy properties: 
      - A. for code engine [deploy/ce/ce.config.properties](deploy/ce/ce.config.properties) 
      - B. for cloud foundry [deploy/cf/cf.config.properties](deploy/cf/cf.config.properties)
   

    ## Build properties

    - ONBOARDING_ENV
      - Should be set to `stage` or `prod` based on whether the service is being onboarded to Staging RMC (used by first party services) or prod RMC and PC (used by third party services)
    - GC_OBJECT_ID
      <!-- - To fetch catalog.json -->
      - Service ID defined in RMC for your service
        - Go to Resource Management Console  -> _Summary_ page and copy the value of ID field under the _Service details_ section.
        - Example: RMC summary for test-osb service: `https://test.cloud.ibm.com/onboarding/summary/[your-service]`
    - ONBOARDING_IAM_API_KEY
      - Api key to access test Global Catalog api
    - DEPLOYMENT_IAM_API_KEY 
        - IBM cloud api key with necessary access as descriibed in the Pre-requisites section. Also See [Creating an API key](https://cloud.ibm.com/docs/account?topic=account-userapikey&interface=ui#create_user_key)
    - BROKER_ICR_NAMESPACE_URL
      - IBM Container registry namespace where your broker container image will be uploaded. Choose from a list of namespaces [here](https://cloud.ibm.com/registry/namespaces) or  [create an ICR namespace](https://cloud.ibm.com/docs/Registry?topic=Registry-registry_setup_cli_namespace) if non exists.
      - eg. `us.icr.io/yournamespace`
    - ICR_IMAGE
      - Image name to push on namespace
  
    <br />

    ## Deployment properties
    <br />

    ### A. Code Engine deployment properties
    
    <br />

    - DEPLOYMENT_IAM_API_KEY 
      - IBM cloud api key with necessary access as descriibed in the Pre-requisites section. Also See [Creating an API key](https://cloud.ibm.com/docs/account?topic=account-userapikey&interface=ui#create_user_key)
    - APP_NAME
      - Cloud Foundry broker application name for deployment. Tip: This name has to be unique across all of IBM Cloud Foundary application. Try using a unique indentifier in the name so that you dont run into conflicts.  
    - BROKER_USERNAME
      - Set a username for the Broker API
    - BROKER_PASSWORD
      - Set a password for the Broker API
      > Note: The BROKER_USERNAME and BROKER_PASSWORD values provided here also need to be configured in RMC while publishing broker. If no environment variable present for both default values would be applied from properties.
    - BROKER_ICR_NAMESPACE_URL
      - IBM Container registry namespace where your broker container image will be uploaded. Choose from a list of namespaces [here](https://cloud.ibm.com/registry/namespaces) or  [create an ICR namespace](https://cloud.ibm.com/docs/Registry?topic=Registry-registry_setup_cli_namespace) if non exists.
      - eg. `us.icr.io/yournamespace`
    - ICR_IMAGE
      - Provide a unqiue image name to push on namespace
    - CE_PROJECT
      - Select a project from the [list of available projects](https://cloud.ibm.com/codeengine/projects). You can also [create a new one](https://cloud.ibm.com/docs/codeengine?topic=codeengine-manage-project#create-a-project). Note that you must have a selected project to deploy an app.
    - CE_REGION
      - Set region for code engine deployment. This is value in the `Location` column from the [list of available projects](https://cloud.ibm.com/codeengine/projects) you would be using to create the app into. 
    - CE_RESOURCE_GROUP
      - Select resource group to target for code engine. This is value in the  `Resource group` column from the [list of available projects](https://cloud.ibm.com/codeengine/projects) you would be using to create the app into. 
    - CE_REGISTRY_SECRET_NAME
      - Select an exisitg registry access from your project or [create one](https://cloud.ibm.com/docs/codeengine?topic=codeengine-add-registry#add-registry-access-ce)
  
    <br />

    ### B. Cloud Foundry deployment properties
    <br />

    - DEPLOYMENT_IAM_API_KEY 
      - IBM cloud api key with necessary access as descriibed in the Pre-requisites section. Also See [Creating an API key](https://cloud.ibm.com/docs/account?topic=account-userapikey&interface=ui#create_user_key)
    - APP_NAME
      - Cloud Foundry broker application name for deployment. Tip: This name has to be unique across all of IBM Cloud Foundary application. Try using a unique indentifier in the name so that you dont run into conflicts.  
    - BROKER_USERNAME
      - Set a username for the Broker API
    - BROKER_PASSWORD
      - Set a password for the Broker API
      > Note: The BROKER_USERNAME and BROKER_PASSWORD values provided here also need to be configured in RMC while publishing broker.
    - BROKER_ICR_NAMESPACE_URL
      - IBM Container registry namespace where your broker container image will be uploaded. Choose from a list of namespaces [here](https://cloud.ibm.com/registry/namespaces) or  [create an ICR namespace](https://cloud.ibm.com/docs/Registry?topic=Registry-registry_setup_cli_namespace) if non exists.
      - eg. `us.icr.io/yournamespace`
    - ICR_IMAGE
      - Image name to push on namespace
    - CF_API
      - Cloudfoundry api endpoint. See list of available endpoints [here](https://ondeck.console.cloud.ibm.com/docs/cloud-foundry-public?topic=cloud-foundry-public-ts-cf-apps)
    - CF_ORGANIZATION
      - Name of organization to be targeted for deployment. Your orgs can be found [here](https://cloud.ibm.com/account/cloud-foundry)
    - CF_SPACE
      -  Name of space to be targeted for deployment. After selecting your Org [here](https://cloud.ibm.com/account/cloud-foundry), a list of available spaces can be seen

  
      <br />

    ## Set the variables in your environment using:

        export build config:
          export $(cat deploy/build.config.properties)

        export code engine config properties:
          export $(cat deploy/ce/ce.config.properties)

        export code engine config properties:
          export $(cat deploy/cf/cf.config.properties)

      <br />

3. Building and deploying the broker 

      - Step 1: Build the broker project, create a docker image and push to IBM Container Registry

        `make build`
        <!-- - build and push image on icr. -->
        <!-- - requires following env variables to be set: 
          - BROKER_ICR_NAMESPACE_URL, DEPLOYMENT_IAM_API_KEY, GC_OBJECT_ID, ONBOARDING_IAM_API_KEY -->

      - Step 2: Deploy the image on Cloud Foundry or Code Engine
      
	      `make deploy-cf`
		
        <!-- - deploy image on cloud foundry. -->
        <!-- - requires the following env variables to be set: 
          - BROKER_ICR_NAMESPACE_URL, DEPLOYMENT_IAM_API_KEY, CF_API, APP_NAME, CF_ORGANIZATION, CF_SPACE -->

        #### OR

        
	      `make deploy-ce`
		
        <!-- - deploy image on code engine. -->
        <!-- - requires the following env variables to be set: 
          - BROKER_ICR_NAMESPACE_URL, DEPLOYMENT_IAM_API_KEY, APP_NAME, CE_REGION, CE_RESOURCE_GROUP, CE_PROJECT, CE_REGISTRY_SECRET_NAME -->
			
        Alternately, Steps 1 and 2 can be run as a single command to build and deploy the broker to cloud foundary or code engine  
   
        `make build-deploy-cf`
        - make build + make deploy-cf
  
        `make build-deploy-ce`
        - make build + make deploy-ce


<!-- ## Deploying the Broker to Cloud Foundry from Windows
1. Clone the repo 

        `git clone git@github.ibm.com:dwedge/osb-sdk.git`

2. Update the deployment properties in  [scripts/deploy_approach_windows/build.config.properties](scripts/deploy_approach_windows/build.config.properties) 

        APP_NAME=
        CF_API=
        CF_SPACE=
        CF_ORGANIZATION=
        # CATALOG_ID=
	
3. Deploy the broker to IBM Cloud Foundry

        deploy build-deploy <iamapikey> -->


<!-- ## Deploying the Broker to Code Engine
1. CLone the repo 

        `git clone git@github.ibm.com:dwedge/osb-sdk.git`

2. Update the deployment properties in  `build.config.properties` 

        APP_NAME=
        API_ENDPOINT=
        REGION=
        SPACE=
        ORG=
        CATALOG_ID=


3. Deploy the broker to IBM Cloud Foundry

        make cf apikey=<iamapikey> -->
 

## Testing the broker
- Download the [postman_collection.json](postman_collection.json) and import into your postman to test out the apis
- Modify the planID and ServiceID in the postman collection to match your service

## Register the broker in RMC and download the catalog.json

- Once the broker is deployed, go to Deployments in the RMC dashboard and click `Add broker` then `Manage`
![Deployments page in RMC](images/RMC-catalog-download1.png)

- Enter mandatory fields and Save. 
- Publish the Broker
- Publish the deployment
- You are all set!
