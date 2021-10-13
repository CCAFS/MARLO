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
	var isPublicationMQAP = true;

	if (id == "publicationMQAP") {
		isPublicationMQAP = false;
	} else {
		isPublicationMQAP = true;
	}

	console.log("segundo", isPublicationMQAP);

	// if (!document.querySelector(".dt-buttons")) {

	$("#" + id).DataTable({
		paging: isPublicationMQAP,
		searching: isPublicationMQAP,
		info: isPublicationMQAP,
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
			let nameColumns = ['UNSD code', 'Short Name','Full Name']

			$.each(data, function (index, item) {
				
				$('#list-print-sdgs').append(
					'<tr>' + '<td >' + item['usndCode'] + '</td>' + '<td>'
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
					+ (item['regionDTO']==null? '':'<strong>UN49Code:</strong> ')
					+ (item['regionDTO']==null? '':item['regionDTO'].um49Code
					+ ' - <strong>Name:</strong> ')
					+ (item['regionDTO']==null? '':item['regionDTO'].name) + '</td>' + '</tr>')
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

function all_initiatives() {
	$.ajax({
		url: config.endpoint + '/allInitiatives',
		type: "GET",
		beforeSend: function () {
			// hideFilter();
			cleanModal();
			manageSpinner(true,"all_initiatives");
			destroyTable("all_initiatives");
		},
		success: function (data) {
			// ********************************************* */
			// print data
			manageSpinner(false,"all_initiatives");
			console.log(data);
			let nameColumns = ['Code','Official Code', 'Name','Status','Action area id', 'Action area description','','Stage id', 'Stage Desciption' ]			

			$.each(data, function (index, item) {				
				$('#list-print-all_initiatives').append(
					'<tr>' + '<td >' + item['id'] + '</td>' 
					+ '<td >' + item['official_code'] + '</td>' 
					+ '<td >' + item['name'] + '</td>' 
					+ '<td >' + item['status'] + '</td>' 
					+ '<td >' + item['action_area_id'] + '</td>' 
					+ '<td>'+ item['action_area_description'] + '</td>'
					+ '<td>'+ item['active'] + '</td>'
					+ '<td>'+ item['stageId'] + '</td>'
					+ '<td>'+ item['description'] + '</td>' 
					+ '</tr>')
			});
setTimeout(() => {
	updateDataTable("all_initiatives");
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

function action_areas_outcomes() {
	$.ajax({
		url: config.endpoint + '/actionAreaOutcomeIndicators',
		type: "GET",
		beforeSend: function () {
			// hideFilter();
			cleanModal();
			manageSpinner(true,"actionAreaOutcomeIndicators");
			destroyTable("actionAreaOutcomeIndicators");
		},
		success: function (data) {
			// ********************************************* */
			// print data
			manageSpinner(false,"actionAreaOutcomeIndicators");
			console.log(data);
			let nameColumns = ['Action Area ID', 'Action Area Name','Outcome ID','Outcome Statement','Outcome Indicator ID','Outcome Indicator Statement']

			$.each(data, function (index, item) {				
				$('#list-print-actionAreaOutcomeIndicators').append(
					'<tr>' + '<td >' + item['actionAreaId'] + '</td>' + '<td>'
					+ item['actionAreaName'] + '</td>'+ '<td>'
					+ item['outcomeId'] + '</td>'+ '<td>'
					+ item['outcomeStatement'] + '</td>'+ '<td>'
					+ item['outcomeIndicatorId'] + '</td>'+ '<td>'
					+ item['outcomeIndicatorStatement'] + '</td>' + '</tr>')
			});
setTimeout(() => {
	updateDataTable("actionAreaOutcomeIndicators");
}, 1000);
			
			// end print Data
			// ********************************************** */
		},
		error: function (e) {
			console.log(e);
		}
	});
}

function work_packages() {
	$.ajax({
		url: config.endpoint + '/workpackages',
		type: "GET",
		beforeSend: function () {
			// hideFilter();
			cleanModal();
			manageSpinner(true,"workpackages");
			destroyTable("workpackages");
		},
		success: function (data) {
			// ********************************************* */
			// print data
			manageSpinner(false,"workpackages");
			console.log(data);
			let nameColumns = ['Action Area ID', 'Action Area Name','Outcome ID','Outcome Statement','Outcome Indicator ID','Outcome Indicator Statement']			

			$.each(data, function (index, item) {				
				$('#list-print-workpackages').append(
					'<tr>' + '<td >' + item['id'] + '</td>' + '<td>'
					+ item['acronym'] + '</td>'+ '<td>'
					+ item['name'] + '</td>'+ '<td>'
					+ item['initiativeId'] + '</td>'+ '<td>'
					+ item['stageId'] + '</td>'+ '<td>'
					+ (item['results']==null? '':item['results'] )+ '</td>'+ '<td>'
					+ (item['pathway_content']==null? '':item['pathway_content'] ) + '</td>' + '</tr>')
			});
setTimeout(() => {
	updateDataTable("workpackages");
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
					+ item['impactAreaName'] + '</td>' +'<td>'
					+ item['targetYear'] + '</td>' + '<td>'
					+ item['targetUnit'] + '</td>' + '<td>'
					+ item['value'] + '</td>' + '</tr>')
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

function mqapClean(){
	$('#doiInput').val('');
	cleanModal();
	destroyTable("publicationMQAP");
}

function mqap() {
	let text=$('#doiInput').val();
	console.log(text);
	$.ajax({
		 
		url: config.endpoint + '/publicationsWOS?url='+text,
		type: "GET",
		beforeSend: function () {
			// hideFilter();
			cleanModal();
			manageSpinner(true,"publicationMQAP");
			destroyTable("publicationMQAP");
		},
		success: function (data) {
			// ********************************************* */
			// print data
			manageSpinner(false,"publicationMQAP");
			let nameColumns = ['Field', 'Value']

			// $.each(nameColumns, function (index, name) {
			// $('#list-print-columns-name').append('<th >' + name + '</th>')
			// });
			console.log(data.title);
			/*
			 * $.each(data, function (key, item) {
			 * 
			 */
			let mapped='';
			let notmaped='';
			 $.each(data.organizations, function (index, item) {
				 if(item['confidant']>=80){
					 mapped +='\n'+item['fullName'];
				 }else{
					 notmaped +='\n'+item['fullName'];
				 }
			 });
			 let authorslist='';
			 $.each(data.authors, function (index, item) {
				 authorslist+='\n'+item['full_name']
			 });
			 
			  $('#print-publicationMQAP').append(` 
				<tr><td>Publication type</td><td>${data.publicationType}</td></tr>
				<tr><td>Title</td><td>${data.title}</td></tr>
				<tr><td>Volume</td><td>${validateNull(data.volume)}</td></tr>
				<tr><td>Pages</td><td>${validateNull(data.pages)}</td></tr>
				<tr><td>Is ISI?</td><td>${data.is_isi}</td></tr>
				<tr><td>DOI</td><td>${data.doi}</td></tr>
				<tr><td>Is open access?</td><td>${data.is_oa}</td></tr>
				<tr><td>Open access link</td><td>${data.oa_link}</td></tr>
				<tr><td>Source</td><td>${data.source}</td></tr>
				<tr><td>Publication year</td><td>${data.publicationYear}</td></tr>
				<tr><td>Authors</td><td>${authorslist}</td></tr>
				<tr><td>Institutions Mapped</td><td>${mapped}</td></tr>
				<tr><td>Institutions Not Mapped</td><td>${notmaped}</td></tr>
				`)							 	
			/* }); */
			updateDataTable("publicationMQAP");
			// end print Data
			// ********************************************** */
		},
		error: function (e) {
			console.log(e);
		}
	});
}

function glossary(){
	$.ajax({
		url: config.endpoint + '/glossary',
		type: "GET",
		beforeSend: function () {
			// hideFilter();
			cleanModal();
			manageSpinner(true,"glossary");
			destroyTable("glossary");
		},
		success: function (data) {
			// ********************************************* */
			// print data
			manageSpinner(false,"glossary");
			let nameColumns = ['Term', 'Definition']

		
			$.each(data, function (index, item) {
				$('#list-print-glossary').append(
					'<tr>' + '<td>'+ item['term'] + '</td>'
					+ '<td>'+ item['definition'] + '</td>'
					+ '</tr>')
			});
			updateDataTable("glossary");
			// end print Data
			// ********************************************** */
		},
		error: function (e) {
			console.log(e);
		}
	});
}
function CGIARRegions(){
	$.ajax({
		url: config.endpoint + '/OneCGIARRegions',
		type: "GET",
		beforeSend: function () {
			// hideFilter();
			cleanModal();
			manageSpinner(true,"CGIAR_regions");
			destroyTable("CGIAR_regions");
		},
		success: function (data) {
			// ********************************************* */
			// print data
			manageSpinner(false,"CGIAR_regions");
			let nameColumns = ['id', 'Name','Acronym','Countries']

			// $.each(nameColumns, function (index, name) {
			// $('#list-print-columns-name').append('<th >' + name + '</th>')
			// });

			$.each(data, function (index, item) {
				$('#list-print-CGIAR-regions').append(
					'<tr>' + '<td >' + item['id'] + '</td>' 
					+ '<td>'+ item['name'] + '</td>'
					+ '<td>'+ item['acronym'] + '</td>'
					// + '<td>'+ item['regionType'].name + '</td>'
					+ '<td>'+ getCountries(item['countries']) + '</td>' 
					+ '</tr>')
			});
			updateDataTable("CGIAR_regions");
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

function getCountries(countryDTO) {
	let resultado = "";
	$.each(countryDTO, function (index, item) {
		if(index==0){
			resultado += `<span data-toggle="tooltip" data-placement="top" class="pointer" title="${item.name}">${item.isoAlpha2}</span>`;	
		}else{
			resultado += `<span data-toggle="tooltip" data-placement="top" class="pointer" title="${item.name}">,${item.isoAlpha2}</span>`;	
		}
				
	});
	return resultado;
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

function acronyms() {
	$.ajax({
		url: config.endpoint + '/acronyms',
		type: "GET",
		beforeSend: function () {
			// hideFilter();
			cleanModal();
			manageSpinner(true,"acronyms");
			destroyTable("acronyms");
		},
		success: function (data) {
			// ********************************************* */
			// print data
			manageSpinner(false,"acronyms");
			console.log(data);
			let nameColumns = ['Code', 'Acronym','Description']			

			$.each(data, function (index, item) {				
				$('#list-print-acronyms').append(
					'<tr>' + '<td >' + item['code'] + '</td>' + '<td>'
					+ item['acronym'] + '</td>'+ '<td>'
					+ item['description'] + '</td>' + '</tr>')
			});
setTimeout(() => {
	updateDataTable("acronyms");
}, 1000);
			
			// end print Data
			// ********************************************** */
		},
		error: function (e) {
			console.log(e);
		}
	});
}