function truncate(str) {
	let n = 25;
	return (str.length > n) ? str.substr(0, n - 1) + '...' : str;
};

function converYesOrNot(bool) {
	if (bool == "true") {
		return 'Yes'
	} else {
		return 'No'
	}
}

function validateNull(data) {
	if (data == null) {
		return ''
	} else {
		return data
	}
}

function manageSpinner(bool,id) {
	console.log("hide spinner");
	if (bool) {
		document.getElementById('spinner_'+id).style.display = "block";
	} else {
		document.getElementById('spinner_'+id).style.display = "none";
	}

}


function destroyTable(id) {
	if ($.fn.DataTable.isDataTable("#" + id)) {
		$("#" + id).DataTable().clear().destroy();
	}
}

function updateDataTable(id) {

	console.log("segunddo");

	// if (!document.querySelector(".dt-buttons")) {

	$("#" + id).DataTable({
		responsive: "true",
		dom: 'Bfrtilp',
		destroy: true,
		buttons: [
			{
				extend: 'excelHtml5',
				text: '<i class="fas fa-file-excel"></i> ',
				titleAttr: 'Exportar a Excel',
				className: 'btn btn-success',
				title: id
			},
			{
				extend: 'pdfHtml5',
				text: '<i class="fas fa-file-pdf"></i> ',
				titleAttr: 'Exportar a PDF',
				className: 'btn btn-danger',
				title: id
			}
		]
	});

	console.log(id + "_wrapper");
	$(".dataTables_wrapper").each(function () {
		console.log("Id es");
		console.log(this.id);

	});
	console.log("update");

}


function cleanModal() {
	$("#list-print-columns-name").html("");
	$("#list-print").html("");
	document.querySelector(".modal-body").style.display = "none";
	document.querySelector(".modal-body").style.display = "unset";
	console.log("clean");
}

function cgiar_entities() {

	$.ajax({
		url: config.endpoint + '/cgiar-entities',
		type: "GET",
		beforeSend: function () {
			manageSpinner(true,"cgiar_entities");
			// hideFilter();
			cleanModal();
			destroyTable("cgiar_entities");
		},
		success: function (data) {
			manageSpinner(false,"cgiar_entities");
			// ********************************************* */
			// print data
			// console.log(data);
			let nameColumns = ['Code', 'Name', 'Acronym',
				'CGIAR Entity Type']

			// $.each(nameColumns, function (index, name) {
			// $('#list-print-columns-name').append(
			// '<th >' + name + '</th>')
			// });

			$.each(data, function (index, item) {
				$('#list-print-cgiar_entities').append(
					'<tr>' + '<td >' + item['code'] + '</td>'
					+ '<td>' + item['name'] + '</td>'
					+ '<td>' + item['acronym'] + '</td>'
					+ '<td>' + '<strong>Code:</strong> '
					+ item['cgiarEntityTypeDTO'].code
					+ ' - <strong>Name:</strong> '
					+ item['cgiarEntityTypeDTO'].name
					+ '</td>' + '</tr>')
			});
			updateDataTable("cgiar_entities");

			// end print Data
			// ********************************************** */
		},
		error: function (e) {
			console.log(e);
		}
	});
}

function sdg(){
	$.ajax({
		url: config.endpoint + '/allSDG',
		type: "GET",
		beforeSend: function () {
			// hideFilter();
			cleanModal();
			manageSpinner(true,"sdgs");
			destroyTable("sdgs");
		},
		success: function (data) {
			// ********************************************* */
			// print data
			manageSpinner(false,"sdgs");
			console.log(data);
			let nameColumns = ['SMO code', 'Short Name','Full Name']

			$.each(data, function (index, item) {
				
				$('#list-print-sdgs').append(
					'<tr>' + '<td >' + item['smoCode'] + '</td>' + '<td>'
					+ item['shortName'] + '</td>' + '<td>'
					+ item['fullName'] + '</td>' + '</tr>')
			});
setTimeout(() => {
	updateDataTable("sdgs");
}, 1000);
			
			// end print Data
			// ********************************************** */
		},
		error: function (e) {
			console.log(e);
		}
	});
}

