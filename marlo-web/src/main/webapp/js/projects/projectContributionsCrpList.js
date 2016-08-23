var $addOutcome, $addOutcomeSelect;
var OUTCOME_ID_PARAMETER = 'outcomeId'
$(document).ready(function() {

  $addOutcome = $('.addOutcomeBlock a');
  $addOutcomeSelect = $('.outcomesListBlock select');

  var outcomesSelectedIds = ($('#outcomesSelectedIds').text()).split(',');
  $addOutcomeSelect.clearOptions(outcomesSelectedIds);

  $('select').select2();

  $addOutcomeSelect.on('change', function(e) {
    var url = $addOutcome.attr('href');
    var uri = new Uri(url);
    var projectOutcomeID = $(this).val();
    uri.replaceQueryParam(OUTCOME_ID_PARAMETER, projectOutcomeID)
    $addOutcome.attr('href', uri);
  });

  $addOutcome.on('click', function(e) {
    e.preventDefault();
    var url = $(this).attr('href');
    var projectOutcomeID = new Uri(url).getQueryParamValue(OUTCOME_ID_PARAMETER);

    if(projectOutcomeID == "-1") {
      var notyOptions = jQuery.extend({}, notyDefaultOptions);
      notyOptions.text = 'You must select an outcome for adding.';
      noty(notyOptions);
    } else {
      window.location.href = url;
    }

  });

});
