import {
	Button, CodeSnippet, ModalWrapper
} from "carbon-components-react";

import './DisplayModal.scss';
import { useEffect } from "react";
import _ from "lodash";

function DisplayModal(props) {
	const { response, onCloseModal } = props;

	useEffect(() => {
		if (!_.isEmpty(response)) {
			openModal()
		}
	}, [])

	const openModal = () => {
		document.getElementsByClassName("modal-trigger")[0].click()
	}

	const closeModal = () => {
		document.getElementsByClassName("bx--modal-close")[0].click()
		onCloseModal();
	}

	return (
		<div>
			<ModalWrapper
				buttonTriggerText="Modal"
				buttonTriggerClassName="modal-trigger"
				modalHeading="Metering Response"
				passiveModal={true}
				preventCloseOnClickOutside={true}
			>
				<div className="res-modal">
					{"Metering API returned status: " + response[0].status}
					<h6>Response</h6>
					<CodeSnippet type="multi">{JSON.stringify(response, null, 4)}</CodeSnippet>
					<Button kind="secondary" className="login-btn" onClick={closeModal}>Close</Button>
				</div>
			</ModalWrapper>
		</div>
	);
}

DisplayModal.displayName = 'DisplayModal';

export default DisplayModal;
