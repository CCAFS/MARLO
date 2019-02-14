var caseStudiesName;
var $elementsBlock;

$(document).ready(init);

function init() {

  // Add Events
  attachEvents();

  // Add select2
  addSelect2();

  // This function enables launch the pop up window
  popups();

  // Add amount format
  $('input.currencyInput').currencyInput();

  $('.ccRelevanceBlock input:radio').on('change', function() {
    var $commentBox = $(this).parents('.ccRelevanceBlock').find('.ccCommentBox');
    if(this.value != 3) {
      $commentBox.slideDown();
    } else {
      $commentBox.slideUp();
    }
  });
}

function attachEvents() {

  $('select.elementType-repIndPolicyType').on("addElement removeElement", function(event,id,name) {
    // Other Please Specify
    if(id == 4) {
      if(event.type == "addElement") {
        $('.block-pleaseSpecify').slideDown();
      }
      if(event.type == "removeElement") {
        $('.block-pleaseSpecify').slideUp();
      }
    }
  });

  // On change policyInvestimentTypes
  $('select.policyInvestimentTypes').on('change', function() {
    console.log(this.value);
    if(this.value == 3) {
      $('.block-budgetInvestment').slideDown();
    } else {
      $('.block-budgetInvestment').slideUp();
    }
  });

  // Partnership Geographic Scope
  $('.nationalBlock').find("select").select2({
      placeholder: "Select a country(ies)",
      templateResult: formatStateCountries,
      templateSelection: formatStateCountries,
      width: '100%'
  });

  $('select.elementType-repIndGeographicScope ').on("addElement removeElement", function(event,id,name) {
    var $partner = $(this).parents('.geographicScopeBlock');
    var $scopes = $(this).parents('.elementsListComponent');
    var $regionalBlock = $partner.find('.regionalBlock');
    var $nationalBlock = $partner.find('.nationalBlock');

    var isRegional = $scopes.find('.elementRelationID[value="2"]').exists();
    var isMultiNational = $scopes.find('.elementRelationID[value="3"]').exists();
    var isNational = $scopes.find('.elementRelationID[value="4"]').exists();
    var isSubNational = $scopes.find('.elementRelationID[value="5"]').exists();

    // Regions
    if(isRegional) {
      $regionalBlock.slideDown();
    } else {
      $regionalBlock.slideUp();
    }

    // Countries
    // $nationalBlock.find("select").val(null).trigger('change');
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
            placeholder: "Select a country",
            templateResult: formatStateCountries,
            templateSelection: formatStateCountries,
            width: '100%'
        });
      }
      $nationalBlock.slideDown();
    } else {
      $nationalBlock.slideUp();
    }
  });

}

function addSelect2() {
  $('form select').select2({
    width: '100%'
  });
}

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