
// Overdue fine report Expanding text for title
function formatTitle(d) {
	// `d` is the original data object for the row

	return ('<dl>' + '<dt>Title</dt>' + '<dd>' + d.title + '</dd>' + '</dl>');

}

// Patron BLock report Expanding text for description
function formatDescription(d) {
	// `d` is the original data object for the row
	return ('<dl>' + '<dt>Description</dt>' + '<dd>'+ d.desc+'</dd>' + '</dl>');
}


function formatAuthorEditionPublishedYear(d) {
	// `d` is the original data object for the row
	return ( ( d.author != null ? '<dl>' + '<dt>Author</dt>' + '<dd>'+ d.author+'</dd>' + '</dl>' : '' ) +
			( d.edition != null ? '<dl>' + '<dt>Edition</dt>' + '<dd>'+ d.edition+'</dd>' + '</dl>' : '' )+
			( d.publishYear != null ? '<dl>' + '<dt>Published Year</dt>' + '<dd>'+ d.publishYear+'</dd>' + '</dl>': '' )+
			( d.staffNote != null ? '<dl>' + '<dt>Staff Note</dt>' + '<dd>'+ d.staffNote+'</dd>' + '</dl>': '' ) +
			( d.statement != null ? '<dl>' + '<dt>Statement</dt>' + '<dd>'+ d.statement+'</dd>' + '</dl>': '' ));
}


function callInstitutionRecordsReportAjaxRequest()
{
	var e = document.getElementById("institutionDropDown");
	var institution = e.value;

	if (institution != 0) {
		
		$.ajax({
		type : "GET",
		cache : false,
		url : '/reports/institutionRecords/data',
		data : {
			"institution" : institution
		},
		beforeSend : function() {
			$('#loader').removeClass('hidden') // Loader
		},
		success : function(data) {

			console.log('Damithsssss');
			
			$('#institutionalRecordDataTable').dataTable().fnDestroy();

			var table = $('#institutionalRecordDataTable').DataTable(
					{
						dom : 'Bfrtip',
						buttons : [ 'excel', 'print' ],
						data : data,
						order : [ [ 1, 'asc' ] ],
						orderCellsTop : true,
						fixedHeader : true,
						autoWidth : false,
						
						"columnDefs" : [ {
							title : "Institution",
							"targets" : 0,
							width : '15px'
						}, {
							title : "Location",
							"targets" : 1,
							width : '15px'
						}, {
							title : "Instance Count",
							"targets" : 2,
							width : '5px'
						}, {
							title : "Holdings Count",
							"targets" : 3,
							width : '5px'
						}, {
							title : "Item Count",
							"targets" : 4,
							width : '5px'
						},],
						columns : [{
							"data" : "institution"
						}, {
							"data" : "location"
						}, {
							"data" : "instanceCount"
						}, {
							"data" : "holdingCount",
						}, {
							"data" : "itemCount"
						} ],

					
						"pageLength" : 1000,
						"displayLength" : 1000,
						"drawCallback" : function(settings) {
							var api = this.api();
							var rows = api.rows({
								page : 'current'
							}).nodes();
							var last = null;
							var subTotal = new Array();
							var groupID = -1;
							var aData = new Array();
							var index = 0;

							api
									.column(0, {
										page : 'current'
									})
									.data()
									.each(
											function(group,
													i) {

												// console.log(group+"-----"+i);

												var vals = api
														.row(api.row($(rows).eq(i)).index())
														.data();
												
												// console.log(group +">>>"+
												// vals.instanceCount);
												
												var instanceCount = vals.instanceCount; 
												var holdingCount = vals.holdingCount; 
												var itemCount = vals.itemCount;

												if (typeof aData[group] == 'undefined') {
													aData[group] = new Array();
													aData[group].rows = [];
													aData[group].instanceCount = [];
													aData[group].itemCount = [];
													aData[group].holdingCount = [];
												}

												aData[group].rows
														.push(i);
												aData[group].instanceCount
														.push(instanceCount);
												aData[group].holdingCount
														.push(holdingCount);
												aData[group].itemCount
														.push(itemCount);
											});

							var idx = 0;

							for ( var office in aData) {

								idx = Math.max.apply(Math,
										aData[office].rows);

								var sumInstanceCount = 0;
								var sumHoldingCount = 0;
								var sumItemCount = 0;

								$
										.each(
												aData[office].instanceCount,
												function(k,
														v) {
													sumInstanceCount = sumInstanceCount
															+ v;
												});

								$
										.each(
												aData[office].itemCount,
												function(k,
														v) {
													sumItemCount = sumItemCount
															+ v;
												});

								$
										.each(
												aData[office].holdingCount,
												function(k,
														v) {
													sumHoldingCount = sumHoldingCount
															+ v;
												});

								$(rows)
										.eq(idx)
										.after(
												'<tr class="group" style="background-color: #c1c1c1;"><td colspan="2"> Total count of '
														+ office
														+ '  </td>'
														+ '<td>'
														+ sumInstanceCount
														+ '</td><td>'
														+ sumHoldingCount
														+ '</td><td>'
														+ sumItemCount
														+ '</td></tr>');

							}
							;

						}
					});

		},
		complete : function() {
			// class and hiding the spinner.
			$('#loader').addClass('hidden')
		},
	});
	}
}

