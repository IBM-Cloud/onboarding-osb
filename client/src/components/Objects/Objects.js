import {
	DataTable, Table, TableBody, TableCell, TableContainer, TableHead, TableHeader, TableRow, TableToolbar, TableToolbarSearch, TableToolbarContent, TableToolbarMenu
} from "carbon-components-react";
import { Download16, Renew16 } from '@carbon/icons-react';
import './Objects.scss';
import { useEffect, useState } from "react";
import _ from "lodash";
import { getRowDataFromCatalog } from "../../util/app.utils";
import { AppConst } from "../../constants/app.constants";

function Objects(props) {
	const { catalog } = props;
	const [rowData, setRowData] = useState([]);
	const [headerData, setHeaderData] = useState([]);

	useEffect(() => {
		if (!_.isEmpty(catalog)) {
			setHeaderData(AppConst.tableHeaders.Objects);
			setRowData(getRowDataFromCatalog(catalog.services));
		}
	}, [catalog])

	return (
		<div className="bx--grid object-info">
			<div className="bx--row">
				<div className="bx--col-lg-16">
					<DataTable isSortable={true} rows={rowData} headers={headerData}>
						{({ rows, headers, getHeaderProps, getRowProps, getTableProps, onInputChange }) => (
							<TableContainer key="tableContainer" title={AppConst.tableTitles["Objects"]}>
								<TableToolbar>
									<TableToolbarSearch placeholder="Search..." onChange={onInputChange} />
									<TableToolbarContent>
										<TableToolbarMenu disabled children={[]} key="download" renderIcon={Download16} iconDescription={"download"} onClick={(e) => { console.log(e) }} />
										<TableToolbarMenu disabled children={[]} key="refresh" renderIcon={Renew16} iconDescription={"reload"} onClick={(e) => { console.log(e) }} />
									</TableToolbarContent>
								</TableToolbar>
								<Table {...getTableProps()}>
									<TableHead>
										<TableRow>
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
												<TableRow key={row.id} {...getRowProps({ row })}>
													{row.cells.map((cell) => (
														<TableCell key={cell.id}>{cell.value}</TableCell>
													))}
												</TableRow>
											</>
										))}
									</TableBody>
								</Table>
							</TableContainer>
						)}
					</DataTable>
				</div>
			</div>
		</div>
	);
}

Objects.displayName = 'Objects';

export default Objects;
