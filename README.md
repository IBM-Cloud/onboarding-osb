
# Open Service Broker Reference App

## Overview
The  [Open Service Broker](https://github.com/openservicebrokerapi/servicebroker/blob/v2.12/spec.md) (OSB) broker reference project allows services onboarding to the IBM Cloud catalog to quickly spin up a broker app that is pre-configured with the service. 

There are 2 way to onboard a service in the IBM Cloud catalog: 
  - the traditional [Staging Resource Management Console](https://cloud.ibm.com/onboarding/dashboard)  
  - the new [Partner Center](https://cloud.ibm.com/partner-center/sell) 

During the onboarding process, you are required to provide your broker app URL. This readme provides a step by step guide on how to configure and deploy  the broker app. This is done using the provided CLI makefile automation. The automation performs the following tasks for you:

 - builds the Java OSB broker code using maven  
 - creates a docker container image of the broker app, 
 - creates an IBM Container Registry (ICR) namespace (if it does exist) and uploads the image to the ICR namesapce 
 - deploys the app on either IBM Cloud Foundry or IBM Code Engine

## Prerequisites



1.  [Docker](https://docs.docker.com/engine/install/) setup locally on your computer
2. The following privileges are required 
    1. Be a contributor in RMC if onboarding using RMC 
    2. Be a user in IBM Cloud account where the service is being on-boarded
    3. Writer and Editor access to IBM Container Registry
    4. Editor access to IBM Cloud Foundry or Code Engine
    5. Added to the service in the [Global catalog](https://globalcatalog.cloud.ibm.com) (via the Visibility tab in the UI)

>  Note: The broker need not be deployed on the same IBM Cloud account as the account on which the service is being on-boarded.

3. IBM Container Registry namespace

    The CLI automation requires an IBM Container Registry namespace to be provided in the config property - `BROKER_ICR_NAMESPACE_URL`. This is the namespace into which the OSB container image will be uploaded. If you do not such a namespace, we will create one for you. Simply provide a unique name in the `build.config.properties` for your namespace in the _Building the Broker_ section . 
    <!-- follow these [instructions](https://cloud.ibm.com/docs/Registry?topic=Registry-getting-started#gs_registry_namespace_add) to create one. -->

4. IBM Cloud API key(s)

    The project expects 2 IBM Cloud API keys 

    - ONBOARDING_IAM_API_KEY

      API key created in cloud account where the service is being onboarded. This is used to access test Global Catalog API
      
    - DEPLOYMENT_IAM_API_KEY

      IBM cloud API key created in cloud account where the broker is to be deployed. Necessary permission are as descriibed in the Pre-requisites section. 
      
      You may follow [this](https://cloud.ibm.com/docs/account?topic=account-userapikey&interface=ui#create_user_key) document to create the API keys. Once created, save the API key values to a safe location on your local machine.  

5. Create a ServiceID and API Key (for metering)

    To be able to test metering,  created a new ServiceID in the onboarding account. The service ID can be created [here](https://cloud.ibm.com/iam/serviceids). Click the 3 dot menu and unlock the serviceID. Next select the ServiceID row to go to the details page and create an API key for the Service ID. Note down the API Key as this will be used as METERING_API_KEY during the deploy step   

    - METERING_API_KEY

      IBM cloud API key created using the steps mentioned above.


## Building the Broker

 ### 1. Clone the repo 

        git clone https://github.com/IBM-Cloud/onboarding-osb.git

### 2. Fill out the  [`deploy/build.config.properties`](deploy/build.config.properties) files with the instructions provided below and export the file(s) to create OS environment variables 


   >   Note: If you do not wish to export the properties as environment variables, the deploying will still work as long as the properties are set in this properties file(s):<br />
   > Note: Environment variables take precedence over properties set in the config.properties



  - ONBOARDING_ENV
    - Can be set to either `stage` or `prod` based on whether the service is being on-boarded  using the staging   (used internally by IBMers only) or production environments
  - GC_OBJECT_ID
    <!-- - To fetch catalog.json -->
    - This value can be found in the Brokers tab in Partner  Center. 
    - To find it in RMC 
      - Go to Resource Management Console  -> _Summary_ page and copy the value of ID field under the _Service details_ section. Example RMC summary page url: `https://cloud.ibm.com/onboarding/summary/[your-service]`
  - BROKER_ICR_NAMESPACE_URL
    - IBM Container registry namespace where your broker container image will be uploaded. Choose from a list of namespaces [here](https://cloud.ibm.com/registry/namespaces) or  [create an ICR namespace](https://cloud.ibm.com/docs/Registry?topic=Registry-registry_setup_cli_namespace) if non exists.
    - eg. `us.icr.io/yournamespace`
  - ICR_IMAGE
    - provide a name for the broker container image that will be pushed on ICR namespace
  - ICR_NAMESPACE_REGION
    - Region for ICR namespace
  - ICR_RESOURCE_GROUP
    - Resource group for ICR namespace
  
    <br />

### 3. Set the variables in your environment using:

    export build config:
      export $(cat deploy/build.config.properties)

     
### 4. Build the broker:

    DEPLOYMENT_IAM_API_KEY=your-deployment-apikey ONBOARDING_IAM_API_KEY=your-onboarding-apikey make build
  
*Congratulations!* We now have the broker application image.  
The automation did a maven build on the broker application, created a container image and push it to IBM Container Registry. Log in to IBM Cloud and look under ICR namespaces to find your image. 

Our next step now is to deploy the broker application image we just created. The CLI tool we provide has 2 supported deployment platforms - [IBM Cloud Code Engine](https://www.ibm.com/cloud/code-engine) and [IBM Cloud Foundry](https://www.ibm.com/cloud/cloud-foundry). Based on the platform of your choice, just to the next appropriate section section

## Deploying the Broker on IBM Cloud Code Engine

### 1. Fill out the [`deploy/ce/ce.config.properties`](deploy/ce/ce.config.properties) files with the instructions provided below and export the file(s) to create OS environment variables.   
  - APP_NAME
    - Application name for the broker on Code Engine  Try using a unique identifier in the name so that you dont run into conflicts.  
  - ONBOARDING_ENV
    - Can be set to either `stage` or `prod` based on whether the service is being on-boarded  using the staging   (used internally by IBMers only) or production environments
  - BROKER_USERNAME
    - Set a username for the Broker API
  - BROKER_PASSWORD
    - Set a password for the Broker API
    > Note: The BROKER_USERNAME and BROKER_PASSWORD values provided here also need to be configured in RMC while publishing broker. If no environment variable present for both default values would be applied from properties.
  - BROKER_ICR_NAMESPACE_URL
    - IBM Container registry namespace where your broker container image will be uploaded. Choose from a list of namespaces [here](https://cloud.ibm.com/registry/namespaces) or  [create an ICR namespace](https://cloud.ibm.com/docs/Registry?topic=Registry-registry_setup_cli_namespace) if non exists.
    - eg. `us.icr.io/yournamespace`
  - ICR_IMAGE
    - Provide a name for the broker container image that will be pushed on ICR namespace

  - CE_PROJECT
    - Select a project from the [list of available projects](https://cloud.ibm.com/codeengine/projects). You can [create a new one](https://cloud.ibm.com/docs/codeengine?topic=codeengine-manage-project#create-a-project) yourself or give us a name and we will create one for you. 
  - CE_REGION
    - Set region for code engine deployment. This is value in the `Location` column from the [list of available projects](https://cloud.ibm.com/codeengine/projects) you would be using to create the app into. 
  - CE_RESOURCE_GROUP
    - Select resource group to target for code engine. This is value in the  `Resource group` column from the [list of available projects](https://cloud.ibm.com/codeengine/projects) you would be using to create the app into. 
  - CE_REGISTRY_SECRET_NAME
    - Select an exisitg registry access from your project or [create one](https://cloud.ibm.com/docs/codeengine?topic=codeengine-add-registry#add-registry-access-ce)


    
### 2. Set the variables in your environment

    
    export $(cat deploy/ce/ce.config.properties)

### 3. Deploy to ce

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

  
      <br />

### 2. Set the variables in your environment
    
    export $(cat deploy/cf/cf.config.properties)

### 3. Deploy to cf

    DEPLOYMENT_IAM_API_KEY=your-deployment-apikey METERING_API_KEY=your-metering-apikey make deploy-cf 

  Deploys the broker on IBM Cloud Foundry 


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
