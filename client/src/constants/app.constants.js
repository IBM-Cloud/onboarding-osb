import env from "react-dotenv";
export const AppConst = {
	buildNumber: env.BUILD_NUMBER || "test_version_0.1",

	brokerCreds: {
		brokerUsername: env.BROKER_USERNAME || "admin",
		brokerPassword: env.BROKER_PASSWORD || "admin"
	},

	tableTitles: {
		Objects: "Objects and metadata",
		Instances: "Instances"
	},

	instanceDetailsPathParams: {
		type: "type",
		id: "instance_id"
	},

	tables: ["Objects", "Instances"],

	apiUrls: {
		getCatalog: "/v2/catalog",
		getInstances: "/support/instances",
		getMetadata: "/support/metadata",
		sendMetering: (gcId) => "/metering/"+gcId+"/usage"
	},

	tableHeaders: {
		Objects: [
			{
				header: 'Name',
				key: 'name',
			},
			{
				header: 'Kind',
				key: 'kind',
			},
			{
				header: 'ID',
				key: 'objectId',
			},
			{
				header: 'Updated',
				key: 'updated',
			}
		],
		Instances: [
			{
				header: 'Name',
				key: 'name',
			},
			{
				header: 'ID',
				key: 'instanceId',
			},
			{
				header: 'Status',
				key: 'status',
			},
			{
				header: 'Updated',
				key: 'updateDate',
			}
		],
		Metric: [
			{
				header: 'Metric',
				key: 'metric',
			},
			{
				header: 'Type',
				key: 'type',
			},
			{
				header: 'ID',
				key: 'metricId',
			}
		],
		TestMetric: [
			{
				header: 'Metric Quantity',
				key: 'metric',
			},
			{
				header: 'Cost per unit',
				key: 'cost',
			}
		]
	}
}
