$(document).ready(init);

function init() {
  // Events
  attachEvents();
}

function attachEvents() {
  $('#globalUnitID').on('change', updatePhases);
  $('#phaseID').on('change', updateDeliverables);
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
  var $deliverablesList = $('#deliverables-checkbox');
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
          var $checkmark = $('#check-template').parent().clone(true);

          $checkmark.find('input').val(e.id);
          $checkmark.find('.labelText').text(e.title);
          $deliverablesList.append($checkmark).append("<br>");

          // $deliverablesList.append('<li> <input type="checkbox" name="deliverables[]" value="' + e.id + '" />D' +
          // e.id
          // + ': ' + e.title + ' </li>')
        });
      }
  });
}