function callOverdueOpenLoansReportAjaxRequest()
{	
	var e = document.getElementById("servicePointDropDown");
	var servicepointid = e.value;

	if (servicepointid != 0) {

	$.ajax({
		type : "GET",
		cache : false,
		url : '/reports/overdueOpenLoans/data',
		data : {
			"servicepointid" : servicepointid
		},
		beforeSend : function() {
			$('#loader').removeClass('hidden') // Loader
		},
		success : function(data) {

			$('#overdueOpenLoansDataTable').dataTable().fnDestroy();

			var table = $('#overdueOpenLoansDataTable').DataTable(
					{

						dom : 'Bfrtip',
						buttons : [ 'excel', 'print' ],
						data : data,
						order : [ [ 1, 'asc' ] ],
						dom : 'Blfrtip',
						orderCellsTop : true,
						fixedHeader : true,
						autoWidth : false,

						"columnDefs" : [ {
							title : "Location",
							"targets" : 0
						}, {
							title : "Barcode",
							"targets" : 1,
							width : '5px'
						}, {
							title : "Title",
							"targets" : 2,
							width : '15px'
						}, {
							title : "Due Date",
							"targets" : 3,
							width : '5px'
						}, {
							title : "Primary Identifiier",
							"targets" : 4
						}, {
							title : "User",
							"targets" : 5
						},
						{
							title : "Email",
							"targets" : 6
						}, {
							title : "Phone",
							"targets" : 7
						} ],
						columns : [ {
							"data" : "location"
						}, {
							"data" : "barcode"
						}, {
							"data" : "title"
						}, {
							"data" : "date"
						}, {
							"data" : "identifier",
						}, {
							"data" : "name"
						}, {
							"data" : "email"
						}, {
							"data" : "phone"
						}, ],

						"lengthMenu" : [ [ 10, 50, 100, 200, -1 ],
								[ 10, 50, 100, 200, "All" ] ],
						"pageLength" : 10,
					});

		},
		complete : function() {
			// class and hiding the spinner.
			$('#loader').addClass('hidden')
		},
	});
	}
	
}

function callPatronBlocksReportAjaxRequest() {

	var e = document.getElementById("institutionDropDown");
	var institution = e.value;

	if (institution != 0) {

	$.ajax({
		type : "GET",
		cache : false,
		url : '/reports/patronBlocks/data',
		data : {
			"institution" : institution
		},
		beforeSend : function() {
			$('#loader').removeClass('hidden') // Loader
		},
		success : function(data) {

			$('#patronBlocksDataTable').dataTable().fnDestroy();

			var table = $('#patronBlocksDataTable').DataTable(
					{

						dom : 'Bfrtip',
						buttons : [ 'excel', 'print' ],
						data : data,
						dom : 'Blfrtip',
						orderCellsTop : true,
						fixedHeader : true,
						autoWidth : false,

						"columnDefs" : [ {
							title : "",
							"targets" : 0
						}, {
							title : "Primary Identifiier",
							"targets" : 1
						}, {
							title : "User",
							"targets" : 2
						}, {
							title : "Email",
							"targets" : 3
						}, {
							title : "Type",
							"targets" : 4
						}, {
							title : "Code",
							"targets" : 5
						}, {
							title : "Borrowing",
							"targets" : 6
						},
						{
							title : "Renewals",
							"targets" : 7
						},
						{
							title : "Requests",
							"targets" : 8
						},],
						columns : [ {
							className : 'dt-control',
							orderable : false,
							data : null,
							defaultContent : ''
						}, {
							"data" : "identifier",
						}, {
							"data" : "name"
						}, {
							"data" : "email"
						}, {
							"data" : "type"
						}, {
							"data" : "code"
						}, { 
							"data" : "borrowing",
							render : getBooleanImage,
							class : 'text-center' ,
							 'sortable': false,
						},{ 
							"data" : "renewals",
							render : getBooleanImage,
							class : 'text-center' ,
							 'sortable': false,
						},{ 
							"data" : "requests",
							render : getBooleanImage,
							class : 'text-center' ,
							 'sortable': false,
						}],

						"lengthMenu" : [ [ 10, 50, 100, 200, -1 ],
								[ 10, 50, 100, 200, "All" ] ],
						"pageLength" : 10,
					});

			table.on('click', 'td.dt-control', function(e) {
				let tr = e.target.closest('tr');
				let row = table.row(tr);

				if (row.child.isShown()) {
					// This row is already open - close it
					row.child.hide();
				} else {
					// Open this row
					row.child(formatDescription(row.data())).show();
				}
			});

		},
		complete : function() {
			// class and hiding the spinner.
			$('#loader').addClass('hidden')
		},
	});
	}

}

