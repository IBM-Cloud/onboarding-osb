all:
	@echo "Available commands"
	@echo ""
	@echo "	make build"
	@echo ""
	@echo "		DEPLOYMENT_IAM_API_KEY=your-deployment-apikey ONBOARDING_IAM_API_KEY=your-onboarding-apikey make build"
	@echo ""
	@echo "		build and push image on icr."
	@echo "		required env variables: "
	@echo "		 ONBOARDING_ENV, GC_OBJECT_ID, BROKER_ICR_NAMESPACE_URL, ICR_IMAGE, ICR_NAMESPACE_REGION, ICR_RESOURCE_GROUP"
	@echo ""
	@echo "	make deploy-ce"
	@echo ""
	@echo "		DEPLOYMENT_IAM_API_KEY=your-deployment-apikey METERING_API_KEY=your-metering-apikey make deploy-ce"
	@echo ""
	@echo "		deploy image on code engine."
	@echo "		required env variables: "
	@echo "		 APP_NAME, BROKER_USERNAME, BROKER_PASSWORD, BROKER_ICR_NAMESPACE_URL, ICR_IMAGE, CE_PROJECT, CE_REGION, CE_RESOURCE_GROUP, CE_REGISTRY_SECRET_NAME"
	@echo ""
	@echo "	make build-deploy-ce"
	@echo ""
	@echo "		DEPLOYMENT_IAM_API_KEY=your-deployment-apikey ONBOARDING_IAM_API_KEY=your-onboarding-apikey METERING_API_KEY=your-metering-apikey make build-deploy-ce"
	@echo ""
	@echo "		make build + make deploy-ce."
	@echo "		required env variables: all from commands build and deploy-ce "
	@echo ""
	@echo "Refer README for more information"

help: 
	$(MAKE) all

# Main goals

get-catalog:
	@echo ""
	@echo "*******************************************************************************"
	@echo "Getting catalog.json"
	@echo "*******************************************************************************"
	@echo ""
	@sudo docker run --entrypoint "./deploy/get_catalog_json.sh" -v $(shell pwd):/osb-app -i --workdir /osb-app  --env-file deploy/build.config.properties -e DEPLOYMENT_IAM_API_KEY=${DEPLOYMENT_IAM_API_KEY} -e ONBOARDING_IAM_API_KEY=${ONBOARDING_IAM_API_KEY} --name osb-container-catalog osb-img

build:
	date +%s > _time_$@.txt
	$(MAKE) init || $(MAKE) init-error
	$(MAKE) build-env || $(MAKE) env-error
	@./deploy/check_build_config.sh
	@echo ""
	@echo "*******************************************************************************"
	@echo "Logging to ibm container registry on docker"
	@echo "*******************************************************************************"
	@echo ""
	@./deploy/docker_login.sh
	$(MAKE) build-job || $(MAKE) cleanup-build
	$(MAKE) cleanup-build
	@echo ""
	@./deploy/convert_time.sh $$(($$(date +%s)-$$(cat  _time_$@.txt))) $@
	@rm _time_$@.txt

deploy:
	@echo ""
	@echo "available options: make deploy-ce"

deploy-ce:
	date +%s > _time_$@.txt
	$(MAKE) init || $(MAKE) init-error
	$(MAKE) ce-env || $(MAKE) env-error
	@./deploy/ce/check_deploy_config_ce.sh
	@./deploy/docker_login.sh
	$(MAKE) deploy-job-ce || $(MAKE) cleanup-deploy-ce
	$(MAKE) cleanup-deploy-ce
	@echo ""
	@./deploy/convert_time.sh $$(($$(date +%s)-$$(cat  _time_$@.txt))) $@
	@rm _time_$@.txt

build-deploy-ce:
	date +%s > _time_$@.txt
	$(MAKE) init || $(MAKE) init-error
	$(MAKE) build-env || $(MAKE) env-error
	$(MAKE) ce-env || $(MAKE) env-error
	@./deploy/check_build_config.sh
	@./deploy/ce/check_deploy_config_ce.sh
	@./deploy/docker_login.sh 
	$(MAKE) build-job || $(MAKE) cleanup-build
	$(MAKE) deploy-job-ce || $(MAKE) cleanup-deploy-ce
	$(MAKE) cleanup-build
	$(MAKE) cleanup-deploy-ce
	@echo ""
	@./deploy/convert_time.sh $$(($$(date +%s)-$$(cat  _time_$@.txt))) $@
	@rm _time_$@.txt

# Helper Goals

