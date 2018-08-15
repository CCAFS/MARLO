$(document).ready(init);

function init() {
  // Events
  attachEvents();
}

function attachEvents() {
  $('#globalUnitID').on('change', updatePhases);
}

function updatePhases() {
  var globalUnitID = this.value;
  var $phasesSelect = $('#phaseID')

  if(!globalUnitID) {
    return;
  }

  $.ajax({
      url: baseURL + "/getPhasesByGlobalUnit.do",
      type: 'GET',
      data: {
        globalUnitID: globalUnitID
      },
      success: function(data) {
        console.log(data);
        $phasesSelect.empty();
        $phasesSelect.addOption(-1, 'Select an option...');
        $.each(data.phasesbyGlobalUnit, function(i,e) {
          $phasesSelect.addOption(e.id, e.name + ' ' + e.year);
        });
      }
  });
}
