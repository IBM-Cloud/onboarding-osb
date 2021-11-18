import mockInstances from '../constants/mock_instance.json';
import { useLocation } from 'react-router-dom';

export const getRowDataFromCatalog = (services) => {
	let rowData = [];
	services.map((service => {
		const { id, id: objectId, name, metadata } = service;
		rowData.push({
			id,
			objectId,
			name,
			kind: "Service",
			updated: new Date(metadata.updated).toLocaleDateString("en-US", { day: "numeric", month: "short", year: "numeric", hour: "2-digit", minute: "2-digit", second: "2-digit" }, "-")
		});
		service.plans.map((plan) => {
			const { id, id: objectId, name, metadata } = plan;
			rowData.push({
				id,
				objectId,
				name,
				kind: "Pricing Plan",
				updated: new Date(metadata.updated).toLocaleDateString("en-US", { day: "numeric", month: "short", year: "numeric", hour: "2-digit", minute: "2-digit", second: "2-digit" }, "-")
			});
			return true
		});
		return true;
	}))
	return rowData;
};

export const getInstanceRows = (instances) => {
	let rowData = [];
	instances.map((instance) => {
		const { instanceId, name, planId, status, region, updateDate } = instance;
		rowData.push({
			id: instanceId,
			instanceId,
			name,
			planId,
			status,
			region,
			updateDate: new Date(updateDate).toLocaleDateString("en-US", { day: "numeric", month: "short", year: "numeric", hour: "2-digit", minute: "2-digit", second: "2-digit" }, "-")
		})
		return true;
	})
	return rowData;
};

export const apiRequest = async (url, requestOptions) => {
	let response = []
	try {
		response = await fetch(url, requestOptions)
			.then(response => response)
			.then(response => response.json())
	}
	catch (error) {
		console.log("No data found.",)
	}
	return response;
};

export const useQuery = () => {
	return new URLSearchParams(decodeURIComponent(useLocation().search));
};

export const getInstances = async () => {
	return mockInstances;
};
