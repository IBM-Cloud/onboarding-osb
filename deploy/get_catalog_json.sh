#!/bin/bash

echo "inside get_catalog_json";
echo "gc_id: ${GC_OBJECT_ID}";


echo "
Getting Access Token"
access_token_curl="`curl -X POST "https://iam.test.cloud.ibm.com/identity/token" --header 'Content-Type: application/x-www-form-urlencoded' --header 'Accept: application/json' --data-urlencode 'grant_type=urn:ibm:params:oauth:grant-type:apikey' --data-urlencode 'apikey='$ONBOARDING_IAM_API_KEY''`"
access_token=`echo $access_token_curl | jq '.access_token'`
access_token="${access_token%\"}"
access_token="${access_token#\"}"
echo "
Getting Catalog"
gcjson="`curl -X GET 'https://globalcatalog.test.cloud.ibm.com/api/v1/'$GC_OBJECT_ID'?include=%2A&depth=100' -H 'accept: application/json' -H 'Authorization: Bearer '$access_token''`"

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
	plan='{"name":"'$(_jq '.name')'","id":"'$(_jq '.id')'","description":"'$(_jq '.overview_ui.en.description')'","paid":true}'
	if [ $firstflag == true ]
	then
		plan_array+="$plan"
		firstflag=false
	else
		plan_array+=", $plan"
	fi
done

plans_gen=[$plan_array]

metadata_gen='{"type":'`echo $gcjson | jq '.visibility.restrictions'`',"longDescription":'`echo $gcjson | jq '.overview_ui.en.long_description'`',"displayName":'`echo $gcjson | jq '.overview_ui.en.display_name'`',"imageUrl":'`echo $gcjson | jq '.images.image'`',"featuredImageUrl":'`echo $gcjson | jq '.images.feature_image'`',"smallImageUrl":'`echo $gcjson | jq '.images.small_image'`',"mediumImageUrl":'`echo $gcjson | jq '.images.medium_image'`',"documentationUrl":'`echo $gcjson | jq '.metadata.ui.urls.doc_url'`',"termsUrl":'`echo $gcjson | jq '.metadata.ui.urls.terms_url'`', "instructionsUrl":'`echo $gcjson | jq '.metadata.ui.urls.instructions_url'`', "parameters": '`echo $gcjson | jq '.metadata.service.parameters'`'}'

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