function sdgtarget(){
	$.ajax({
		url: config.endpoint + '/allSDGTargets',
		type: "GET",
		beforeSend: function () {
			cleanModal();
			manageSpinner(true,"sdgTargets");
			destroyTable("sdgTargets");
		},
		success: function (data) {
			// ********************************************* */
			// print data
			manageSpinner(false,"sdgTargets");
			console.log(data);
			let nameColumns = ['ID', 'SDG Target Code','SDG Target','SDG Code', 'SDG Name']

			$.each(data, function (index, item) {
				
				$('#list-print-sdgTargets').append(
					'<tr>' + '<td >' + item['id'] + '</td>' + '<td>'
					+ item['sdgTargetCode'] + '</td>' + '<td>'
					+ item['sdgTarget'] + '</td>' + '<td>'
					+ item['sdg'].smoCode + '</td>' +'<td>'
					+ item['sdg'].shortName + '</td>' +'</tr>')
			});
setTimeout(() => {
	updateDataTable("sdgTargets");
}, 1000);
			
			// end print Data
			// ********************************************** */
		},
		error: function (e) {
			console.log(e);
		}
	});
}

function sdgIndicators(){
	$.ajax({
		url: config.endpoint + '/allSDGIndicators',
		type: "GET",
		beforeSend: function () {
			cleanModal();
			manageSpinner(true,"sdgIndicators");
			destroyTable("sdgIndicators");
		},
		success: function (data) {
			// ********************************************* */
			// print data
			manageSpinner(false,"sdgIndicators");
			console.log(data);
			let nameColumns = ['ID', 'UNSD Indicator Code','SDG Indicator Code SDG Indicator Name','SDG Target Code', 'SDG Target Name']

			$.each(data, function (index, item) {
				
				$('#list-print-sdgIndicators').append(
					'<tr>' + '<td >' + item['id'] + '</td>' + '<td>'
					+ item['unsdIndicatorCode'] + '</td>' + '<td>'
					+ item['indicatorCode'] + '</td>' + '<td>'
					+ item['indicatorName'] + '</td>' + '<td>'
					+ item['sdgTarget'].sdgTargetCode + '</td>' +'<td>'
					+ item['sdgTarget'].sdgTarget + '</td>' +'</tr>')
			});
setTimeout(() => {
	updateDataTable("sdgIndicators");
}, 1000);
			
			// end print Data
			// ********************************************** */
		},
		error: function (e) {
			console.log(e);
		}
	});
}

function cgiar_entity_types() {
	$.ajax({
		url: config.endpoint + '/cgiar-entity-types',
		type: "GET",
		beforeSend: function () {
			// hideFilter();
			cleanModal();
			manageSpinner(true,"cgiar_entity_types");
			destroyTable("cgiar_entity_types");
		},
		success: function (data) {
			// ********************************************* */
			// print data
			manageSpinner(false,"cgiar_entity_types");
			console.log(data);
			let nameColumns = ['Code', 'Name']

			// $.each(nameColumns, function (index, name) {
			// console.log("primero1");
			// $('#list-print-columns-name').append('<th >' + name + '</th>')
			// });

			$.each(data, function (index, item) {
				console.log("primero2");
				$('#list-print-cgiar_entity_types').append(
					'<tr>' + '<td >' + item['code'] + '</td>' + '<td>'
					+ item['name'] + '</td>' + '</tr>')
			});
setTimeout(() => {
	updateDataTable("cgiar_entity_types");
}, 1000);
			
			// end print Data
			// ********************************************** */
		},
		error: function (e) {
			console.log(e);
		}
	});
}

