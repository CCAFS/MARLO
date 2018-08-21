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
    $('.deliverableCheck').prop("checked", this.checked);
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
        $('.count').text((data.deliverablesbyPhase).length);
        $.each(data.deliverablesbyPhase, function(i,e) {
          var $checkmarkRow = $('.check-template tr').clone(true).removeAttr('id');

          $checkmarkRow.find('input').val(e.id);
          $checkmarkRow.find('.id').text(e.id);
          $checkmarkRow.find('.labelText').text('D' + e.id + ': ' + e.title + '');

          console.log($checkmarkRow);

          $deliverablesList.append($checkmarkRow);

        });
      },
      complete: function() {
        $('.loading').fadeOut();
      }
  });
}
