all:
	@echo "Available commands"
	@echo ""
	@echo "	make build"
	@echo "		build and push image on icr."
	@echo "		required env variables: "
	@echo "		 BROKER_ICR_NAMESPACE_URL, ICR_IMAGE, DEPLOYMENT_IAM_API_KEY, GC_OBJECT_ID, ONBOARDING_IAM_API_KEY"
	@echo ""
	@echo "	make deploy-cf"
	@echo "		deploy image on cloud foundry."
	@echo "		required env variables: "
	@echo "		 BROKER_ICR_NAMESPACE_URL, ICR_IMAGE, DEPLOYMENT_IAM_API_KEY, CF_API, APP_NAME, CF_ORGANIZATION, CF_SPACE"
	@echo ""
	@echo "	make deploy-ce"
	@echo "		deploy image on code engine."
	@echo "		required env variables: "
	@echo "		 BROKER_ICR_NAMESPACE_URL, ICR_IMAGE, DEPLOYMENT_IAM_API_KEY, APP_NAME, CE_REGION, CE_RESOURCE_GROUP, CE_PROJECT, CE_REGISTRY_SECRET_NAME"
	@echo ""
	@echo "	make build-deploy-cf"
	@echo "		make build + make deploy-cf."
	@echo "		required env variables: all from commands build and deploy-cf "
	@echo ""
	@echo "	make build-deploy-ce"
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
	@sudo docker run --entrypoint "./deploy/get_catalog_json.sh" -v $(shell pwd):/osb-app -i --workdir /osb-app  --env-file deploy/build.config.properties --name osb-container-catalog osb-img

build:
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

deploy:
	@echo ""
	@echo "available options: make deploy-cf"

deploy-cf:
	$(MAKE) init || $(MAKE) init-error
	$(MAKE) cf-env || $(MAKE) env-error
	@./deploy/cf/check_deploy_config_cf.sh
	@./deploy/docker_login.sh
	$(MAKE) deploy-job-cf || $(MAKE) cleanup-deploy-cf
	$(MAKE) cleanup-deploy-cf

deploy-ce:
	$(MAKE) init || $(MAKE) init-error
	$(MAKE) ce-env || $(MAKE) env-error
	@./deploy/ce/check_deploy_config_ce.sh
	@./deploy/docker_login.sh
	$(MAKE) deploy-job-ce || $(MAKE) cleanup-deploy-ce
	$(MAKE) cleanup-deploy-ce

build-deploy-cf:
	$(MAKE) init || $(MAKE) init-error
	$(MAKE) build-env || $(MAKE) env-error
	$(MAKE) cf-env || $(MAKE) env-error
	@./deploy/check_build_config.sh
	@./deploy/cf/check_deploy_config_cf.sh
	@./deploy/docker_login.sh
	$(MAKE) build-job || $(MAKE) cleanup-build
	$(MAKE) deploy-job-cf || $(MAKE) cleanup-deploy-cf
	$(MAKE) cleanup

build-deploy-ce:
	$(MAKE) init || $(MAKE) init-error
	$(MAKE) build-env || $(MAKE) env-error
	$(MAKE) ce-env || $(MAKE) env-error
	@./deploy/check_build_config.sh
	@./deploy/ce/check_deploy_config_ce.sh
	@./deploy/docker_login.sh 
	$(MAKE) build-job || $(MAKE) cleanup-build
	$(MAKE) deploy-job-ce || $(MAKE) cleanup-deploy-ce
	$(MAKE) cleanup

# Helper Goals

init:
	@echo ""
	@echo "*******************************************************************************"
	@echo "Initializing"
	@echo "*******************************************************************************"
	@echo ""
	@chmod a+x deploy/*
	@chmod a+x deploy/ce/*
	@chmod a+x deploy/cf/*
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
	@sudo docker run --entrypoint "./deploy/handle_icr_namespace.sh" -v $(shell pwd):/osb-app -i --workdir /osb-app  --env-file deploy/build.config.properties --name osb-container-namespace osb-img
	@sudo docker run --entrypoint "./deploy/install.sh" -v $(shell pwd):/osb-app -i --workdir /osb-app  --env-file deploy/build.config.properties --name osb-container-build osb-img
	@./deploy/build_image.sh $(shell pwd)

deploy-job-cf:
	@echo  starting deploy...
	@echo ""
	@echo "*******************************************************************************"
	@echo "Deploying image to cloudfoundry"
	@echo "*******************************************************************************"
	@echo ""
	@sudo docker run --entrypoint "./deploy/cf/deploy_cf.sh" -v $(shell pwd):/osb-app -i --workdir /osb-app  --env-file deploy/cf/cf.config.properties --name osb-container-deploy-cf osb-img

deploy-job-ce:
	@echo  starting deploy...
	@echo ""
	@echo "*******************************************************************************"
	@echo "Deploying image to code-engine"
	@echo "*******************************************************************************"
	@echo ""
	@./deploy/ce/ce_export_env.sh
	$(shell export $(cat deploy/ce/ce.config.properties | xargs) > /dev/null)
	@sudo docker run --entrypoint "./deploy/ce/deploy_ce.sh" -v $(shell pwd):/osb-app -i --workdir /osb-app  --env-file deploy/ce/ce.config.properties --name osb-container-deploy-ce osb-img

build-env:
	@./deploy/build_export_env.sh
	$(shell export $(cat deploy/build.config.properties | xargs) > /dev/null)

ce-env:
	@./deploy/ce/ce_export_env.sh
	$(shell export $(cat deploy/ce/ce.config.properties | xargs) > /dev/null)
	
cf-env:
	@./deploy/cf/cf_export_env.sh
	$(shell export $(cat deploy/cf/cf.config.properties | xargs) > /dev/null)

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
	$(MAKE) cleanup-deploy-cf
	$(MAKE) cleanup-deploy-ce

cleanup-build:
	@echo  ......cleaning up after build
	@sudo docker container stop osb-container-catalog > /dev/null || echo "--- clear"
	@sudo docker container rm osb-container-catalog > /dev/null || echo "--- clear"
	@sudo docker container stop osb-container-namespace > /dev/null || echo "--- clear"
	@sudo docker container rm osb-container-namespace > /dev/null || echo "--- clear"
	@sudo docker container stop osb-container-build > /dev/null || echo "--- clear"
	@sudo docker container rm osb-container-build > /dev/null || echo "--- clear"

cleanup-deploy-cf:
	@echo  ......cleaning up after deploy
	@sudo docker container stop osb-container-deploy-cf > /dev/null || echo "--- clear"
	@sudo docker container rm osb-container-deploy-cf > /dev/null || echo "--- clear"

cleanup-deploy-ce:
	@echo  ......cleaning up after deploy
	@sudo docker container stop osb-container-deploy-ce > /dev/null || echo "--- clear"
	@sudo docker container rm osb-container-deploy-ce > /dev/null || echo "--- clear"

.PHONY: all	build deploy deploy-cf build-deploy-cf get-catalog cleanup init