init:
	@echo ""
	@echo "*******************************************************************************"
	@echo "Initializing"
	@echo "*******************************************************************************"
	@echo ""
	@chmod a+x deploy/*
	@chmod a+x deploy/ce/*
	@echo $(shell pwd)

build-job:
	@echo  starting build...
	@echo ""
	@echo "*******************************************************************************"
	@echo "Building docker image for environment"
	@echo "*******************************************************************************"
	@echo ""
	@echo "This may take a while. don't terminate process..."
	@sudo docker build -q -f deploy/Dockerfile -t osb-img $(shell pwd)
	$(MAKE) get-catalog || $(MAKE) catalog-json-error
	@echo ""
	@echo "*******************************************************************************"
	@echo "Building and pushing image to ibm container registry"
	@echo "*******************************************************************************"
	@echo ""
	@sudo docker run --entrypoint "./deploy/handle_icr_namespace.sh" -v $(shell pwd):/osb-app -i --workdir /osb-app  --env-file deploy/build.config.properties -e DEPLOYMENT_IAM_API_KEY=${DEPLOYMENT_IAM_API_KEY} -e ONBOARDING_IAM_API_KEY=${ONBOARDING_IAM_API_KEY} --name osb-container-namespace osb-img
	@sudo docker run --entrypoint "./deploy/install.sh" -v $(shell pwd):/osb-app -i --workdir /osb-app  --env-file deploy/build.config.properties --name osb-container-build osb-img
	@./deploy/build_image.sh $(shell pwd)

deploy-job-ce:
	@echo  starting deploy...
	@echo ""
	@echo "*******************************************************************************"
	@echo "Deploying image to code-engine"
	@echo "*******************************************************************************"
	@echo ""
	@./deploy/ce/ce_export_env.sh
	$(shell export $(cat deploy/ce/ce.config.properties | xargs) > /dev/null)
	@sudo docker run --entrypoint "./deploy/ce/deploy_ce.sh" -v $(shell pwd):/osb-app -i --workdir /osb-app  --env-file deploy/ce/ce.config.properties -e METERING_API_KEY=${METERING_API_KEY} -e DEPLOYMENT_IAM_API_KEY=${DEPLOYMENT_IAM_API_KEY} --name osb-container-deploy-ce osb-img

build-env:
	@./deploy/build_export_env.sh
	$(shell export $(cat deploy/build.config.properties | xargs) > /dev/null)

ce-env:
	@./deploy/ce/ce_export_env.sh
	$(shell export $(cat deploy/ce/ce.config.properties | xargs) > /dev/null)

error:
	@echo  ......Error encountered......
	@echo ""
	@echo "*******************************************************************************"
	@echo "Cleanup if error"
	@echo "*******************************************************************************"
	@echo ""
	$(MAKE) cleanup

init-error:
	@echo "*******************************************************************************"
	@echo "Error encountered while initializing"
	@echo "*******************************************************************************"
	exit 1

env-error:
	@echo "*******************************************************************************"
	@echo "Error reading config properties"
	@echo "*******************************************************************************"
	exit 1

catalog-json-error:
	@echo ""
	@echo "*******************************************************************************"
	@echo "Error while fetching catalog json encountered"
	@echo "Verify if GC_OBJECT_ID is right"
	@echo "*******************************************************************************"
	@echo ""
	exit 1

cleanup:
	@echo ""
	@echo "*******************************************************************************"
	@echo "Cleaning up in order components created"
	@echo "*******************************************************************************"
	@echo ""
	$(MAKE) cleanup-build
	$(MAKE) cleanup-deploy-ce
	@echo "Full cleanup done."

cleanup-build:
	@echo  ......cleaning up after build
	@sudo docker container stop osb-container-catalog > /dev/null || $(MAKE) skip-message
	@sudo docker container rm osb-container-catalog > /dev/null || $(MAKE) skip-message
	@sudo docker container stop osb-container-namespace > /dev/null || $(MAKE) skip-message
	@sudo docker container rm osb-container-namespace > /dev/null || $(MAKE) skip-message
	@sudo docker container stop osb-container-build > /dev/null || $(MAKE) skip-message
	@sudo docker container rm osb-container-build > /dev/null || $(MAKE) skip-message
	@echo "Done."

cleanup-deploy-ce:
	@echo  ......cleaning up after ce deploy
	@sudo docker container stop osb-container-deploy-ce > /dev/null || $(MAKE) skip-message
	@sudo docker container rm osb-container-deploy-ce > /dev/null || $(MAKE) skip-message
	@echo "Done."

skip-message:
	@echo 
	@echo Already clean. skipping.
	@echo

.PHONY: all	build deploy get-catalog cleanup init
