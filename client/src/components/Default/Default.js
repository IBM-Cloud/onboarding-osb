import { Button, CodeSnippet, Link, SkeletonText } from "carbon-components-react";
import { CheckmarkFilled16, Launch16, ArrowsHorizontal16 } from '@carbon/icons-react';
import './Default.scss';
import Objects from "../Objects/Objects";
import { useEffect, useState } from "react";
import { AppConst } from "../../constants/app.constants";
import Instances from "../Instances/Instances";
import { apiRequest } from "../../util/app.utils";
import _ from "lodash";
import { encode } from "base-64";

function Default() {
	const [selectTable, setSelectTable] = useState(true);
	const [catalog, setCatalog] = useState({});
	const [instructionsUrl, setInstructionUrl] = useState("#");
	const [documentationUrl, setDocumentationUrl] = useState("#");
	const [brokerMetadata, setBrokerMetadata] = useState({});
	const [isMeteringKeySet, setIsMeteringKeySet] = useState(false);

	useEffect(() => {
		if (_.isEmpty(catalog)) {
			getCatalog();
		}
	})

	const getCatalog = async () => {
		var headers = new Headers();
		headers.append("Content-Type", "application/json");
		headers.append("Accept", "application/json");
		
		var requestOptions = {
			method: 'GET',
			headers: headers,
			redirect: 'follow',
			mode: 'cors'
		};
		let response = await apiRequest(AppConst.apiUrls.getCatalog, requestOptions);
		if (!_.isEmpty(response)) {
			setCatalog(response);
			setInstructionUrl(response.services[0].metadata.instructionsUrl);
			setDocumentationUrl(response.services[0].metadata.documentationUrl);
		}
		let metadata = await apiRequest(AppConst.apiUrls.getMetadata, requestOptions);
		if (!_.isEmpty(metadata)) {
			setBrokerMetadata(metadata);
			setIsMeteringKeySet(metadata.IS_METERING_APIKEY_SET);
		}
		return response;
	}

	const toggleTable = () => {
		setSelectTable(!selectTable)
	}

	return (

		<div className="container">
			<div className="bx--grid">
				<div className="bx--row">
					<div className="bx--col-lg-12">
						<span className="h3">
							{!_.isEmpty(catalog) ? catalog.services[0].metadata.displayName : <SkeletonText width="15%" className="skeletopn-text" />}
						</span>
						<span><CheckmarkFilled16 aria-label="status" className="svg-left fill-green" /> Running</span>
					</div>
					<div className="bx--col-lg-4 link-blank">
						<Link className="" target="_blank" href="#">Manage on IBM Cloud<Launch16 aria-label="open link" className="svg-right fill-link" /></Link>
					</div>

				</div>
				<div className="bx--grid broker-info">
					<div className="bx--row sub-row">
						<div className="bx--col-lg-12">
							<span className="h5">Service broker</span>
						</div>
						<div className="bx--col-lg-4 link-left">
							<Link className="" target="_blank" href={brokerMetadata.PC_URL ? brokerMetadata.PC_URL : "#"}>Partner Center | Sell<Launch16 aria-label="open link" className="svg-right fill-link" /></Link>
						</div>
					</div>
					<div className="bx--row sub-row">
						<div className="bx--col-lg-12">
							<p>
								{!_.isEmpty(catalog) ? catalog.services[0].description : <SkeletonText width="15%" className="skeletopn-text" />}
								<Link className="inline-link" target="_blank" href={documentationUrl}>Learn more<Launch16 aria-label="open link" className="svg-right fill-link" /></Link>
							</p>
						</div>
						<div className="bx--col-lg-4 link-left">
							<Link className="" target="_blank" href={instructionsUrl}>How to use<Launch16 aria-label="open link" className="svg-right fill-link" /></Link>
						</div>
					</div>
					<div className="bx--row sub-row">
						<div className="bx--col-lg-16">
							<div className="bx--label">Build number</div>
							<CodeSnippet>{brokerMetadata.BUILD_NUMBER}</CodeSnippet>
						</div>
					</div>
					<div className="bx--row sub-row">
						<div className="bx--col-lg-16">
							<div className="bx--label">Broker URL</div>
							<CodeSnippet>{brokerMetadata.BROKER_URL ? brokerMetadata.BROKER_URL : "#"}</CodeSnippet>
						</div>
					</div>
				</div>

				{selectTable ? <Objects catalog={catalog}></Objects> : <Instances catalog={catalog} isMeteringKeySet={isMeteringKeySet}></Instances>}

				<Button className="tableToggle" onClick={toggleTable} renderIcon={ArrowsHorizontal16} iconDescription={"toggle table"} kind="tertiary">
					View {AppConst.tables[selectTable ? 1 : 0]}
				</Button>
			</div>
		</div >
	);
}

Default.displayName = 'Default';

export default Default;
