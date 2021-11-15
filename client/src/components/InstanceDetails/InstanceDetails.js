import './InstanceDetails.scss';
import { useEffect, useState } from "react";
import _ from "lodash";
import { useQuery } from "../../util/app.utils";
import { AppConst } from '../../constants/app.constants';

function InstanceDetails(props) {
	const query = useQuery();

	const [instanceType, setInstanceType] = useState("");
	const [instanceId, setInstanceId] = useState("");
	const [isDataPresent, setIsDataPresent] = useState(true);

	useEffect(() => {
		let type = query.get(AppConst.instanceDetailsPathParams.type);
		let id = query.get(AppConst.instanceDetailsPathParams.id);
		if (type && id) {
			setInstanceType(type);
			setInstanceId(id);
		}
		else {
			setIsDataPresent(false)
		}
	}, [])

	return (
		<div className="container">
			<div className="bx--grid bg-white">
				{!isDataPresent
					?
					<div className="bx--row">
						<div className="empty-instance-message">
							<h5>No Instance Details Found.</h5>
							<p>Check provisioned instances.</p>
						</div>
					</div>
					:
					<>
						<div className="bx--row">
							<div className="bx--col-lg-16">
								<h5>Deployment Details</h5>
							</div>

						</div>
						<div className="bx--row data-row mid-row">
							<div className="bx--col-lg-2">
								<strong>Type</strong>
							</div>
							<div className="bx--col-lg-14">
								{instanceType}
							</div>
						</div>
						<div className="bx--row data-row">
							<div className="bx--col-lg-2">
								<strong>ID</strong>
							</div>
							<div className="bx--col-lg-14">
								{instanceId}
							</div>
						</div>
					</>

				}
			</div>
		</div >
	);
}

InstanceDetails.displayName = 'InstanceDetails';

export default InstanceDetails;
