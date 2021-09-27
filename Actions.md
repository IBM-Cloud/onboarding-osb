## Use Github Actions to create and deploy your broker

1. Fork the repository


2. Update `deploy/build.config.properties` file:

```
ONBOARDING_ENV=prod
GC_OBJECT_ID=
BROKER_ICR_NAMESPACE_URL=us.icr.io/broker-namespace
ICR_IMAGE=broker-img
ICR_NAMESPACE_REGION=us-south
ICR_RESOURCE_GROUP=Default 
```

- `ONBOARDING_ENV`
  - Should be set to `stage` or `prod` based on whether the service is being onboarded to Staging RMC (used by first party services) or prod RMC and PC (used by third party services)
- `GC_OBJECT_ID`
  <!-- - To fetch catalog.json -->
  - Service ID defined in RMC for your service
    - Go to Resource Management Console  -> _Summary_ page and copy the value of ID field under the _Service details_ section.
    - Example: RMC summary for test-osb service: `https://test.cloud.ibm.com/onboarding/summary/[your-service]`
- `BROKER_ICR_NAMESPACE_URL`
  - IBM Container registry namespace where your broker container image will be uploaded. Choose from a list of namespaces [here](https://cloud.ibm.com/registry/namespaces) or  [create an ICR namespace](https://cloud.ibm.com/docs/Registry?topic=Registry-registry_setup_cli_namespace) if non exists.
  - eg. `us.icr.io/yournamespace`
- `ICR_IMAGE`
  - Image name to push on namespace


3. Update `deploy/ce/ce.config.properties`
```
APP_NAME=broker-app
BROKER_USERNAME=admin
BROKER_PASSWORD=admin
BROKER_ICR_NAMESPACE_URL=us.icr.io/broker-namespace
ICR_IMAGE=broker-img
CE_PROJECT=
CE_REGION=
CE_RESOURCE_GROUP=
CE_REGISTRY_SECRET_NAME=
```

- `APP_NAME`
  - Cloud Foundry broker application name for deployment. Tip: This name has to be unique across all of IBM Cloud Foundary application. Try using a unique indentifier in the name so that you dont run into conflicts.  
- `BROKER_USERNAME`
  - Set a username for the Broker API
- `BROKER_PASSWORD`
  - Set a password for the Broker API
  > Note: The BROKER_USERNAME and BROKER_PASSWORD values provided here also need to be configured in RMC while publishing broker. If no environment variable present for both default values would be applied from properties.
- `BROKER_ICR_NAMESPACE_URL`
  - IBM Container registry namespace where your broker container image will be uploaded. Choose from a list of namespaces [here](https://cloud.ibm.com/registry/namespaces) or  [create an ICR namespace](https://cloud.ibm.com/docs/Registry?topic=Registry-registry_setup_cli_namespace) if non exists.
  - eg. `us.icr.io/yournamespace`
- `ICR_IMAGE`
  - Provide a unqiue image name to push on namespace
- `CE_PROJECT`
  - Select a project from the [list of available projects](https://cloud.ibm.com/codeengine/projects). You can also [create a new one](https://cloud.ibm.com/docs/codeengine?topic=codeengine-manage-project#create-a-project). Note that you must have a selected project to deploy an app.
- `CE_REGION`
  - Set region for code engine deployment. This is value in the `Location` column from the [list of available projects](https://cloud.ibm.com/codeengine/projects) you would be using to create the app into. 
- `CE_RESOURCE_GROUP`
  - Select resource group to target for code engine. This is value in the  `Resource group` column from the [list of available projects](https://cloud.ibm.com/codeengine/projects) you would be using to create the app into. 
- `CE_REGISTRY_SECRET_NAME`
  - Select an exisitg registry access from your project or [create one](https://cloud.ibm.com/docs/codeengine?topic=codeengine-add-registry#add-registry-access-ce)


4. Set your secrets in your repository:
- `DEPLOYMENT_IAM_API_KEY`
    - IBM cloud api key with necessary access as descriibed in the Pre-requisites section. Also See [Creating an API key](https://cloud.ibm.com/docs/account?topic=account-userapikey&interface=ui#create_user_key)
- `ONBOARDING_IAM_API_KEY`
    - Api key to access test Global Catalog api

    
5. Go to your Actions tab


6. Select **Create and Deploy an Open Service Broker** and run it


