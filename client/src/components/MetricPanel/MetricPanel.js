import {
	HeaderPanel, Button, CodeSnippet, NumberInput, DataTable, TableContainer, Table, TableHead, TableRow, TableHeader, TableBody, TableCell, ModalWrapper, Tag, Loading, InlineLoading
} from "carbon-components-react";
import { Close16, Add16 } from '@carbon/icons-react';
import './MetricPanel.scss';
import { useEffect, useState } from "react";
import _ from "lodash";
import { AppConst } from "../../constants/app.constants";
import { apiRequest } from "../../util/app.utils";
import { encode } from "base-64";
import DisplayModal from "../DisplayModal/DisplayModal";

function MetricPanel(props) {
	const { closePanel, instance, metrics, plan, gcId, isMeteringKeySet } = props;

	const [metricQuantity, setMetricQuantity] = useState({});
	const [updateFlag, setUpdateFlag] = useState(false);
	const [meteringResponse, setMeteringResponse] = useState(false);
	const [showModal, setShowModal] = useState(false);
	const [isLoading, setIsLoading] = useState(false);

	useEffect(() => {
		if (_.isEmpty(metricQuantity)) {
			let costs = {};
			metrics.map((met => {
				costs[met.metric] = parseFloat(0.00)
				return true
			}));
			setMetricQuantity(costs);
			setUpdateFlag(!updateFlag);
		}
	}, [])

	const onNumberChange = (e) => {
		let quantity = metricQuantity;
		let metricId = e.imaginaryTarget.id;
		quantity[metricId] = e.imaginaryTarget.value;
		setMetricQuantity(quantity);
		setUpdateFlag(!updateFlag);
	}

	const getCostForQuantity = (metricId, quantity) => {
		let quantityTier = metrics.find(m => m.metric === metricId).price.prices[0].quantity_tier;
		let price = metrics.find(m => m.metric === metricId).price.prices[0].price;
		let cost = quantity * price;
		return cost;
	}

	const closeModal = () => {
		setShowModal(false);
	}

	const getMesuredUsage = () => {
		let measuredUsage = [];
		Object.keys(metricQuantity).map((metric) => {
			measuredUsage.push({
				"measure": metric,
				"quantity": metricQuantity[metric]
			})
			return true;
		})
		return measuredUsage;
	}

	const sendMetering = async () => {
		setIsLoading(true)
		let resourceId = instance.id;
		let region = instance.region;
		let planId = plan.id;
		let measuredUsage = getMesuredUsage();
		const requestBody = {
			"region": region,
			"resource_instance_id": resourceId,
			"plan_id": planId,
			"measured_usage": measuredUsage
		}

		var headers = new Headers();
		headers.append("Content-Type", "application/json");
		headers.append("Accept", "application/json");
		
		var requestOptions = {
			method: 'POST',
			headers: headers,
			redirect: 'follow',
			mode: 'cors',
			body: JSON.stringify(requestBody)
		};
		let response = await apiRequest(AppConst.apiUrls.sendMetering(gcId), requestOptions);
		setMeteringResponse(response);
		setIsLoading(false)
		setShowModal(true);
	}

	const getTotalCost = () => {
		let calculateTotal = 0;
		Object.keys(metricQuantity).map(metricId => {
			let cost = getCostForQuantity(metricId, metricQuantity[metricId]);
			calculateTotal += parseFloat(cost);
		})
		return calculateTotal;
	}

	const getCostPerUnit = (metricId) => metrics.find(m => m.metric === metricId).price.prices[0].price;

	return (
		<HeaderPanel aria-label="metric side panel" aria-labelledby="side panel" expanded>
			<div className="bx--grid">
				<div className="bx--row">
					<div className="bx--col-lg-14">
						<h3>Estimation and pricing</h3>
					</div>
					<div className="bx--col-lg-2">
						<Button kind="ghost" onClick={closePanel} className="panel-close-btn" renderIcon={Close16} iconDescription={"close panel"} />
					</div>
				</div>
				<div className="bx--row sub-row">
					<div className="bx--col-lg-16">
						<div className="bx--label">Pricing plan name</div>
						<CodeSnippet className="panel-ele-70">{plan.name}</CodeSnippet>
					</div>
				</div>
				<div className="bx--row sub-row">
					<div className="bx--col-lg-16">
						<div className="bx--label">Pricing plan programmatic name</div>
						<CodeSnippet className="panel-ele-70">{plan.name}</CodeSnippet>
					</div>
				</div>
				<div className="bx--row sub-row">
					<div className="bx--col-lg-16">
						<div className="bx--label">Location</div>
						<CodeSnippet className="panel-ele-70">{instance.region}</CodeSnippet>
					</div>
				</div>
				<div className="bx--row sub-row">
					<div className="bx--col-lg-16">
						<h5>Estimation</h5>
					</div>
				</div>
				{
					metrics.map((metric, index) => (
						<div key={index} className="bx--row sub-row">
							<div className="bx--col-lg-8">
								<div className="bx--label">{metric.metric}</div>
								<NumberInput onChange={onNumberChange} id={metric.metric} min={0}></NumberInput>
							</div>
						</div>
					)
					)
				}
				<div className="bx--row sub-row">
					<div className="bx--col-lg-16">
						<h5>Testing</h5>
					</div>
				</div>
				<div className="bx--row sub-row testing-row">
					<div className="bx--col-lg-16">
						Once you've entered values for any metrics you'd
						ike to test, you can fire corresponding usage events
						using our wonderful button.
					</div>
					<div className="bx--col-lg-16 sub-row">
						<DataTable rows={metrics} headers={AppConst.tableHeaders.TestMetric}>
							{({ rows, headers, getHeaderProps, getTableProps }) => (
								<TableContainer title="">
									<Table className="metric-table" {...getTableProps()}>
										<TableHead>
											<TableRow>
												{headers.map((header, index) => (
													<TableHeader key={index} {...getHeaderProps({ header })}>
														{header.header}
													</TableHeader>
												))}
											</TableRow>
										</TableHead>
										<TableBody className="metric-table-body">
											{rows.map((row, index) => (
												<TableRow key={row.id}>
													<TableCell style={{ backgroundColor: "#f4f4f4", paddingLeft: "1rem" }} key={row.cells[0].id}><strong>{metricQuantity[row.cells[0].value]}</strong> {row.cells[0].value}</TableCell>
													<TableCell style={{ backgroundColor: "#f4f4f4", paddingLeft: "1rem", textAlign: "right" }} key={row.cells[1].id}>{getCostPerUnit(row.cells[0].value)} USD</TableCell>
												</TableRow>
											))}
										</TableBody>
									</Table>
								</TableContainer>
							)}
						</DataTable>
					</div>
				</div>
				<div className="bx--row sub-row testing-row testing-row-bottom">
					<div className="bx--col-lg-16 sub-row">
						{isLoading
							?
							<InlineLoading className=""/>
							: <>
								<Button disabled={isMeteringKeySet ? getTotalCost() <= 0 : true} renderIcon={Add16} iconDescription={"send test metric"} onClick={sendMetering}>Send metering data</Button>
								<div>{!isMeteringKeySet && <Tag type="red">Can't send metering data. METERING_API_KEY is not set in broker.</Tag>}</div>
							</>
						}
					</div>
				</div>
			</div>
			{showModal && <DisplayModal response={meteringResponse} onCloseModal={closeModal}></DisplayModal>}
		</HeaderPanel>
	);
}

MetricPanel.displayName = 'MetricPanel';

export default MetricPanel;
