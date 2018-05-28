$(document).ready(function() {

  // Add select2
  addSelect2();

  // Attach Events
  attachEvents();

});

function attachEvents() {

  // Check the stage of innovation
  $('select.stageInnovationSelect').on('change', function() {
    var isStageFour = this.value == 4;
    if(isStageFour) {
      $('.stageFourBlock').slideDown();
    } else {
      $('.stageFourBlock').slideUp();
    }
  });

  // Check geographicScopeSelect
  $('select.geographicScopeSelect').on('change', function() {
    var $regionalBlock = $('.regionalBlock');
    var $nationalBlock = $('.nationalBlock');

    var isRegional = this.value == 2;
    var isMultiNational = this.value == 3;
    var isNational = this.value == 4;
    var isSubNational = this.value == 5;

    // Regions
    if(isRegional) {
      $regionalBlock.show();
    } else {
      $regionalBlock.hide();
    }

    // Countries
    $nationalBlock.find("select").val(null).trigger('change');
    if(isMultiNational || isNational || isSubNational) {
      if(isMultiNational) {
        $nationalBlock.find("select").select2({
            maximumSelectionLength: 0,
            placeholder: "Select a country(ies)",
            templateResult: formatStateCountries,
            templateSelection: formatStateCountries,
            width: '100%'
        });
      } else {
        $nationalBlock.find("select").select2({
            maximumSelectionLength: 1,
            placeholder: "Select a country(ies)",
            templateResult: formatStateCountries,
            templateSelection: formatStateCountries,
            width: '100%'
        });
      }
      $nationalBlock.show();
    } else {
      $nationalBlock.hide();
    }
  });
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