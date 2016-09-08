// Limits for textarea input
var lWordsElemetTitle = 20;
var lWordsElemetDesc = 150;

var $statuses, $statusDescription;
var flagshipsIds;

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
   * Project Flagships
   */

  flagshipsIds = function() {
    var arr = [];
    $('#projectFlagshipsBlock input:checked').each(function(i,e) {
      arr.push($(e).val());
    });
    return arr.join()
  }

  $('#projectFlagshipsBlock input').on('change', function() {
    $.ajax({
        url: baseURL + '/clusterList.do',
        method: 'POST',
        data: {
          flagshipsId: flagshipsIds()
        },
        beforeSend: function() {
          $coreSelect.empty();
          $coreSelect.addOption(-1, 'Select an option');
        },
        success: function(data) {
          $.each(data.clusterOfActivities, function(i,e) {
            $coreSelect.addOption(e.id, e.description);
          });
        }
    });
  });

  // No regional programmatic focus
  $('#projectNoRegional').on('click', function() {
    if(this.checked) {
      $('input.rpInput').attr("onclick", "return false").addClass('disabled').prop("checked", false);
    } else {
      $('input.rpInput').attr("onclick", "").removeClass('disabled');
    }
  });

  /**
   * Cluster of Activities
   */

  var $coreSelect = $('#projectsList select');
  var $coreProjects = $('#projectsList .list');

  var coaSelectedIds = ($('#coaSelectedIds').text()).split(',');
  $coreSelect.clearOptions(coaSelectedIds);

  /**
   * Scope of project
   */

  var $projectsScopesSelect = $('#projectsScopes select');
  var $projectsScopes = $('#projectsScopes .list');

  var scopesSelectedIds = ($('#scopesSelectedIds').text()).split(',');
  $projectsScopesSelect.clearOptions(scopesSelectedIds);

  /** Events */

  // Event to add an item to core Project list from select option
  $coreSelect.on('change', function(e) {
    addItemList($(this).find('option:selected'));
  });

  $projectsScopesSelect.on('change', function(e) {
    addItemScopeList($(this).find('option:selected'));
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

  function addItemScopeList($item) {
    var $listElement = $("#projecScope-template").clone(true).removeAttr("id");
    $listElement.find('.id').val($item.val());
    $listElement.find('.name').html($item.text());

    $listElement.appendTo($projectsScopes).hide().show('slow');
    $projectsScopes.find('.emptyText').hide();
    $item.remove();
    $projectsScopesSelect.trigger("change.select2");
    setProjectsIndexes();
  }

  function addItemList($item) {
    var $listElement = $("#cpListTemplate").clone(true).removeAttr("id");
    $listElement.find('.id').val($item.val());
    $listElement.find('.name').html($item.text());

    // Getting CoA Leaders
    $.ajax({
        url: baseURL + '/ClusterActivitiesLeaders.do',
        data: {
          clusterActivityID: $item.val()
        },
        success: function(data) {
          $.each(data.leaders, function(i,e) {
            $listElement.find('.leaders').append('<li class="leader">' + escapeHtml(e.description) + '</li>');
          });
        }
    });

    $listElement.appendTo($coreProjects).hide().show('slow');
    $coreProjects.find('.emptyText').hide();
    $item.remove();
    $coreSelect.trigger("change.select2");
    setProjectsIndexes();
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
      setProjectsIndexes();
    });
  }

  function setProjectsIndexes() {
    $coreProjects.find('li.clusterActivity').each(function(i,item) {
      $(item).setNameIndexes(1, i);
    });

    $projectsScopes.find('li.projecScope').each(function(i,item) {
      $(item).setNameIndexes(1, i);
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
      maxDate: '2030-12-31',
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
      maxDate: '2030-12-31',
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

function setDisabledCheckedBoxes() {
  $('#projectWorking input[type=checkbox].fpInput:checked').attr("onclick", "return false").addClass('disabled');
}
