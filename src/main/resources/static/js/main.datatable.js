
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

	$.ajax({
		type : "GET",
		cache : false,
		url : '/reports/inventoryloans/data',
		data : {
			"location" : location
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

	var institution = document.getElementById("institutionDropDown").value
	var campus = document.getElementById("campusDropDown").value
	var library = document.getElementById("libraryDropDown").value
	var location = document.getElementById("locationDropDown").value

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
			"location" : location
		},
		beforeSend : function() {
			$('#loader').removeClass('hidden') // Loader
		},
		success : function(data) {

			$('#circulationLogTable').dataTable().fnDestroy();

			var table = $('#circulationLogTable').DataTable(
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
							title : "Barcode",
							"targets" : 0
						}, {
							"width" : "10px",
							title : "Call Number",
							"targets" : 1
						}, {
							"width" : "10px",
							title : "Type",
							"targets" : 2
						}, {
							"width" : "50px",
							title : "Title",
							"targets" : 3
						}, {
							"width" : "10px",
							title : "Last Loan Date",
							"targets" : 4
						}, {
							"width" : "10px",
							title : "# of loans",
							"targets" : 5
						} ],
						columns : [

						{
							"data" : "barcode"
						}, {
							"data" : "callNumber"
						}, {
							"data" : "materialType"
						}, {
							"data" : "title"
						}, {
							"data" : "loanDate"
						}, {
							"data" : "numLoans"
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