function callOverdueFinesReportAjaxRequest() {

	var e = document.getElementById("institutionDropDown");
	var feeFineOwner = e.value;

	if (feeFineOwner != 0) {
		$.ajax({
			type : "GET",
			cache : false,
			url : '/reports/overdueFines/data',
			data : {
				"feeFineOwner" : feeFineOwner
			},
			beforeSend : function() {
								
				$('#loader').removeClass('hidden') // Loader
			},
			success : function(data) {

				// alert(JSON.stringify(data));

				$('#overdueFinesDataTable').dataTable().fnDestroy();

				// $('#overdueFinesDataTable thead tr').clone(true).appendTo(
				// '#overdueFinesDataTable thead' );

				var table = $('#overdueFinesDataTable').DataTable(
						{

							dom : 'Bfrtip',
							buttons : [ 'excel', 'print' ],
							data : data,
							order : [ [ 7, 'asc' ] ],
							dom : 'Blfrtip',
							orderCellsTop : true,
							fixedHeader : true,
							autoWidth : false,

							"columnDefs" : [ {
								title : "",
								"targets" : 0
							}, {
								title : "Group",
								"targets" : 1
							}, {
								title : "Location",
								"targets" : 2
							}, {
								title : "Payment Status",
								"targets" : 3
							}, {
								title : "Type",
								"targets" : 4
							}, {
								title : "Due Date",
								"targets" : 5
							}, {
								title : "Patron Block",
								"targets" : 6,
								width : '5px'
							}, {
								title : "Primary Identifiier",
								"targets" : 7
							}, {
								title : "User",
								"targets" : 8
							},
							{
								title : "Email",
								"targets" : 9
							}, {
								title : "Barcode",
								"targets" : 10
							}, {
								title : "Amount",
								"targets" : 11,
								"width" : '5px'
							}, {
								title : "Remaning",
								"targets" : 12,
								"width" : '5px'
							} ],
							columns : [ {
								className : 'dt-control',
								orderable : false,
								data : null,
								defaultContent : ''
							}, {
								"data" : "group"
							}, {
								"data" : "location"
							}, {
								"data" : "paymentStatus"
							}, {
								"data" : "type"
							}, {
								"data" : "date"
							}, {
								"data" : "isPatronBlock",
							}, {
								"data" : "identifier",
							}, {
								"data" : "name"
							}, {
								"data" : "email"
							}, {
								"data" : "barcode"
							}, {
								"data" : "amount"
							}, {
								"data" : "remaningAmount"
							}, ],

							"lengthMenu" : [ [ 10, 50, 100, 200, -1 ],
									[ 10, 50, 100, 200, "All" ] ],
							"pageLength" : 10,
						});

				table.on('click', 'td.dt-control', function(e) {
					let tr = e.target.closest('tr');
					let row = table.row(tr);

					if (row.child.isShown()) {
						// This row is already open - close it
						row.child.hide();
					} else {
						// Open this row
						row.child(formatTitle(row.data())).show();
					}
				});

			},
			complete : function() {
				// class and hiding the spinner.
				$('#loader').addClass('hidden')
			},
		});
	}

}

