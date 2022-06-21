$(document).ready(function() {

  // Add select2
  addSelect2();

  // Add Geographic Scope
  $('select.elementType-repIndGeographicScope ').on("addElement removeElement", function(event,id,name) {
    setGeographicScope(this);
  });
  setGeographicScope($('form select.elementType-repIndGeographicScope')[0]);

  // Activate Popup
  popups();

  // Attach Events
  attachEvents();
  AddRequired();
  feedbackAutoImplementation();
});

function attachEvents() {

    // 
    $('#isClearLeadToAddRequired').on('click', AddRequired);

  // Check the stage of innovation
  $('select.stageInnovationSelect').on('change', function() {
    var isStageFour = this.value == 4;
    if(isStageFour) {
      $('.stageFourBlock-true').slideDown();
      $('.stageFourBlock-false').slideUp();
    } else {
      $('.stageFourBlock-true').slideUp();
      $('.stageFourBlock-false').slideDown();
    }
  });

  // Check the stage of innovation
  $('select.innovationTypeSelect').on('change', function() {
    console.log(this.value)
    var id = this.value;
    if(id == 6) {
      $('.typeSixBlock').slideDown();
      $('.numberInnovations-block').slideUp();
    } else if(id == 1) {
      $('.numberInnovations-block').slideDown();
      $('.typeSixBlock').slideUp();
    } else {
      $('.typeSixBlock').slideUp();
      $('.numberInnovations-block').slideUp();
    }
  });

  $('input.isClearLead').on('change', function() {
    var selected = $('input.isClearLead').is(":checked");

    if(selected == true) {
      $('.lead-organization').slideUp();
    } else {
      $('.lead-organization').slideDown();
    }

  })

//On change radio buttons
  $('input[class*="radioType-"]').on('change', onChangeRadioButton);

}
function AddRequired(){
  console.log($('#isClearLeadToAddRequired').is(":checked"));
  if ($('#isClearLeadToAddRequired').is(":checked")) {
    $('.top-five-contributing').find('.requiredTag').show();
  }else{
    $('.top-five-contributing').find('.requiredTag').hide();
  }
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

function onChangeRadioButton() {
	  var thisValue = this.value === "true";
	  var radioType = $(this).classParam('radioType');
	  if (thisValue) {
	    $('.block-' + radioType).slideDown();
	  } else {
	    $('.block-' + radioType).slideUp();
	  }
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
  var flag = '<i class="flag-icon flag-icon-' + state.element.value.toLowerCase() + '"></i> ';
  var $state;
  if(state.id != -1) {
    $state = $('<span>' + flag + state.text + '</span>');
  } else {
    $state = $('<span>' + state.text + '</span>');
  }
  return $state;
};