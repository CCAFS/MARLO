$(document).ready(function() {

  // Add select2
  addSelect2();

  // Attach Events
  attachEvents();

});

function attachEvents() {


}

function addSelect2() {
  $('form select').select2({
      width: '100%',
      templateResult: formatList,
      templateSelection: formatList
  });

  $('form select.countriesIds').select2({
      maximumSelectionLength: 0,
      placeholder: "Select a country(ies)",
      templateResult: formatStateCountries,
      templateSelection: formatStateCountries,
      width: '100%'
  });

  $('form #deliverableSelect').select2({
    templateResult: formatState,
    templateSelection: formatState,
    width: '100%'
});

}

function formatList(state) {
  if(!state.id || (state.id == "-1")) {
    return state.text;
  }
  var result = "<span>" + state.text + "</span>";
  return $(result);
};

function formatStateCountries(state) {
  if(!state.id) {
    return state.text;
  }
  var flag = '<i class="flag-sm flag-sm-' + state.element.value.toUpperCase() + '"></i> ';
  var $state;
  if(state.id != -1) {
    $state = $('<span>' + flag + state.text + '</span>');
  } else {
    $state = $('<span>' + state.text + '</span>');
  }
  return $state;
};
