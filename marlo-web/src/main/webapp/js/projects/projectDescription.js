// Limits for textarea input
var lWordsElemetTitle = 20;
var lWordsElemetDesc = 150;

var $statuses, $statusDescription;

$(document).ready(function() {

  $statuses = $('#description_project_status');
  $statusDescription = $('#statusDescription');
  var implementationStatus = $statuses.find('option[value="2"]').text();
  $endDate = $('#project\\.endDate');

  datePickerConfig({
      "startDate": "#project\\.startDate",
      "endDate": "#project\\.endDate",
      defaultMinDateValue: $("#minDateValue").val(),
      defaultMaxDateValue: $("#maxDateValue").val()
  });
  // setProgramId();
  setDisabledCheckedBoxes();
  addSelect2();
  applyWordCounter($("textarea.project-title"), lWordsElemetTitle);
  applyWordCounter($("textarea.project-description"), lWordsElemetDesc);

  validateEvent([
    "#justification"
  ]);

  /**
   * Upload files functions
   */

  $('.fileUpload .remove').on('click', function(e) {
    var context = $(this).attr('id').split('-')[1];
    var $parent = $(this).parent().parent();
    var $inputFile = $('[id$=' + context + '-template]').clone(true).removeAttr("id");
    $parent.empty().append($inputFile);
    $inputFile.hide().fadeIn('slow');
    forceChange = true;
  });

  /**
   * CORE-Projects
   */
  var $coreSelect = $('#projectsList select');
  var $coreProjects = $('#projectsList .list');

  // loadInitialCoreProjects();

  /** Events */

  // Event to add an item to core Project list from select option
  $coreSelect.on('change', function(e) {
    addItemList($(this).find('option:selected'));
  });

  // Event to remove an element 'li' from core project list
  $('ul li .remove').on('click', function(e) {
    if($(e.target).hasClass('popUpValidation')) {
      $("#removeContribution-dialog").dialog({
          modal: true,
          width: 500,
          buttons: {
              "Confirm": function() {
                removeItemList($(e.target).parents('li'));
                $(this).dialog("close");
              },
              "Cancel": function() {
                $(this).dialog("close");
              }
          }
      });
    } else {
      removeItemList($(e.target).parents('li'));
    }

  });

  $statuses.on('change', function(e) {
    if(isStatusCancelled($(this).val())) {
      $statusDescription.show(400);
    } else {
      $statusDescription.hide(400);
    }
  });

  $endDate.on('change', changeStatus);
  $endDate.trigger('change');

  /** Functions */

  function changeStatus() {
    var d = new Date($(this).val());
    checkImplementationStatus(d.getFullYear());
  }

  function isStatusCancelled(statusId) {
    return(statusId == "5")
  }

  function checkImplementationStatus(year) {
    if(year <= currentReportingYear) {
      $statuses.removeOption(2);
    } else {
      $statuses.addOption(2, implementationStatus);
    }
    $statuses.select2();
  }

  // Function to load all core projects with ajax
  function loadInitialCoreProjects() {
    $.ajax({
        'url': '../../' + $('#projectsAction').val(),
        beforeSend: function() {
          $coreSelect.empty().append(setOption(-1, "Please select a project"));
        },
        success: function(data) {
          // Getting core projects previously selected
          var coreProjectsIds = [];
          $coreProjects.find('li input.id').each(function(i_id,id) {
            coreProjectsIds.push($(id).val().toString());
          });
          // Setting core projects allowed to select
          $.each(data.projects, function(i,project) {
            if($.inArray(project.id.toString(), coreProjectsIds) == -1) {
              $coreSelect.append(setOption(project.id, project.id + " - " + project.title));
            }
          });
        },
        complete: function() {
          $coreSelect.select2();
        }
    });
  }

  function addItemList($item) {
    var $listElement = $("#cpListTemplate").clone(true).removeAttr("id");
    $listElement.find('.id').val($item.val());
    $listElement.find('.name').html($item.text());
    $listElement.appendTo($coreProjects).hide().show('slow');
    $coreProjects.find('.emptyText').hide();
    $item.remove();
    $coreSelect.trigger("change.select2");
    setcoreProjectsIndexes();
  }

  function removeItemList($item) {
    // Adding to select list
    var data = {
        id: $item.find('.id').val(),
        'name': $item.find('.name').text()
    };
    var $select = $item.parents('.panel').find('select');
    $select.append(setOption(data.id, data.name));
    $select.trigger("change.select2");
    // Removing from list
    $item.hide("slow", function() {
      $item.remove();
      setcoreProjectsIndexes();
    });
  }

  function setcoreProjectsIndexes() {
    $coreProjects.find('li').each(function(i,item) {
      var elementName = "project.linkedProjects";
      $(item).find('.id').attr('name', elementName);
    });
  }

});

/**
 * Attach to the date fields the datepicker plugin
 */
function datePickerConfig(element) {
  var defaultMinDateValue = element.defaultMinDateValue;
  var defaultMaxDateValue = element.defaultMaxDateValue;
  var minDateValue = defaultMinDateValue;
  var maxDateValue = defaultMaxDateValue;

  // Start date calendar
  maxDateValue = $(element.endDate).val();

  // Add readonly attribute to prevent inappropriate user input
  // $(element.startDate).attr('readonly', true);
  var finalMaxDate = (maxDateValue != 0) ? maxDateValue : defaultMaxDateValue;
  $(element.startDate).datepicker({
      dateFormat: "yy-mm-dd",

      minDate: '2015-01-01',
      maxDate: '2019-12-31',
      changeMonth: true,
      changeYear: true,
      defaultDate: null,
      onClose: function(selectedDate) {
        if(selectedDate != "") {
          // $(element.endDate).datepicker("option", "minDate", selectedDate);
        }
      }
  });

  // End date calendar
  minDateValue = $(element.startDate).val();

  // Add readonly attribute to prevent inappropriate user input
  // $(element.endDate).attr('readonly', true);
  var finalMinDate = (minDateValue != 0) ? minDateValue : defaultMinDateValue;
  $(element.endDate).datepicker({
      dateFormat: "yy-mm-dd",
      minDate: '2015-01-01',
      maxDate: '2019-12-31',
      changeMonth: true,
      changeYear: true,
      defaultDate: null,
      onClose: function(selectedDate) {
        if(selectedDate != "") {
          // $(element.startDate).datepicker("option", "maxDate", selectedDate);
        }
      }
  });
}

// Activate the chosen plugin.
function addSelect2() {
  $("form select").select2();
}

// Set default Program ID
function setProgramId() {
  var programId = $("input#programID").val();
  $("#projectWorking input[value='" + programId + "']").attr("checked", true).attr("onclick", "return false");
}

function setDisabledCheckedBoxes() {
  $('#projectWorking input[type=checkbox]:checked').attr("onclick", "return false").addClass('disabled');
}