function countries() {
	$.ajax({
		url: config.endpoint + '/countries',
		type: "GET",
		beforeSend: function () {
			// hideFilter();
			cleanModal();
			manageSpinner(true,"countries");
			destroyTable("countries");
		},
		success: function (data) {
			// ********************************************* */
			// print data
			manageSpinner(false,"countries");
			console.log(data);
			let nameColumns = ['Code', 'ISO Alpha2', 'Name', 'Region']

			// $.each(nameColumns, function (index, name) {
			// $('#list-print-columns-name').append('<th >' + name + '</th>')
			// });

			$.each(data, function (index, item) {
				$('#list-print-countries').append(
					'<tr>' + '<td >' + item['code'] + '</td>' + '<td>'
					+ item['isoAlpha2'] + '</td>' + '<td>'
					+ item['name'] + '</td>' + '<td>'
					+ '<strong>UN49Code:</strong> '
					+ item['regionDTO'].um49Code
					+ ' - <strong>Name:</strong> '
					+ item['regionDTO'].name + '</td>' + '</tr>')
			});
			updateDataTable("countries");
			// end print Data
			// ********************************************** */
		},
		error: function (e) {
			console.log(e);
		}
	});
}

function action_areas() {
	$.ajax({
		url: config.endpoint + '/action-areas',
		type: "GET",
		beforeSend: function () {
			// hideFilter();
			cleanModal();
			manageSpinner(true,"action_areas");
			destroyTable("action_areas");
		},
		success: function (data) {
			// ********************************************* */
			// print data
			manageSpinner(false,"action_areas");
			console.log(data);
			let nameColumns = ['Code', 'Name','Description']			

			$.each(data, function (index, item) {				
				$('#list-print-action_areas').append(
					'<tr>' + '<td >' + item['id'] + '</td>' + '<td>'
					+ item['name'] + '</td>'+ '<td>'
					+ item['description'] + '</td>' + '</tr>')
			});
setTimeout(() => {
	updateDataTable("action_areas");
}, 1000);
			
			// end print Data
			// ********************************************** */
		},
		error: function (e) {
			console.log(e);
		}
	});
}

function impact_areas() {
	$.ajax({
		url: config.endpoint + '/impact-areas',
		type: "GET",
		beforeSend: function () {
			// hideFilter();
			cleanModal();
			manageSpinner(true,"impact_areas");
			destroyTable("impact_areas");
		},
		success: function (data) {
			// ********************************************* */
			// print data
			manageSpinner(false,"impact_areas");
			console.log(data);
			let nameColumns = ['Code', 'Name','Description']			

			$.each(data, function (index, item) {				
				$('#list-print-impact_areas').append(
					'<tr>' + '<td >' + item['id'] + '</td>' + '<td>'
					+ item['name'] + '</td>'+ '<td>'
					+ item['description'] + '</td>' + '</tr>')
			});
setTimeout(() => {
	updateDataTable("impact_areas");
}, 1000);
			
			// end print Data
			// ********************************************** */
		},
		error: function (e) {
			console.log(e);
		}
	});
}

function impact_areas_indicators() {
	$.ajax({
		url: config.endpoint + '/impact-areas-indicators',
		type: "GET",
		beforeSend: function () {
			// hideFilter();
			cleanModal();
			manageSpinner(true,"impact_areas_indicators");
			destroyTable("impact_areas_indicators");
		},
		success: function (data) {
			// ********************************************* */
			// print data
			manageSpinner(false,"impact_areas_indicators");
			console.log(data);
			let nameColumns = ['Code', 'Name','Description']			

			$.each(data, function (index, item) {				
				$('#list-print-impact_areas_indicators').append(
					'<tr>' + '<td >' + item['indicatorId'] + '</td>' + '<td>'
					+ item['indicatorStatement'] + '</td>'+ '<td>'
					+ item['impactAreaId'] + '</td>' +'<td>'
					+ item['impactAreaName'] + '</td>' + '</tr>')
			});
setTimeout(() => {
	updateDataTable("impact_areas_indicators");
}, 1000);
			
			// end print Data
			// ********************************************** */
		},
		error: function (e) {
			console.log(e);
		}
	});
}

