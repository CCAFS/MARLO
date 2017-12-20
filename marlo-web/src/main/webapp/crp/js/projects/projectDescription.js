// Limits for textarea input

var $statuses, $statusDescription;
var flagshipsIds;
var liaisonInstitutionsPrograms, liaisonUserSelected;

$(document).ready(function() {

  $statuses = $('.description_project_status');
  $statusDescription = $('#statusDescription');
  $endDate = $('.endDate');
  var implementationStatus = $statuses.find('option[value="2"]').text();

  liaisonUserSelected = $('#liaisonUserSelected').text();
  liaisonInstitutionsPrograms = jQuery.parseJSON($('#liaisonInstitutionsPrograms').text());

  datePickerConfig({
      "startDate": ".startDate",
      "endDate": ".endDate",
      defaultMinDateValue: $("#minDateValue").val(),
      defaultMaxDateValue: $("#maxDateValue").val()
  });

  setDisabledCheckedBoxes();

  addSelect2();

  $('form .CoASelect').select2({
      templateResult: formatState,
      templateSelection: formatState,
      width: '100%'
  });

  /**
   * Liaison institution selection
   */

  $('.liaisonInstitutionSelect').on('change', function() {
    var liasonInstitutionID = $(this).val();

    if(liasonInstitutionID == -1) {
      return
    }

    var crpProgramId = liaisonInstitutionsPrograms[liasonInstitutionID];
    if(crpProgramId != -1) {
      $('input[value="' + crpProgramId + '"]').prop("checked", true);
      $('#projectFlagshipsBlock input').trigger('change');
    }

    // Getting users
    $.ajax({
        url: baseURL + '/liasonUsersByInstitutionsAction.do',
        method: 'POST',
        data: {
          liasonInstitutionID: liasonInstitutionID
        },
        beforeSend: function() {
          $('.loading.liaisonUsersBlock').fadeIn();
          $('.liaisonUserSelect').empty();
          $('.liaisonUserSelect').addOption(-1, 'Select an option');
        },
        success: function(data) {
          $.each(data.liasonsUsers, function(i,e) {
            $('.liaisonUserSelect').addOption(e.id, escapeHtml(e.description));
            if(!e.active) {
              $('.liaisonUserSelect option[value="' + e.id + '"]').attr('disabled', 'disabled');
            }
          });

          // Set current liaison user
          if($('.liaisonUserSelect option[value="' + liaisonUserSelected + '"]').exists()) {
            $('.liaisonUserSelect').val(liaisonUserSelected);
          } else {
            $('.liaisonUserSelect').val('-1');
          }
          $('.liaisonUserSelect').trigger('change.select2');

        },
        complete: function(){
          $('.loading.liaisonUsersBlock').fadeOut();
        }
    });
  });
  if(editable){
    $('.liaisonInstitutionSelect').trigger('change');
  }

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
    console.log(flagshipsIds());
    $.ajax({
        url: baseURL + '/clusterByFPsAction.do',
        data: {
          flagshipID: flagshipsIds()
        },
        beforeSend: function() {
          $('.loading.clustersBlock').fadeIn();
          $coreSelect.empty();
          $coreSelect.addOption(-1, 'Select an option');
        },
        success: function(data) {
          console.log(data.clusters);
          $.each(data.clusters, function(i,e) {
            $coreSelect.addOption(e.id, e.description);
          });
        },
        complete: function(){
          $('.loading.clustersBlock').fadeOut();
        }
    });
  });

  // No regional programmatic focus
  $('#projectNoRegional').on('change', function() {
    if(this.checked) {
      $('input.rpInput').attr("onclick", "return false").addClass('disabled').prop("checked", false);
    } else {
      $('input.rpInput').attr("onclick", "").removeClass('disabled');
    }
  }).trigger('change');

  /**
   * Cluster of Activities
   */

  var $coreSelect = $('#projectsList select');
  var $coreProjects = $('#projectsList .list');

  var coaSelectedIds = ($('#coaSelectedIds').text()).split(',');
  $coreSelect.clearOptions(coaSelectedIds);

  // Validate more than one cluster and if has specific rule
  validateClusters();

  /**
   * Scope of project
   */

  var $projectsScopesSelect = $('#projectsScopes select');
  var $projectsScopes = $('#projectsScopes .list');

  var scopesSelectedIds = ($('#scopesSelectedIds').text()).split(',');
  $projectsScopesSelect.clearOptions(scopesSelectedIds);

  /** Gender questions * */

  $('input#gender').on('change', function() {
    if($(this).is(':checked')) {
      $('#gender-question').slideUp();
    } else {
      $('#gender-question').slideDown();
    }
  });

  $('input#gender, input#youth, input#capacity').on('change', function() {
    $('input#na').prop("checked", false);
  });

  $('input#na').on('change', function() {
    $('input#gender, input#youth, input#capacity').prop("checked", false);
    $('#gender-question').slideDown();
  });

  /** Events * */

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
          closeText: "",
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
    var statusID= $(this).val();
    if(isStatusCancelled(statusID) || isStatusComplete(statusID) || isStatusExtended(statusID)) {
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

  function validateClusters() {
    var clusterNumber = $coreProjects.find('li.clusterActivity').length;
    if($coreSelect.classParam('multipleCoA') === "false") {
      if(clusterNumber == 0) {
        $coreSelect.prop("disabled", false);
      } else {
        $coreSelect.prop("disabled", true);
      }
    }

  }

  function setProjectsIndexes() {
    // Validate more than one cluster and if has specific rule
    validateClusters();

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
  date($(element.startDate), $(element.endDate));
}

function date(start,end) {
  var dateFormat = "yy-mm-dd";
  var from = $(start).datepicker({
      dateFormat: dateFormat,
      minDate: MIN_DATE,
      maxDate: MAX_DATE,
      changeMonth: true,
      numberOfMonths: 1,
      changeYear: true,
      onChangeMonthYear: function(year,month,inst) {
        var selectedDate = new Date(inst.selectedYear, inst.selectedMonth, 1);
        $(this).datepicker('setDate', selectedDate);
        if(selectedDate != "") {
          $(end).datepicker("option", "minDate", selectedDate);
        }
      }
  }).on("click", function() {
    if(!$(this).val()) {
      $(this).datepicker('setDate', new Date());
    }
  });

  var to = $(end).datepicker({
      dateFormat: dateFormat,
      minDate: MIN_DATE,
      maxDate: MAX_DATE,
      changeMonth: true,
      numberOfMonths: 1,
      changeYear: true,
      onChangeMonthYear: function(year,month,inst) {
        var selectedDate = new Date(inst.selectedYear, inst.selectedMonth + 1, 0);
        $(this).datepicker('setDate', selectedDate);
        if(selectedDate != "") {
          $(start).datepicker("option", "maxDate", selectedDate);
        }
      }
  }).on("click", function() {
    if(!$(this).val()) {
      $(this).datepicker('setDate', new Date());
    }
  });

  function getDate(element) {
    var date;
    try {
      date = $.datepicker.parseDate(dateFormat, element.value);
    } catch(error) {
      date = null;
    }

    return date;
  }
}

// Activate the chosen plugin.
function addSelect2() {
  $("form select").select2();
}

function setDisabledCheckedBoxes() {
  // $('#projectWorking input[type=checkbox].fpInput:checked').attr("onclick", "return false").addClass('disabled');
}

function formatState(state) {
  if(state.text.length == 0) {
    return;
  }
  if(state.id != "-1") {
    var text = state.text.split(/:(.+)?/);
    if(typeof text[1] != "undefined") {
      var $state = $("<span><strong>" + text[0] + ":</strong> " + text[1] + "</span>");
      return $state;
    } else {
      var $state = $("<span>" + state.text + "</span>");
      return $state;
    }
  } else {
    var $state = $("<span>" + state.text + "</span>");
    return $state;
  }

};
