var $globalUnitSelect, $phasesSelect, $deliverablesList;
$(document).ready(init);

function init() {
  $entitySelect = $('#entityID');
  $globalUnitSelect = $('#globalUnitID');
  $phasesSelect = $('#phaseID');
  $deliverablesList = $('#deliverables-checkbox table tbody');
  // Events
  attachEvents();
}

function attachEvents() {
  $entitySelect.on('change', updateEntity);
  $globalUnitSelect.on('change', updatePhases);
  $phasesSelect.on('change', updateDeliverables);

  $('#toggleSelectAll').on('change', function() {
    $deliverablesList.find('tr:visible').find('.deliverableCheck').prop("checked", this.checked);
    updateCheckedCount();
  });

  $('.deliverableCheck').on('change', function() {
    updateCheckedCount();
  });

  $('#filterButton').on('click', function() {
    var fiterText = $.trim($('#filterText').val());
    if(fiterText) {
      $deliverablesList.find('tr').hide();
      $('.deliverableCheck').prop("checked", false);
      $.each(fiterText.split(','), function(i,id) {
        var id = $.trim(id);
        if(id) {
          $('tr#' + id).show();
          $('tr#' + id).find('.deliverableCheck').prop("checked", true);
        }
      });
    } else {
      $deliverablesList.find('tr').show();
      $('.deliverableCheck').prop("checked", true);
    }
    updateCheckedCount();
  });
}

function updateEntity() {
  var entityName = $(this).find(":selected").text();
  var entityAction = $(this).find(":selected").classParam('action');
  $('th.entityName').text(entityName);
  $("#bulkReplicationForm").attr("action", "./" + entityAction + ".do");

  $globalUnitSelect.val(-1);
  $phasesSelect.empty();
  $deliverablesList.empty();
  $('.count').text(0);
}

function updatePhases() {
  var globalUnitID = this.value;

  if(globalUnitID == "-1") {
    return;
  }

  $.ajax({
      url: baseURL + "/getPhasesByGlobalUnit.do",
      data: {
        selectedGlobalUnitID: globalUnitID
      },
      beforeSend: function() {
        $('.loading').fadeIn();
        $phasesSelect.empty();
        $deliverablesList.empty();
        $('.count').text(0);
      },
      success: function(data) {
        $phasesSelect.addOption('-1', 'Select an option...');
        $.each(data.phasesbyGlobalUnit, function(i,e) {
          $phasesSelect.addOption(e.id, e.name + ' ' + e.year);
        });
      },
      complete: function() {
        $('.loading').fadeOut();
        updateCheckedCount();
      }
  });
}

function updateDeliverables() {
  var phaseID = this.value;
  if(phaseID == "-1") {
    return;
  }

  var entity = $entitySelect.find(":selected").text();
  //console.log(entity);

  $.ajax({
      url: baseURL + "/" + $entitySelect.val() + ".do",
      data: {
        selectedPhaseID: phaseID,
        includePublications : entity.includes("WOS")
      },
      beforeSend: function() {
        $('.loading').fadeIn();
        $deliverablesList.empty();

      },
      success: function(data) {
        var dataArray = (data.entityByPhaseList);

        $.each(dataArray, function(i,e) {
          var $checkmarkRow = $('.check-template tr').clone(true).removeAttr('id');
          $checkmarkRow.attr('id', e.id);
          $checkmarkRow.find('input').addClass('deliverableCheckAdded');
          $checkmarkRow.find('input').val(e.id);
          $checkmarkRow.find('.id').text(e.id);
          $checkmarkRow.find('.labelText').text(e.composedName);

          // console.log($checkmarkRow);
          $deliverablesList.append($checkmarkRow);

        });
      },
      complete: function() {
        $('.loading').fadeOut();
        updateCheckedCount();
      }
  });
}

function updateCheckedCount() {
  var checkedCounted = $('.deliverableCheckAdded:checked').length;
  $('.count').text(checkedCounted);

  if(checkedCounted) {
    // $('.controls-block').fadeIn();
  } else {
    // $('.controls-block').fadeOut();
  }
}
