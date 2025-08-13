(function($) {
	$.toggleShowPassword = function(options) {
		var settings = $.extend({
			field : "#password",
			control : "#toggle_show_password",
		}, options);

		var control = $(settings.control);
		var field = $(settings.field);

		control.bind('click', function() {
			if (control.is(':checked')) {
				field.attr('type', 'text');
			} else {
				field.attr('type', 'password');
			}
		})
	};

}(jQuery));

$(document).ready(function() {

	$.toggleShowPassword({
		field : '#password',
		control : "#showPassword"
	});

	var end = new Date();

	$('#datetimepicker_from_date').datetimepicker({
		format : 'yyyy-mm-dd',
		autoclose : true,
		endDate : end,
		minView : 2,
		pickTime : false,
		language : 'pt-BR'
	});

	$('#datetimepicker_to_date').datetimepicker({
		format : 'yyyy-mm-dd',
		autoclose : true,
		endDate : end,
		minView : 2,
		pickTime : false,
		language : 'pt-BR'
	});

	$('#helpButton').click(function() {
		$("#exampleModal").modal();
	});

});


//function selectInstitution() {
//
//	var institutionId = document.getElementById("institutionDropDown").value;
//	
//	$
//			.ajax({
//				type : 'GET',
//				url : "../getCampuses",
//				data : {
//					"institutionId" : institutionId
//				},
//				success : function(data) {
//
//					var campusDropDown = $('#campusDropDown'), option = "";
//
//					campusDropDown.empty();
//
//					option = option
//							+ "<option value=''> Select Campus </option>";
//
//					for (var i = 0; i < data.length; i++) {
//						option = option + "<option value='"
//								+ data[i].campus_id + "'>"
//								+ data[i].campus_name + "</option>";
//						
//					}
//
//					campusDropDown.append(option);
//				},
//				error : function() {
//					alert("error");
//				}
//
//			});
//
//}
//
//function selectCampus() {
//
//	var campusId = document.getElementById("campusDropDown").value;
//	
//	$
//			.ajax({
//				type : 'GET',
//				url : "../getLibraries",
//				data : {
//					"campusId" : campusId
//				},
//				success : function(data) {
//
//					var libraryDropDown = $('#libraryDropDown'), option = "";
//
//					libraryDropDown.empty();
//
//					option = option
//							+ "<option value=''> Select Library </option>";
//
//					for (var i = 0; i < data.length; i++) {
//						option = option + "<option value='"
//								+ data[i].library_id + "'>"
//								+ data[i].library_name + "</option>";
//						
//					}
//
//					libraryDropDown.append(option);
//				},
//				error : function() {
//					alert("error");
//				}
//
//			});
//
//}
//
//function selectLibrary() {
//
//	var libraryId = document.getElementById("libraryDropDown").value;
//	
//	$
//			.ajax({
//				type : 'GET',
//				url : "../getLocations",
//				data : {
//					"libraryId" : libraryId
//				},
//				success : function(data) {
//
//					var locationDropDown = $('#locationDropDown'), option = "";
//
//					locationDropDown.empty();
//
//					option = option
//							+ "<option value=''> Select Location </option>";
//
//					for (var i = 0; i < data.length; i++) {
//						option = option + "<option value='"
//								+ data[i].location_id + "'>"
//								+ data[i].location_name + "</option>";
//						
//						//alert(data[i].location_name);
//					}
//
//					locationDropDown.append(option);
//				},
//				error : function() {
//					alert("error");
//				}
//
//			});
//
//}
//
//
//function selectInstitution2() {
//
//	var institutionId = document.getElementById("institutionDropDown2").value;
//	
//	$
//			.ajax({
//				type : 'GET',
//				url : "../getCampuses",
//				data : {
//					"institutionId" : institutionId
//				},
//				success : function(data) {
//
//					var campusDropDown = $('#campusDropDown2'), option = "";
//
//					campusDropDown.empty();
//
//					option = option
//							+ "<option value=''> Select Campus </option>";
//
//					for (var i = 0; i < data.length; i++) {
//						option = option + "<option value='"
//								+ data[i].campus_id + "'>"
//								+ data[i].campus_name + "</option>";
//						
//					}
//
//					campusDropDown.append(option);
//				},
//				error : function() {
//					alert("error");
//				}
//
//			});
//
//}
//
//function selectCampus2() {
//
//	var campusId = document.getElementById("campusDropDown2").value;
//	
//	$
//			.ajax({
//				type : 'GET',
//				url : "../getLibraries",
//				data : {
//					"campusId" : campusId
//				},
//				success : function(data) {
//
//					var libraryDropDown = $('#libraryDropDown2'), option = "";
//
//					libraryDropDown.empty();
//
//					option = option
//							+ "<option value=''> Select Library </option>";
//
//					for (var i = 0; i < data.length; i++) {
//						option = option + "<option value='"
//								+ data[i].library_id + "'>"
//								+ data[i].library_name + "</option>";
//						
//					}
//
//					libraryDropDown.append(option);
//				},
//				error : function() {
//					alert("error");
//				}
//
//			});
//
//}
//
//function selectLibrary2() {
//
//	var libraryId = document.getElementById("libraryDropDown2").value;
//	
//	$
//			.ajax({
//				type : 'GET',
//				url : "../getLocations",
//				data : {
//					"libraryId" : libraryId
//				},
//				success : function(data) {
//
//					var locationDropDown = $('#locationDropDown2'), option = "";
//
//					locationDropDown.empty();
//
//					option = option
//							+ "<option value=''> Select Location </option>";
//
//					for (var i = 0; i < data.length; i++) {
//						option = option + "<option value='"
//								+ data[i].location_id + "'>"
//								+ data[i].location_name + "</option>";
//						
//						//alert(data[i].location_name);
//					}
//
//					locationDropDown.append(option);
//				},
//				error : function() {
//					alert("error");
//				}
//
//			});
//
//}

function updateDropdown(currentSelect, nextDropdownName, url, paramName, idField, nameField) {
	console.log(currentSelect);
	
	
	const value = currentSelect.value;
    const row = currentSelect.closest('.row');
    const nextDropdown = row.querySelector(`[name="${nextDropdownName}"]`);

    if (!nextDropdown) return;

    $.ajax({
        type: 'GET',
        url: url,
        data: { [paramName]: value },
        success: function (data) {
            let options = `<option value=''>Select ${capitalize(nextDropdownName.replace('DropDown', ''))}</option>`;
            data.forEach(item => {
                options += `<option value='${item[idField]}'>${item[nameField]}</option>`;
            });
            nextDropdown.innerHTML = options;
        },
        error: function () {
            alert('Error fetching ' + nextDropdownName);
        }
    });
}

function capitalize(str) {
    return str.charAt(0).toUpperCase() + str.slice(1);
}

// Entry points for onchange attributes
function selectInstitution(selectEl) {
    updateDropdown(selectEl, "campusDropDown", "../getCampuses", "institutionId", "campus_id", "campus_name");
}

function selectCampus(selectEl) {
    updateDropdown(selectEl, "libraryDropDown", "../getLibraries", "campusId", "library_id", "library_name");
}

function selectLibrary(selectEl) {
    updateDropdown(selectEl, "locationDropDown", "../getLocations", "libraryId", "location_id", "location_name");
}
