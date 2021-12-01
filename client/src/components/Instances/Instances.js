import {
	DataTable, Table, TableBody, TableCell, TableContainer, TableHead, TableHeader, TableRow, TableExpandRow, TableExpandedRow, TableExpandHeader, TableToolbar, TableToolbarSearch, TableToolbarContent, TableToolbarMenu, Button, ModalWrapper
} from "carbon-components-react";
import { Download16, Renew16 } from '@carbon/icons-react';
import './Instances.scss';
import { useEffect, useState } from "react";

import { AppConst } from "../../constants/app.constants";
import MetricPanel from "../MetricPanel/MetricPanel";
import { apiRequest, getInstanceRows } from "../../util/app.utils";
import _, { isEmpty } from "lodash";
import { encode } from "base-64";

function Instances(props) {
	const { catalog, isMeteringKeySet } = props;
	const [rowData, setRowData] = useState([]);
	const [headerData, setHeaderData] = useState([]);
	const [togglePanel, setTogglePanel] = useState(false);
	const [allMetrics, setAllMetrics] = useState([]);
	const [selectedInstance, setSelectedInstance] = useState("");
	const [selectedPlan, setSelectedPlan] = useState("");
	const [selectedMetrics, setSelectedMetrics] = useState([]);

	useEffect(() => {
		setHeaderData(AppConst.tableHeaders.Instances);
		if (!_.isEmpty(catalog) && _.isEmpty(rowData)) {
			getInstanceData();
		}
	}, [catalog])

	const getInstanceData = async () => {
		var headers = new Headers();
		headers.append("Content-Type", "application/json");
		headers.append("Accept", "application/json");
		
		var requestOptions = {
			method: 'GET',
			headers: headers,
			redirect: 'follow',
			mode: 'cors'
		};
		let instanceData = await apiRequest(AppConst.apiUrls.getInstances, requestOptions);
		let instanceRows = getInstanceRows(instanceData);
		setRowData(instanceRows);
		getMetric();
	}

	const onTestClick = (e) => {
		setSelectedInstance(rowData[e.target.id]);
		let planId = rowData.find(r => r.instanceId === e.target.name).planId;
		setSelectedMetrics(getMetricsForPlan(planId));
		setSelectedPlan(catalog.services[0].plans.find(p => p.id === planId));
		setTogglePanel(!togglePanel);
	}

	const onCloseClick = () => {
		setTogglePanel(!togglePanel);
	}

	const getMetric = async () => {
		let metricsData = [];
		catalog.services[0].plans.map((plan) => {
			if (plan.metadata.costs.metrics) {
				metricsData.push({
					planId: plan.id,
					metrics: plan.metadata.costs.metrics.map((metric) => {
						if (metric && metric.amounts) {
							let amount = metric.amounts.find((amt) => amt.country === "USA");
							return ({
								id: metric.metric_id,
								metric: metric.charge_unit_name,
								metricId: metric.metric_id,
								type: metric.tier_model,
								price: amount
							})
						}
						else {
							return 1;
						}
					})
				})
			}
			else {
				return 1;
			}
			return true;
		})
		setAllMetrics(metricsData);
	}

	const getMetricsForPlan = (planId) => {
		let selectedMetric = allMetrics.find((met) => met.planId === planId)
		if (selectedMetric && selectedMetric.metrics) {
			return selectedMetric.metrics;
		}
		else return [];
	}

	return (
		<>

			<div className="bx--grid instances-info">
				<div className="bx--row">
					<div className="bx--col-lg-16">
						<>
							<DataTable rows={rowData} headers={headerData}>
								{({ rows, headers, getHeaderProps, getRowProps, getTableProps, onInputChange }) => (
									<TableContainer key="tableContainer" title={AppConst.tableTitles["Instances"]}>
										<TableToolbar>
											<TableToolbarSearch placeholder="Search..." onChange={onInputChange} />
											<TableToolbarContent>
												<TableToolbarMenu disabled children={[]} key="download" renderIcon={Download16} iconDescription={"Download"} onClick={(e) => { console.log(e) }} />
												<TableToolbarMenu disabled children={[]} key="refresh" renderIcon={Renew16} iconDescription={"Reload"} onClick={(e) => { console.log(e) }} />
											</TableToolbarContent>
										</TableToolbar>
										{
											isEmpty(rowData)
												?
												<div className="empty-message">
													<h5>No Instances Found.</h5>
													<p>Provision instances to see this list.</p>
												</div>
												:
												<Table {...getTableProps()}>
													<TableHead>
														<TableRow>
															<TableExpandHeader />
															{headers.map((header, index) => (
																<TableHeader key={index} {...getHeaderProps({ header })}>
																	{header.header}
																</TableHeader>
															))}
														</TableRow>
													</TableHead>
													<TableBody>
														{rows.map((row, index) => (
															<>
																<TableExpandRow key={row.id} {...getRowProps({ row })}>
																	{row.cells.map((cell) => (
																		<TableCell key={cell.id}>{cell.value}</TableCell>
																	))}
																</TableExpandRow>
																{row.isExpanded && (
																	<TableExpandedRow colSpan={headers.length + 1}>
																		{!isEmpty(getMetricsForPlan(rowData[index].planId))
																			?
																			<>
																				<div>
																					<div className="metric-desc">Here are the metrics for this pricing plan.You can even fire events from them to see if things
																						are hooked up right.
																					</div>
																					<div className="align-right">
																						<Button id={index} name={rowData[index].instanceId} onClick={onTestClick} className="test-metric-btn">Test metric reporting</Button>
																					</div>
																				</div>
																				<div>
																					<DataTable rows={getMetricsForPlan(rowData[index].planId)} headers={AppConst.tableHeaders.Metric}>
																						{({ rows, headers, getHeaderProps, getTableProps }) => (
																							<TableContainer title="">
																								<Table className="metric-table" {...getTableProps()}>
																									<TableHead>
																										<TableRow>
																											{headers.map((header) => (
																												<TableHeader {...getHeaderProps({ header })}>
																													{header.header}
																												</TableHeader>
																											))}
																										</TableRow>
																									</TableHead>
																									<TableBody className="metric-table-body">
																										{rows.map((row) => (
																											<TableRow key={row.id}>
																												{row.cells.map((cell) => (
																													<TableCell style={{ paddingLeft: "1rem" }} key={cell.id}>{cell.value}</TableCell>
																												))}
																											</TableRow>
																										))}
																									</TableBody>
																								</Table>
																							</TableContainer>
																						)}
																					</DataTable>
																				</div>
																			</>
																			:
																			<div className="empty-message">
																				<h5>No Metrics Found.</h5>
																				<p>Check plan details.</p>
																			</div>
																		}
																	</TableExpandedRow>
																)
																}
															</>
														))}
													</TableBody>
												</Table>
										}
									</TableContainer>
								)}
							</DataTable>
						</>
					</div>
				</div>
			</div>
			{togglePanel && <MetricPanel closePanel={onCloseClick} instance={selectedInstance} metrics={selectedMetrics} plan={selectedPlan} gcId={catalog.services[0].id} isMeteringKeySet={isMeteringKeySet} ></MetricPanel>}
		</>
	);
}

Instances.displayName = 'Instances';

export default Instances;
