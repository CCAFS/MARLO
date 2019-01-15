$(document).ready(function() {

  // Add select2
  addSelect2();

  // Attach Events
  attachEvents();
});

function attachEvents() {

  $("#deliverableSelect").select2({
    templateResult: formatState,
    templateSelection: formatState,
    width: "100%"
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

  $('input.input-yn').on('change', function(){
    var value = this.value;
    var $parent = $(this).parents(".contributionForm");
    var $narrativeBlock = $parent.find(".narrativeBlock")

    if(value == "true"){
      $narrativeBlock.show();
    }else{
      $narrativeBlock.hide();
    }

   });

}

function addSelect2() {
  $('form select').select2({
      width: '100%',
      templateResult: formatList,
      templateSelection: formatList
  });

  $('form select.countriesSelect').select2({
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
  console.log(state);
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

function formatState(state) {
  if(!state.id) {
    return state.text;
  }
  var $state = "";
  if(state.element.value != "-1") {
    $state =
        $('<span> <i class="flag-sm flag-sm-' + state.element.value.toUpperCase() + '"></i>  ' + state.text + '</span>');
  } else {
    $state = $('<span>' + state.text + '</span>');
  }
  return $state;
};