function callOverdueItemsReportAjaxRequest() {

	var e = document.getElementById("institutionDropDown");
	var feeFineOwner = e.value;

	if (feeFineOwner != 0) {
		$.ajax({
			type : "GET",
			cache : false,
			url : '/reports/overdueItems/data',
			data : {
				"feeFineOwner" : feeFineOwner
			},
			beforeSend : function() {
				$('#loader').removeClass('hidden') // Loader
			},
			success : function(data) {

				// alert(JSON.stringify(data));

				$('#overdueItemsDataTable').dataTable().fnDestroy();

				// $('#overdueFinesDataTable thead tr').clone(true).appendTo(
				// '#overdueFinesDataTable thead' );

				var table = $('#overdueItemsDataTable').DataTable(
						{

							dom : 'Bfrtip',
							buttons : [ 'excel', 'print' ],
							data : data,
							order : [ [ 7, 'asc' ] ],
							dom : 'Blfrtip',
							orderCellsTop : true,
							fixedHeader : true,
							autoWidth : false,

							"columnDefs" : [ {
								title : "Location",
								"targets" : 0
							}, {
								title : "Barcode",
								"targets" : 1
							}, {
								title : "Title",
								"targets" : 2
							}, {
								title : "Due Date",
								"targets" : 3
							}, {
								title : "Primary Identifiier",
								"targets" : 4
							}, {
								title : "User",
								"targets" : 5
							},

							{
								title : "Email",
								"targets" : 6
							}, {
								title : "Phone",
								"targets" : 7
							} ],
							columns : [ {
								"data" : "location"
							}, {
								"data" : "barcode"
							}, {
								"data" : "title"
							}, {
								"data" : "date"
							}, {
								"data" : "identifier",
							}, {
								"data" : "name"
							}, {
								"data" : "email"
							}, {
								"data" : "phone"
							}, ],

							"lengthMenu" : [ [ 10, 50, 100, 200, -1 ],
									[ 10, 50, 100, 200, "All" ] ],
							"pageLength" : 10,
						});

			},
			complete : function() {
				// class and hiding the spinner.
				$('#loader').addClass('hidden')
			},
		});
	}

}

function callInventoryLoansReportAjaxRequest() {

	var e = document.getElementById("locationDropDownList");
	var location = e.value;
	
	var e2 = document.getElementById("yearDropDown");
	var year = e2.value;
	
	$.ajax({
		type : "GET",
		cache : false,
		url : '/reports/inventoryloans/data',
		data : {
			"location" : location,
			"year" : year
		},
		beforeSend : function() {
			$('#loader').removeClass('hidden') // Loader
		},
		success : function(data) {

			$('#inventoryLoanTable').dataTable().fnDestroy();

			var table = $('#inventoryLoanTable').DataTable(
					{

						dom : 'Bfrtip',
						buttons : [ 'excel', 'print' ],
						data : data,
						order : [ [ 4, 'asc' ] ],
						dom : 'Blfrtip',
						orderCellsTop : true,
						fixedHeader : true,
						autoWidth : false,
						"columnDefs" : [ {
							"width" : "10px",
							title : "Type",
							"targets" : 0
						}, {
							"width" : "50px",
							title : "Primary Identifier",
							"targets" : 1
						}, {
							"width" : "10px",
							title : "User",
							"targets" : 2
						}, {
							"width" : "10px",
							title : "Item Policy",
							"targets" : 3
						}, {
							"width" : "10px",
							title : "Due Date",
							"targets" : 4
						}, {
							"width" : "10px",
							title : "Barcode",
							"targets" : 5
						} ],
						columns : [

						{
							"data" : "item.materialType.name"
						}, {
							"data" : "borrower.externalSystemId"
						}, {
							"data" : "borrower",
							render : getFullName,
						}, {
							"data" : "loanPolicy.name"
						}, {
							"data" : "dueDate",
							render : getShortDate,
						}, {
							"data" : "item.barcode"
						} ],

						"lengthMenu" : [ [ 10, 50, 100, 200, -1 ],
								[ 10, 50, 100, 200, "All" ] ],
						"pageLength" : 10,
					});

		},
		complete : function() { // Set our complete callback, adding the .hidden
			// class and hiding the spinner.
			$('#loader').addClass('hidden')
		},
	});

}

