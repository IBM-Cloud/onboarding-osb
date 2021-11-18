
# Open Service Broker Reference App

## Overview
The  [Open Service Broker](https://github.com/openservicebrokerapi/servicebroker/blob/v2.12/spec.md) (OSB) broker reference project allows SaaS services onboarding to the IBM Cloud catalog to quickly spin up a broker app. 

There are 2 way to onboard a service in the IBM Cloud catalog: 
  - the traditional [Resource Management Console](https://cloud.ibm.com/onboarding/dashboard)  
  - the new [Partner Center](https://cloud.ibm.com/partner-center/sell) 

As you onboard through Partner Center or RMC, you will  be required to provide your broker app URL. This readme provides a step by step guide on how to configure and deploy a reference broker app that hooks up to your service and helps you test an end to end flow early in the onboarding process. This is all done with the help of the CLI makefile automation that performs the following tasks for you:

 - builds the Java OSB broker code using maven  
 - creates a docker container image of the broker app, 
 - creates an IBM Container Registry (ICR) namespace (if it does exist) and uploads the image to the ICR namesapce 
 - deploys the app on either IBM Cloud Foundry or IBM Code Engine

> Note that this broker is not recommended being used in production but it to be used as a reference to build your production broker

## Introduction to Onboarding vs Deployment IBM Cloud Accounts
In the sections to follow you will come across many references to Onboarding and Deployment IBM Cloud account. This section tries do demystify the two.

#### _Onboarding IBM Cloud Account_ 
is the IBM Cloud account under which your SaaS service is being onboarded via RMC or Partner Center.

> Note to IBMers: If you are an IBMers, very likely you will  be onboarding on test.cloud.ibm.com but your Deployment Cloud account MUST be cloud.ibm.com. Care should be taken to create the right resources in the right cloud environment.

#### _Deployment IBM Cloud Account_ 
is the cloud account under which you will be deploying your broker app using either Code Engine or Cloud Foundry. You could choose this to be same as your Onboarding Cloud account as long as the account is on cloud.ibm.com (IBM Cloud prod)

> Note to IBMers: The Deployment  cloud account **MUST** be an account in **cloud.ibm.com** and **NOT** in test.cloud.ibm.com or any other flavor of IBM cloud. This implies, the IBM Container registry, namespace and the IBM Code Engine or Cloud Foundry app to be deployed in the sections to follow have to be on  cloud.ibm.com

## Prerequisites


1.  #### [Docker](https://docs.docker.com/engine/install/) setup locally on your computer
2. #### IBM Cloud Access
    The following privileges are required 
    1. If using RMC, you have to be added as a contributor to the service in RMC. (Ignore this for Partner Center) 
    2. You have to be invited to the IBM Cloud account where the service is being on-boarded
    3. The broker may be deployed in the on-boarding IBM Cloud account or any other IBM Cloud account - referred to as the "Deployment IBM Cloud account". You will need the following access in the  Deployment IBM Cloud Account 
        
        a. Writer and Editor access to IBM Container Registry

        b. Editor access to IBM Cloud Foundry or Code Engine
        
        Search your user and verify you have the required access [here](https://cloud.ibm.com/iam/users) 
    4. You have to be added to the service in the [Global catalog](https://globalcatalog.cloud.ibm.com) (via the Visibility tab in the UI)


<!-- 3. IBM Container Registry namespace

    The CLI automation requires an IBM Container Registry namespace to be provided in the config property - `BROKER_ICR_NAMESPACE_URL`. This is the namespace into which the OSB container image will be uploaded. Look for available namespaces [here](https://cloud.ibm.com/registry/namespaces). If you do not have a namespace you can use, we will create one for you, simply provide a unique name in the `build.config.properties` for your namespace in the _Building the Broker_ section below. 
    follow these [instructions](https://cloud.ibm.com/docs/Registry?topic=Registry-getting-started#gs_registry_namespace_add) to create one. -->

3. #### IBM Cloud API key(s)

    The project expects 3 IBM Cloud API keys. Two are covered in this section and the third one in the next section. 

    - ONBOARDING_IAM_API_KEY

      is the API key created in cloud account where the SaaS service is being onboarded via RMC or PC. The API key will used to access [Global Catalog](https://globalcatalog.cloud.ibm.com) API
      
    - DEPLOYMENT_IAM_API_KEY

      is the IBM cloud API key created in cloud account where the broker is to be deployed. The API key (and therefore the user owning the API key) must have the following access in the  Deployment IBM Cloud Account 
        
        a. Writer and Editor access to IBM Container Registry

        b. Editor access to IBM Cloud Foundry or IBM Code Engine 
      
      Find your user and verify you have the required access [here](https://cloud.ibm.com/iam/users)

      You may follow [this](https://cloud.ibm.com/docs/account?topic=account-userapikey&interface=ui#create_user_key) document to create the API keys. Once created, save the API key values to a safe location on your local machine.  

4. #### Create a ServiceID and API Key (for metering)

    To be able to test metering on your SaaS service, a new ServiceID need to be created in the [Onboarding IBM Cloud account](#onboarding-ibm-cloud-account). The service ID can be created [here](https://cloud.ibm.com/iam/serviceids). Once created, click the 3 dot menu and unlock the serviceID. Next select the ServiceID row to go to the details page and create an API key for the Service ID. Note down the API Key as this will be used as METERING_API_KEY during the deploy step   

    - METERING_API_KEY

      IBM cloud API key created using the steps mentioned above.


## Building the Broker

 ### 1. Clone the repo and cd into the directory

        git clone https://github.com/IBM-Cloud/onboarding-osb.git
        cd onboarding-osb

### 2. Fill out the  [`deploy/build.config.properties`](deploy/build.config.properties) files with the instructions provided below and export the file(s) to create OS environment variables 

   <!-- >   Note: If you do not wish to export the properties as environment variables, the deploying will still work as long as the properties are set in this properties file(s)<br />
   > Note: Environment variables take precedence over properties set in the config.properties -->

  - ONBOARDING_ENV
    - Can be set to either `stage` or `prod` based on whether the service is being on-boarded  using the staging   (used internally by IBMers only) or production environments
  - GC_OBJECT_ID
    <!-- - To fetch catalog.json -->
    - This value can be found in the Brokers tab in Partner  Center. 
    - To find it in RMC 
      - Go to Resource Management Console  -> _Summary_ page and copy the value of ID field under the _Service details_ section. Example RMC summary page url: `https://cloud.ibm.com/onboarding/summary/[your-service]`
  - #### BROKER_ICR_NAMESPACE_URL
    - The CLI automation requires an IBM Container Registry namespace in the [Deployment Cloud Account](#deployment-ibm-cloud-account) into which the OSB container image will be uploaded. You can choose one of the available namespaces [here](https://cloud.ibm.com/registry/namespaces). If you would like to create a new namespace simply provide a unique name and the automation will create one for you. If you would like to create it manually please follow [these](https://cloud.ibm.com/docs/Registry?topic=Registry-registry_setup_cli_namespace) instructions and provide the name.  
    - eg. `us.icr.io/yournamespace`
  - ICR_IMAGE
    - is the name for the broker container image that will be pushed on ICR namespace in the [Deployment Cloud Account](#deployment-ibm-cloud-account)
  - ICR_NAMESPACE_REGION
    - is the region in which the namespace is created (or is to be created). The region string to be set for ICR_NAMESPACE_REGION can be found [here](https://cloud.ibm.com/registry/start). 
    
      eg. `us-south` for Dallas, `eu-central` for Frankfurt
  - ICR_RESOURCE_GROUP
    - is the resource group under which ICR namespace exists (or will be created). The resource group name can be found [here](https://cloud.ibm.com/registry/namespaces)
  
    <br />

### 3. Set the variables in your environment using:

    export build config:
      export $(cat deploy/build.config.properties)

     
### 4. Build the broker:

Running the below command in your terminal will maven build your broker, package it as a container image and upload it to IBM Container Registry in the namespace you provided.


    DEPLOYMENT_IAM_API_KEY=your-deployment-apikey ONBOARDING_IAM_API_KEY=your-onboarding-apikey make build

> Note: When prompted, enter your local computer's password. 
  
*Congratulations!* We now have the broker application image.  
Log in to IBM Cloud and look under [ICR namespaces](https://cloud.ibm.com/registry/namespaces) to find your image. 

Our next step now is to deploy the broker application image we just created. The CLI tool we provide has 2 supported deployment platforms - [IBM Cloud Code Engine](https://www.ibm.com/cloud/code-engine) and [IBM Cloud Foundry](https://www.ibm.com/cloud/cloud-foundry). Based on the platform of your choice, just to the next appropriate section section

## Deploying the Broker on IBM Cloud Code Engine

### 1. Fill out the [`deploy/ce/ce.config.properties`](deploy/ce/ce.config.properties) files with the instructions provided below and export the file(s) to create OS environment variables.   
  - APP_NAME
    - is the application name you would like to give the broker on Code Engine.  Try using a unique identifier in the name so that you dont run into conflicts.  
  - ONBOARDING_ENV
    - should be set to either `stage` or `prod` based on whether the service is being on-boarded  using the staging   (used internally by IBMers only) or production environments
  - BROKER_USERNAME
    - is the username you would like  to set for the Broker
  - BROKER_PASSWORD
    - is the password you would like to set for the Broker 
    > Note: The BROKER_USERNAME and BROKER_PASSWORD values provided here also need to be configured in RMC while publishing broker. If no environment variable present for both default values would be applied from properties.
  - BROKER_ICR_NAMESPACE_URL
    - use the same namespace URL provided in `deploy/build.config.properties` during the build step
    - eg. `us.icr.io/yournamespace`
  - ICR_IMAGE
    - use the same image name provided in `deploy/build.config.properties` during the build step
  - CE_PROJECT
    - Select a project from the [list of available projects](https://cloud.ibm.com/codeengine/projects). You can [create a new one](https://cloud.ibm.com/docs/codeengine?topic=codeengine-manage-project#create-a-project) yourself or give us a name and we will create one for you. 
  - CE_REGION
    - Set region for IBM Code Engine deployment. This is value in the `Location` column from the [list of available projects](https://cloud.ibm.com/codeengine/projects) you would be using to create the app into. 
  - CE_RESOURCE_GROUP
    - Select resource group to target for IBM Code Engine. This is value in the  `Resource group` column from the [list of available projects](https://cloud.ibm.com/codeengine/projects) you would be using to create the app into. 
  - CE_REGISTRY_SECRET_NAME
    - Select an exisitg registry access from your project or [create one](https://cloud.ibm.com/docs/codeengine?topic=codeengine-add-registry#add-registry-access-ce)
  - PC_URL [optional]
    - Partner Center url for dashboard. use [this](https://cloud.ibm.com/partner-center/sell) to see your applications and copy url.


    
### 2. Export the variables in your environment

    
    export $(cat deploy/ce/ce.config.properties)

### 3. Deploy to IBM Code Engine

Running the below command in your terminal will deploy your app to IBM Code Engine. If you have not yet created the required API keys refer to section on [IBM Cloud API keys](#ibm-cloud-api-keys)

    DEPLOYMENT_IAM_API_KEY=your-deployment-apikey METERING_API_KEY=your-metering-apikey make deploy-ce


<br/>

## Deploying the Broker on IBM Cloud Foundry

### 1. Fill out the [`deploy/cf/config.properties`](deploy/cf/build.config.properties) files with the instructions provided below and export the file(s) to create OS environment variables    <br />

  - APP_NAME
    - Cloud Foundry broker application name for deployment. Tip: This name has to be unique across all of IBM Cloud Foundry application. Try using a unique identifier in the name so that you dont run into conflicts.  
  - ONBOARDING_ENV
    - Can be set to either `stage` or `prod` based on whether the service is being on-boarded  using the staging   (used internally by IBMers only) or production environments
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
    - Cloud Foundry API endpoint. See list of available endpoints [here](https://ondeck.console.cloud.ibm.com/docs/cloud-foundry-public?topic=cloud-foundry-public-ts-cf-apps)
  - CF_ORGANIZATION
    - Name of organization to be targeted for deployment. Your orgs can be found [here](https://cloud.ibm.com/account/cloud-foundry)
  - CF_SPACE
    -  Name of space to be targeted for deployment. After selecting your Org [here](https://cloud.ibm.com/account/cloud-foundry), a list of available spaces can be seen.
  - PC_URL [optional]
    - Partner Center url for dashboard. use [this](https://cloud.ibm.com/partner-center/sell) to see your applications and copy url.

  
      <br />

### 2. Set the variables in your environment
    
    export $(cat deploy/cf/cf.config.properties)

### 3. Deploy to IBM Cloud Foundry
Running the below command in your terminal will deploy your app to IBM Cloud Foundry. If you have not yet created the required API keys refer to section on [IBM Cloud API keys](#ibm-cloud-api-keys)

    DEPLOYMENT_IAM_API_KEY=your-deployment-apikey METERING_API_KEY=your-metering-apikey make deploy-cf 




  <br />

## Building and deploying in a single command 

The CLI tool also provides a single command that both builds and deploys the the broker app   
  
  For Cloud Foundry:

    DEPLOYMENT_IAM_API_KEY=your-deployment-apikey ONBOARDING_IAM_API_KEY=your-onboarding-apikey METERING_API_KEY=your-metering-apikey make build-deploy-cf
  
 For Code Engine:

    DEPLOYMENT_IAM_API_KEY=your-deployment-apikey ONBOARDING_IAM_API_KEY=your-onboarding-apikey METERING_API_KEY=your-metering-apikey make build-deploy-ce

  


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
- Download the [postman_collection.json](postman_collection.json) and import into your postman to test out the APIs
- Modify the planID and ServiceID in the postman collection to match your service

<!-- ## Register the broker in RMC and download the catalog.json

- Once the broker is deployed, go to Deployments in the RMC dashboard and click `Add broker` then `Manage`
![Deployments page in RMC](images/RMC-catalog-download1.png)

- Enter mandatory fields and Save. 
- Publish the Broker
- Publish the deployment
- You are all set! -->

</br>

## Guide for ui changes
[see this](client/README.md)