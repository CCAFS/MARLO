$(document).ready(init);

var checkedOptions;
function init() {

  /**
   * Planned studies checkbox array
   */

  plannedStudiesIds = function() {
    var arr = [];
    $('#table-plannedStudies .plannedStudiesCheckbox input:checked, #table-plannedStudies .plannedStudiesCheckbox input.defaultChecked').each(function(i,e) {
      arr.push($(e).val());
    });
    return arr.join();
  }

  $('#table-plannedStudies .plannedStudiesCheckbox input').on('change', function() {
    console.log('change');
    console.log(plannedStudiesIds());
/*    $.ajax({
        url: baseURL + '/clusterByFPsAction.do',
        data: {
          plannedStudieID: plannedStudiesIds(),
          phaseID: phaseID
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
    });*/
  });
}