function callCirculationLogReportAjaxRequest() {

	var sDate = $("#datetimepicker_from_date").find("input").val();
	var eDate = $("#datetimepicker_to_date").find("input").val();

	var institution = document.getElementById("institutionDropDown4").value;
	var campus = document.getElementById("campusDropDown4").value;
	var library = document.getElementById("libraryDropDown4").value;
	var location = document.getElementById("locationDropDown4").value;

	var isEmptyDateWants = document.getElementById("is_with_empty_dates").checked;
	
	var isOpenLoans = document.getElementById("is_only_open_loans").checked;
	
	var materialType = document.getElementById("materialTypeDropDown").value;
	
	 if (Date.parse(sDate) > Date.parse(eDate)) {
		 alert("Start date shouldn't greater than End date");
		 return false;
	 }
	 
	$.ajax({
		type : "GET",
		cache : false,
		url : '/reports/circulationlog/data',
		data : {
			"from_date" : $("#datetimepicker_from_date").find("input").val(),
			"to_date" : $("#datetimepicker_to_date").find("input").val(),
			"institution" : institution,
			"campus" : campus,
			"library" : library,
			"location" : location,
			"isEmptyDateWants" : isEmptyDateWants,
			"isOpenLoans" : isOpenLoans,
			"materialType" : materialType
		},
		beforeSend : function() {
			$('#loader').removeClass('hidden') // Loader
		},
		success : function(data) {

			//console.log(JSON.stringify(data));
			
			$('#circulationLogTable').dataTable().fnDestroy();

			var table = $('#circulationLogTable').DataTable(
					{

						dom : 'Bfrtip',
						buttons : [ 'excel', 'print' ],
						data : data,
						order : [ [ 5, 'asc' ] ],
						dom : 'Blfrtip',
						orderCellsTop : true,
						fixedHeader : true,
						autoWidth : false,
						"columnDefs" : [{
							title : "",
							"targets" : 0
						},
						{
							title : "Location",
							 width: "30%",
							"targets" : 1
						},
						{
							title : "Barcode",
							 width: "5%",
							"targets" : 2
						},{
							title : "Call Number",
							 width: "5%",
							"targets" : 3
						},{
							title : "Material Type",
							 width: "5%",
							"targets" : 4
						},{
							title : "Title",
							 width: "30%",
							"targets" : 5
						},{
							title : "Last Loan Date",
							 width: "10%",
							"targets" : 6
						},{
							title : "FOLIO Total Renewals",
							 width: "5%",
							"targets" : 7
						},{
							title : "FOLIO Total Loans",
							 width: "5%",
							"targets" : 8
						} ,
						{
							title : "ALMA Total Loans",
							 width: "5%",
							"targets" : 9
						},
						{
							title : "Author",
							visible: false,
				            searchable: false,
							"targets" : 10
						},
						{
							title : "Edition",
							visible: false,
				            searchable: false,
							"targets" : 11
						},
						{
							title : "Published Year",
							visible: false,
				            searchable: false,
							"targets" : 12
						},
						{
							title : "Statemet",
							visible: false,
				            searchable: false,
							"targets" : 13
						},
						{
							title : "Staff Note",
							visible: false,
				            searchable: false,
							"targets" : 14
						}
						],
						columns : [
						{
							className : 'dt-control',
							orderable : false,
							data : null,
							defaultContent : ''
						},{
							"data" : "locationId"
						},
						{
							"data" : "barcode"
						}, {
							"data" : "callNumber"
						}, {
							"data" : "materialType"
						}, {
							"data" : "title"
						}, {
							"data" : "loanDate",
							render : getLoanDate,
						}, {
							"data" : "renewalCount"
						}, {
							"data" : "numLoans"
						}, {
							"data" : "almaNumLoans"
						},
						{
							"data" : "author"
						},
						{
							"data" : "edition"
						},
						{
							"data" : "publishYear"
						},
						{
							"data" : "statement"
						},{
							"data" : "staffNote"
						}
						],
										
						"lengthMenu" : [ [ 10, 50, 100, 200, -1 ],
								[ 10, 50, 100, 200, "All" ] ],
						"pageLength" : 10,
					});

			
			table.on('click', 'td.dt-control', function(e) {
				let tr = e.target.closest('tr');
				let row = table.row(tr);

				if (row.child.isShown()) {
					// This row is already open - close it
					row.child.hide();
				} else {
					// Open this row
					row.child(formatAuthorEditionPublishedYear(row.data())).show();
				}
			});
		},
		complete : function() { // Set our complete callback, adding the .hidden
			// class and hiding the spinner.
			$('#loader').addClass('hidden')
		},
	});

}

