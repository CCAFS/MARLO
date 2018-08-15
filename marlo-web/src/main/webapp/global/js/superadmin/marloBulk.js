$(document).ready(init);

function init() {
  // Events
  attachEvents();
}

function attachEvents() {
  $('#globalUnitID').on('change', updatePhases);
  $('#phaseID').on('change', updateDeliverables);

  $('#toggleSelectAll').on('change', function() {
    $('.deliverableCheck').prop("checked", this.checked);
  });
}

function updatePhases() {
  var globalUnitID = this.value;
  var $phasesSelect = $('#phaseID')

  if(globalUnitID == "-1") {
    return;
  }

  $.ajax({
      url: baseURL + "/getPhasesByGlobalUnit.do",
      data: {
        globalUnitID: globalUnitID
      },
      success: function(data) {
        $phasesSelect.empty();
        $phasesSelect.addOption('-1', 'Select an option...');
        $.each(data.phasesbyGlobalUnit, function(i,e) {
          $phasesSelect.addOption(e.id, e.name + ' ' + e.year);
        });
      }
  });
}

function updateDeliverables() {
  var phaseID = this.value;
  var $deliverablesList = $('#deliverables-checkbox table tbody');
  if(phaseID == "-1") {
    return;
  }

  $.ajax({
      url: baseURL + "/getDeliverablesByPhase.do",
      data: {
        phaseID: phaseID
      },
      success: function(data) {
        console.log(data);
        $deliverablesList.empty();
        $.each(data.deliverablesbyPhase, function(i,e) {
          var $checkmarkRow = $('.check-template tr').clone(true).removeAttr('id');

          $checkmarkRow.find('input').val(e.id);
          $checkmarkRow.find('.id').text(e.id);
          $checkmarkRow.find('.labelText').text('D' + e.id + ': ' + e.title + '');

          console.log($checkmarkRow);

          $deliverablesList.append($checkmarkRow);

        });
      }
  });
}
