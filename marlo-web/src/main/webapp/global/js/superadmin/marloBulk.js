var $globalUnitSelect, $phasesSelect, $deliverablesList;
$(document).ready(init);

function init() {
  $globalUnitSelect = $('#globalUnitID');
  $phasesSelect = $('#phaseID');
  $deliverablesList = $('#deliverables-checkbox table tbody');
  // Events
  attachEvents();
}

function attachEvents() {
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

  $.ajax({
      url: baseURL + "/getDeliverablesByPhase.do",
      data: {
        selectedPhaseID: phaseID
      },
      beforeSend: function() {
        $('.loading').fadeIn();
        $deliverablesList.empty();

      },
      success: function(data) {
        $.each(data.deliverablesbyPhase, function(i,e) {
          var $checkmarkRow = $('.check-template tr').clone(true).removeAttr('id');
          $checkmarkRow.attr('id', e.id);
          $checkmarkRow.find('input').addClass('deliverableCheckAdded');
          $checkmarkRow.find('input').val(e.id);
          $checkmarkRow.find('.id').text(e.id);
          $checkmarkRow.find('.labelText').text('D' + e.id + ': ' + e.title + '');

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