function callCirculationLoanReportAjaxRequest() {

	var sDate = $("#datetimepicker_from_date").find("input").val();
	var eDate = $("#datetimepicker_to_date").find("input").val();

	var institution = document.getElementById("institutionDropDown3").value;
	var campus = document.getElementById("campusDropDown3").value;
	var library = document.getElementById("libraryDropDown3").value;
	var location = document.getElementById("locationDropDown3").value;

	var loanAction = document.getElementById("loanActionDropDown").value;
	var materialType = document.getElementById("materialTypeDropDown").value;
	
	 if (Date.parse(sDate) > Date.parse(eDate)) {
		 alert("Start date shouldn't greater than End date");
		 return false;
	 }
	 
	$.ajax({
		type : "GET",
		cache : false,
		url : '/reports/circulationloan/data',
		data : {
			"from_date" : $("#datetimepicker_from_date").find("input").val(),
			"to_date" : $("#datetimepicker_to_date").find("input").val(),
			"institution" : institution,
			"campus" : campus,
			"library" : library,
			"location" : location,
			"loanAction" : loanAction,
			"materialType" : materialType
		},
		beforeSend : function() {
			$('#loader').removeClass('hidden') // Loader
			
		},
		success : function(data) {

			console.log(JSON.stringify(data));
			
			$('#circulationLogTable').dataTable().fnDestroy();

			var table = $('#circulationLogTable').DataTable(
					{

						dom : 'Bfrtip',
						buttons : [ 'excel', 'print' ],
						data : data,
						order : [ [ 5, 'asc' ] ],
						dom : 'Blfrtip',
						orderCellsTop : true,
						fixedHeader : true,
						autoWidth : false,
						"columnDefs" : [{
							title : "",
							"targets" : 0
						},
						{
							title : "Location",
							 width: "30%",
							"targets" : 1
						},
						{
							title : "Barcode",
							 width: "5%",
							"targets" : 2
						},{
							title : "Call Number",
							 width: "5%",
							"targets" : 3
						},{
							title : "Material Type",
							 width: "5%",
							"targets" : 4
						},{
							title : "Title",
							 width: "40%",
							"targets" : 5
						},{
							title : "Loan Date",
							 width: "10%",
							"targets" : 6
						},
						{
							title : "Action",
							 width: "5%",
							"targets" : 7
						},
						{
							title : "Author",
							visible: false,
				            searchable: false,
							"targets" : 8
						},
						{
							title : "Edition",
							visible: false,
				            searchable: false,
							"targets" : 9
						},
						{
							title : "Published Year",
							visible: false,
				            searchable: false,
							"targets" : 10
						},
						{
							title : "Statemet",
							visible: false,
				            searchable: false,
							"targets" : 11
						},
						{
							title : "Staff Note",
							visible: false,
				            searchable: false,
							"targets" : 12
						}
						],
						columns : [
						{
							className : 'dt-control',
							orderable : false,
							data : null,
							defaultContent : ''
						},{
							"data" : "location"
						},
						{
							"data" : "barcode"
						}, {
							"data" : "callNumber"
						}, {
							"data" : "materialType"
						}, {
							"data" : "title"
						}, {
							"data" : "date"
						}, {
							"data" : "action"
						},
						{
							"data" : "author"
						},
						{
							"data" : "edition"
						},
						{
							"data" : "publishYear"
						},
						{
							"data" : "statement"
						},{
							"data" : "staffNote"
						}
						],
										
						"lengthMenu" : [ [ 10, 50, 100, 200, -1 ],
								[ 10, 50, 100, 200, "All" ] ],
						"pageLength" : 10,
					});

			
			table.on('click', 'td.dt-control', function(e) {
				let tr = e.target.closest('tr');
				let row = table.row(tr);

				if (row.child.isShown()) {
					// This row is already open - close it
					row.child.hide();
				} else {
					// Open this row
					row.child(formatAuthorEditionPublishedYear(row.data())).show();
				}
			});
		},
		complete : function() { // Set our complete callback, adding the .hidden
			// class and hiding the spinner.
			$('#loader').addClass('hidden')
		},
	});

}

function getBooleanImage(data, type, full, meta) {
	if (data == '1') {
		return '<i class="fa fa-check" aria-hidden="true"/>'
	} else {
		return '<i class="fa fa-times" aria-hidden="true"/>';
	}
}

function getShortDate(data) {
	return new Date(data).toLocaleDateString();
}

function getFullName(data) {

	return data.firstName + "  " + data.lastName
}

function getLoanDate(data) {

	if (data == '0001-01-01') {
		return ''
	} else {
		return data;
	}
}
