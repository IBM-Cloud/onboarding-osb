#!/bin/bash
IAM_TEST_URL="https://iam.test.cloud.ibm.com/identity/token"
IAM_PROD_URL="https://iam.cloud.ibm.com/identity/token"
GC_TEST_URL="https://globalcatalog.test.cloud.ibm.com"
GC_PROD_URL="https://globalcatalog.cloud.ibm.com"

echo "Getting Access Token"
if [ "$ONBOARDING_ENV" = "stage" ] || [ "$ONBOARDING_ENV" = "STAGE" ]; then
	IAM_URL=$IAM_TEST_URL
	GC_URL=$GC_TEST_URL
else
	IAM_URL=$IAM_PROD_URL
	GC_URL=$GC_PROD_URL
fi
access_token_curl="`curl -X POST $IAM_URL --header 'Content-Type: application/x-www-form-urlencoded' --header 'Accept: application/json' --data-urlencode 'grant_type=urn:ibm:params:oauth:grant-type:apikey' --data-urlencode 'apikey='$ONBOARDING_IAM_API_KEY''`"
access_token=`echo $access_token_curl | jq '.access_token'`
access_token="${access_token%\"}"
access_token="${access_token#\"}"
echo "
Getting Catalog"
gcjson="`curl -X GET $GC_URL'/api/v1/'$GC_OBJECT_ID'?include=%2A&depth=100' -H 'accept: application/json' -H 'Authorization: Bearer '$access_token''`"

check_json=`echo $gcjson | jq '.name'`
if [ $check_json == null ];
then
	exit 1;
else
	echo "Catalog Json Received"
fi

children_array=`echo $gcjson | jq '.children'`
plan_array=""
firstflag=true

echo "
Converting Catalog"

for row in $(echo "${children_array}" | jq -r '.[] | @base64'); do
	_jq() {
	echo ${row} | base64 --decode | jq -r ${1}
	}
	plan_pricing_type="$(_jq '.metadata.pricing.type')"
	plan_type_free=""
	if [[ $plan_pricing_type == *"free"* ]]; then
		plan_type_free=false
	else
		plan_type_free=true
	fi

	# Pricing
	plan_pricing="`curl -X GET $(_jq '.metadata.pricing.url') -H 'Authorization: Bearer '$access_token''`";
	plan_costs='{"type": '`echo $plan_pricing | jq '.type'`', "metrics":'`echo $plan_pricing | jq '.metrics'`'}'
	plan_metadata='{"created":"'$(_jq '.created')'","updated":"'$(_jq '.updated')'","allowInternalUsers":'$(_jq '.metadata.plan.allow_internal_users')',"displayName":"'$(_jq '.overview_ui.en.display_name')'","costs":'$plan_costs'}'
	# /Pricing

	plan='{"name":"'$(_jq '.name')'","id":"'$(_jq '.id')'", "metadata":'$plan_metadata', "description":"'$(_jq '.overview_ui.en.description')'","paid":'$plan_type_free'}'
	if [ $firstflag == true ]
	then
		plan_array+="$plan"
		firstflag=false
	else
		plan_array+=", $plan"
	fi
done

plans_gen=[$plan_array]

metadata_gen='{"type":'`echo $gcjson | jq '.visibility.restrictions'`',"longDescription":'`echo $gcjson | jq '.overview_ui.en.long_description'`',"displayName":'`echo $gcjson | jq '.overview_ui.en.display_name'`',"imageUrl":'`echo $gcjson | jq '.images.image'`',"featuredImageUrl":'`echo $gcjson | jq '.images.feature_image'`',"smallImageUrl":'`echo $gcjson | jq '.images.small_image'`',"mediumImageUrl":'`echo $gcjson | jq '.images.medium_image'`',"documentationUrl":'`echo $gcjson | jq '.metadata.ui.urls.doc_url'`',"termsUrl":'`echo $gcjson | jq '.metadata.ui.urls.terms_url'`', "instructionsUrl":'`echo $gcjson | jq '.metadata.ui.urls.instructions_url'`', "parameters": '`echo $gcjson | jq '.metadata.service.parameters'`', "created": '`echo $gcjson | jq '.created'`', "updated": '`echo $gcjson | jq '.updated'`'}'

main_json='{
	"metadata":'$metadata_gen',
	"name":'`echo $gcjson | jq '.name'`',
	"id":'`echo $gcjson | jq '.id'`',
	"description":'`echo $gcjson | jq '.overview_ui.en.description'`',
	"bindable":'`echo $gcjson | jq '.metadata.service.bindable'`',
	"rc_compatible":'`echo $gcjson | jq '.metadata.rc_compatible'`',
	"iam_compatible":'`echo $gcjson | jq '.metadata.service.iam_compatible'`',
	"plan_updateable":'`echo $gcjson | jq '.metadata.service.plan_updateable'`',
	"unique_api_key":'`echo $gcjson | jq '.metadata.service.unique_api_key'`',	
	"provisionable":'`echo $gcjson | jq '.metadata.service.rc_provisionable'`',
	"plans":'"$plans_gen"'
}'

echo "
Writing Converted Catalog Json To File"

echo `echo $main_json | jq '.'` > src/main/resources/data/catalog.json
echo "
Done."
