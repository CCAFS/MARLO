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

function manageSpinner(bool) {
	console.log("hide spinner");
	if (bool) {
		document.getElementById('spinner').style.display = "block";
	} else {
		document.getElementById('spinner').style.display = "none";
	}

}
function displayHideOurUnset(classs,display){
	document.querySelector(classs).style.display = display;
	return document.querySelector(classs);
}
function hideFilter() {
	document.getElementById("btnExport").style.display = "unset";
	if (document.querySelector(".dt-buttons")) {
		displayHideOurUnset(".dt-buttons","none");
		displayHideOurUnset(".dataTables_filter","none");
		displayHideOurUnset(".dataTables_info","none");
		displayHideOurUnset(".dataTables_length","none");
		displayHideOurUnset(".dataTables_paginate","none");
	}

}

function showFilter() {
	document.getElementById("btnExport").style.display = "none";
	if (document.querySelector(".dt-buttons")) {
		displayHideOurUnset(".dt-buttons","unset");
		displayHideOurUnset(".dataTables_filter","unset");
		displayHideOurUnset(".dataTables_info","unset");
		displayHideOurUnset(".dataTables_length","unset");
		displayHideOurUnset(".dataTables_paginate","unset");
	}

}

function updateDataTable(id) {
	console.log("segunddo");

if (!document.querySelector(".dt-buttons")) {
	$("#tblToExport").DataTable({        
        responsive: "true",
        dom: 'Bfrtilp',       
        buttons:[ 
			{
				extend:    'excelHtml5',
				text:      '<i class="fas fa-file-excel"></i> ',
				titleAttr: 'Exportar a Excel',
				className: 'btn btn-success'
			},
			{
				extend:    'pdfHtml5',
				text:      '<i class="fas fa-file-pdf"></i> ',
				titleAttr: 'Exportar a PDF',
				className: 'btn btn-danger'
			}
		]	        
    });     
}


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
			manageSpinner(true);
			hideFilter();
			cleanModal();
		},
		success: function (data) {
			manageSpinner(false);
			// ********************************************* */
			// print data
			// console.log(data);
			let nameColumns = ['Code', 'Name', 'Acronym',
				'CGIAR Entity Type']

			$.each(nameColumns, function (index, name) {
				$('#list-print-columns-name').append(
					'<th >' + name + '</th>')
			});

			$.each(data, function (index, item) {
				$('#list-print').append(
					'<tr>' + '<td >' + item['code'] + '</td>'
					+ '<td>' + item['name'] + '</td>'
					+ '<td>' + item['acronym'] + '</td>'
					+ '<td>' + '<strong>Code:</strong> '
					+ item['cgiarEntityTypeDTO'].code
					+ ' - <strong>Name:</strong> '
					+ item['cgiarEntityTypeDTO'].name
					+ '</td>' + '</tr>')
			});
			// updateDataTable("cgiar_entities");
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
			hideFilter();
			cleanModal();
			manageSpinner(true);
		},
		success: function (data) {
			// ********************************************* */
			// print data
			manageSpinner(false);
			console.log(data);
			let nameColumns = ['Code', 'Name']

			$.each(nameColumns, function (index, name) {
				console.log("primero1");
				$('#list-print-columns-name').append('<th >' + name + '</th>')
			});

			$.each(data, function (index, item) {
				console.log("primero2");
				$('#list-print').append(
					'<tr>' + '<td >' + item['code'] + '</td>' + '<td>'
					+ item['name'] + '</td>' + '</tr>')
			});

			// updateDataTable("cgiar_entity_types");
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
			hideFilter();
			cleanModal();
			manageSpinner(true);
		},
		success: function (data) {
			// ********************************************* */
			// print data
			manageSpinner(false);
			console.log(data);
			let nameColumns = ['Code', 'ISO Alpha2', 'Name', 'Region']

			$.each(nameColumns, function (index, name) {
				$('#list-print-columns-name').append('<th >' + name + '</th>')
			});

			$.each(data, function (index, item) {
				$('#list-print').append(
					'<tr>' + '<td >' + item['code'] + '</td>' + '<td>'
					+ item['isoAlpha2'] + '</td>' + '<td>'
					+ item['name'] + '</td>' + '<td>'
					+ '<strong>UN49Code:</strong> '
					+ item['regionDTO'].um49Code
					+ ' - <strong>Name:</strong> '
					+ item['regionDTO'].name + '</td>' + '</tr>')
			});
			// updateDataTable("countries");
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
			hideFilter();
			cleanModal();
			manageSpinner(true);
		},
		success: function (data) {
			// ********************************************* */
			// print data
			manageSpinner(false);
			let nameColumns = ['UN49Code', 'Name']

			$.each(nameColumns, function (index, name) {
				$('#list-print-columns-name').append('<th >' + name + '</th>')
			});

			$.each(data, function (index, item) {
				$('#list-print').append(
					'<tr>' + '<td >' + item['um49Code'] + '</td>' + '<td>'
					+ item['name'] + '</td>' + '</tr>')
			});
			// updateDataTable("un_regions");
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
				hideFilter();

				cleanModal();
				manageSpinner(true);
			},
			success: function (data) {
				// ********************************************* */
				// print data
				testInstitution(data);
				showFilter();
				manageSpinner(false);
				console.log(data);
				let nameColumns = ['Code','Acronym', 'Office Location',
					'Name', 'Website']

				$.each(nameColumns, function (index, name) {
					$('#list-print-columns-name').append(
						'<th >' + name + '</th>')
				});

				$
					.each(
						data,
						function (index, item) {
							$('#list-print')
								.append(
									'<tr>'
									+ '<td>'
									+ item['code']
									+ '</td>'
									+ '<td >'
									+ validateNull(item['acronym'])
									+ '</td>'

									//Office Location
									+ '<td>'
									// + '<p class="nomar"><strong>Code:</strong> '
									// + item['countryOfficeDTO']['0'].code
									// + '</p>'
									// + '<p class="nomar"><strong>isHeadquarter:</strong> '
									// + converYesOrNot(item['countryOfficeDTO']['0'].isHeadquarter)
									// + '</p>'
									// + '<p class="nomar"><strong>isoAlpha2:</strong> '
									// + item['countryOfficeDTO']['0'].isoAlpha2
									// + '</p>'
									// + '<p class="nomar"><strong>name:</strong> '
									// + item['countryOfficeDTO']['0'].name
									// + '</p>'
									+ '<p class="nomar"><strong>Headquarter: </strong> '
									+ getHeadquarter(item['countryOfficeDTO'])
									+ '</p>'
									// + '<p class="nomar"><strong>Other office locations:</strong> '
									+ get_other_office_locations(item['countryOfficeDTO'])
									// + '</p>'

									+ '</td>'
									+ '<td>'
									// END Office Location
									+ item['name']
									+ '</td>'
									+ `<td  data-toggle="tooltip" data-placement="top" title="${item['websiteLink']}"><a href="${item['websiteLink']}" target="_blank">website link</a></td>`
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