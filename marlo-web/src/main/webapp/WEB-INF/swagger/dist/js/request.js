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

function hideFilter() {
	if (document.getElementById("institutions_length")) {
		document.getElementById("institutions_length").style.display = "none";
		document.getElementById("institutions_filter").style.display = "none";
		document.getElementById("institutions_info").style.display = "none";
		document.getElementById("institutions_paginate").style.display = "none";

	}

}

function showFilter() {
	if (document.getElementById("institutions_length")) {
		document.getElementById("institutions_length").style.display = "unset";
		document.getElementById("institutions_filter").style.display = "unset";
		document.getElementById("institutions_info").style.display = "unset";
		document.getElementById("institutions_paginate").style.display = "unset";
		$('#example').dataTable({
			"pageLength" : 50
		});
	}

}

// console.log(truncate("http://web.maga.gob.gt/",24));
// console.log(truncate("http://kathmandu.im/ministry-of-agriculture-and-cooperative/",24));

// function destroyTable(){
// table = undefined;
// }
function updateDataTable(id) {
	console.log("segunddo");

	$(".dataTable").attr("id", id)

	$('#' + id).DataTable();

}
function cleanModal() {

	$("#list-print-columns-name").html("");
	$("#list-print").html("");
	document.querySelector(".modal-body").style.display = "none";
	document.querySelector(".modal-body").style.display = "unset";
	console.log("clean");

	// if(table){
	// table
	// .rows()
	// .remove()
	// .draw();
	// }

}

function cgiar_entities() {

	$
			.ajax({
				url : config.endpoint + '/cgiar-entities',
				type : "GET",
				beforeSend : function() {
					hideFilter();
					cleanModal();
				},
				success : function(data) {
					// ********************************************* */
					// print data
					// console.log(data);
					let nameColumns = [ 'Code', 'Name', 'Acronym',
							'CGIAR Entity Type' ]

					$.each(nameColumns, function(index, name) {
						$('#list-print-columns-name').append(
								'<th >' + name + '</th>')
					});

					$.each(data, function(index, item) {
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
				error : function(e) {
					console.log(e);
				}
			});
}

function cgiar_entity_types() {
	$.ajax({
		url : config.endpoint + '/cgiar-entity-types',
		type : "GET",
		beforeSend : function() {
			hideFilter();
			cleanModal();
		},
		success : function(data) {
			// ********************************************* */
			// print data
			console.log(data);
			let nameColumns = [ 'Code', 'Name' ]

			$.each(nameColumns, function(index, name) {
				console.log("primero1");
				$('#list-print-columns-name').append('<th >' + name + '</th>')
			});

			$.each(data, function(index, item) {
				console.log("primero2");
				$('#list-print').append(
						'<tr>' + '<td >' + item['code'] + '</td>' + '<td>'
								+ item['name'] + '</td>' + '</tr>')
			});

			// updateDataTable("cgiar_entity_types");
			// end print Data
			// ********************************************** */
		},
		error : function(e) {
			console.log(e);
		}
	});
}

function countries() {
	$.ajax({
		url : config.endpoint + '/countries',
		type : "GET",
		beforeSend : function() {
			hideFilter();
			cleanModal();
		},
		success : function(data) {
			// ********************************************* */
			// print data
			console.log(data);
			let nameColumns = [ 'Code', 'ISO Alpha2', 'Name', 'Region' ]

			$.each(nameColumns, function(index, name) {
				$('#list-print-columns-name').append('<th >' + name + '</th>')
			});

			$.each(data, function(index, item) {
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
		error : function(e) {
			console.log(e);
		}
	});
}

function un_regions() {
	$.ajax({
		url : config.endpoint + '/un-regions',
		type : "GET",
		beforeSend : function() {
			hideFilter();
			cleanModal();
		},
		success : function(data) {
			// ********************************************* */
			// print data
			let nameColumns = [ 'UN49Code', 'Name' ]

			$.each(nameColumns, function(index, name) {
				$('#list-print-columns-name').append('<th >' + name + '</th>')
			});

			$.each(data, function(index, item) {
				$('#list-print').append(
						'<tr>' + '<td >' + item['um49Code'] + '</td>' + '<td>'
								+ item['name'] + '</td>' + '</tr>')
			});
			// updateDataTable("un_regions");
			// end print Data
			// ********************************************** */
		},
		error : function(e) {
			console.log(e);
		}
	});
}

function institutions() {
	$
			.ajax({
				url : config.endpoint + '/institutions',
				type : "GET",
				beforeSend : function() {
					showFilter();
					cleanModal();
				},
				success : function(data) {
					// ********************************************* */
					// print data
					console.log(data);
					let nameColumns = [ 'Acronym', 'Code', 'Office Location',
							'Name', 'Website' ]

					$.each(nameColumns, function(index, name) {
						$('#list-print-columns-name').append(
								'<th >' + name + '</th>')
					});

					$
							.each(
									data,
									function(index, item) {
										$('#list-print')
												.append(
														'<tr>' + '<td >'
																+ validateNull(item['acronym'])
																+ '</td>'
																+ '<td>'
																+ item['code']
																+ '</td>'
																+

																'<td>'

																+ '<p class="nomar"><strong>Code:</strong> '
																+ item['countryOfficeDTO']['0'].code
																+ '</p>'
																+ '<p class="nomar"><strong>isHeadquarter:</strong> '
																+ converYesOrNot(item['countryOfficeDTO']['0'].isHeadquarter)
																+ '</p>'
																+ '<p class="nomar"><strong>isoAlpha2:</strong> '
																+ item['countryOfficeDTO']['0'].isoAlpha2
																+ '</p>'
																+ '<p class="nomar"><strong>name:</strong> '
																+ item['countryOfficeDTO']['0'].name
																+ '</p>'
																+ '</td>'
																+

																'<td>'
																+ item['name']
																+ '</td>'
																+ `<td  data-toggle="tooltip" data-placement="top" title="${item['websiteLink']}"><a href="${item['websiteLink']}" target="_blank">website link</a></td>`
																+ '</tr>')
									});
					updateDataTable("institutions");
					// end print Data
					// ********************************************** */
				},
				error : function(e) {
					console.log(e);
				}
			});
}
