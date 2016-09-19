var $addOutcome, $addOutcomeSelect;
var OUTCOME_ID_PARAMETER = 'outcomeId'
$(document).ready(function() {

  $addOutcome = $('.addOutcomeBlock a');
  $addOutcomeSelect = $('.outcomesListBlock select');

  $('select').select2({
      templateResult: formatState,
      templateSelection: formatState,
      width: '100%'
  });

  var outcomesSelectedIds = ($('#outcomesSelectedIds').text()).split(',');
  $addOutcomeSelect.clearOptions(outcomesSelectedIds);

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

      addNewOutcome();
    }

  });

});

function addNewOutcome() {
  var value = $addOutcomeSelect.find('option:selected').val();
  var name = $addOutcomeSelect.find('option:selected').text();
  console.log(value)
  console.log(name)
}

function formatState(state) {
  if(state.id != "-1") {
    var text = state.text.split(":");
    var $state = $("<span><strong>" + text[0] + ":</strong> " + text[1] + "</span>");
    return $state;
  } else {
    var $state = $("<span>" + state.text + "</span>");
    return $state;
  }

};