function un_regions() {
	$.ajax({
		url: config.endpoint + '/un-regions',
		type: "GET",
		beforeSend: function () {
			// hideFilter();
			cleanModal();
			manageSpinner(true,"un_regions");
			destroyTable("un_regions");
		},
		success: function (data) {
			// ********************************************* */
			// print data
			manageSpinner(false,"un_regions");
			let nameColumns = ['UN49Code', 'Name']

			// $.each(nameColumns, function (index, name) {
			// $('#list-print-columns-name').append('<th >' + name + '</th>')
			// });

			$.each(data, function (index, item) {
				$('#list-print-un_regions').append(
					'<tr>' + '<td >' + item['um49Code'] + '</td>' + '<td>'
					+ item['name'] + '</td>' + '</tr>')
			});
			updateDataTable("un_regions");
			// end print Data
			// ********************************************** */
		},
		error: function (e) {
			console.log(e);
		}
	});
}

function institutions() {
	$
		.ajax({
			url: config.endpoint + '/institutions',
			type: "GET",
			beforeSend: function () {
				// hideFilter();

				cleanModal();
				manageSpinner(true,"institutions");
				destroyTable("institutions");
			},
			success: function (data) {
				// ********************************************* */
				// print data
				// testInstitution(data);
				// showFilter();
				manageSpinner(false,"institutions");
				console.log(data);
				let nameColumns = ['Code', 'Acronym','Institution Type', 'Office Location',
					'Name', 'Website']
				$
					.each(
						data,
						function (index, item) {
							$('#list-print-institutions')
								.append(
									'<tr>'
									+ '<td>'
									+ item['code']
									+ '</td>'
									+ '<td >'
									+ validateNull(item['acronym'])
									+ '</td>'
									// Institution type
									+ '<td>'
									+  item['institutionType'].name
									+ '</td>'
									// Office Location
									+ '<td>'									
									+ '<p class="nomar"><strong>Headquarter: </strong> '
									+ getHeadquarter(item['countryOfficeDTO'])
									+ '</p>'
									// + '<p class="nomar"><strong>Other office
									// locations:</strong> '
									+ get_other_office_locations(item['countryOfficeDTO'])
									// + '</p>'
									+ '</td>'									
									+ '<td>'
									// END Office Location
									+ item['name']
									+ '</td>'									
									+ `<td class="link-Web"  data-toggle="tooltip" data-placement="top" title="${item['websiteLink']}"><a  href="${item['websiteLink']}" target="_blank">${item['websiteLink']}</a></td>`
									+ '</tr>')
						});
				updateDataTable("institutions");
				// end print Data
				// ********************************************** */
			},
			error: function (e) {
				console.log(e);
			}
		});
}

function getHeadquarter(countryOfficeDTO) {
	let resultado = "";
	$.each(countryOfficeDTO, function (index, item) {
		if (item.isHeadquarter == "true") {
			resultado = `<span data-toggle="tooltip" data-placement="top" class="pointer" title="${item.name}">${item.isoAlpha2}</span>`;
		}
	});
	return resultado;
}

function get_other_office_locations(countryOfficeDTO) {
	let resultado = '<p class="nomar"><strong>Other office locations:</strong>';
	$.each(countryOfficeDTO, function (index, item) {
		if (item.isHeadquarter == "false") {
			if (resultado == "") {
				resultado += `<span data-toggle="tooltip" data-placement="top" class="pointer" title="${item.name}">${item.isoAlpha2}</span>`;
			} else {
				resultado += `, <span data-toggle="tooltip" data-placement="top" class="pointer"  title="${item.name}">${item.isoAlpha2}</span>`;
			}
		}
	});
	resultado += '</p>';
	if (resultado == '<p class="nomar"><strong>Other office locations:</strong></p>') {
		resultado = ""
	}
	return resultado;
}

function testInstitution(params) {
	$.each(params, function (index, item) {
		if (item.countryOfficeDTO.length > 1) {
			console.log("test this: " + item.code + " - size: " + item.countryOfficeDTO.length);
		}

	});
}