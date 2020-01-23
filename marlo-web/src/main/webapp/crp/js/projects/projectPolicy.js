var caseStudiesName;
var $elementsBlock;

$(document).ready(init);

function init() {

  // Add Events
  attachEvents();

  // Add select2
  addSelect2();

  // Add Geographic Scope
  $('select.elementType-repIndGeographicScope ').on("addElement removeElement", function(event,id,name) {
    setGeographicScope(this);
  });
  setGeographicScope($('form select.elementType-repIndGeographicScope')[0]);

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

  $('select.maturityLevel').on('change', function() {
    var id = this.value;
    if((id == 4) || (id == 5)) {
      $('.evidences-block .requiredTag').slideDown();
    } else {
      $('.evidences-block .requiredTag').slideUp();
    }
    if(id == 3) {
      $('.block-researchMaturity').slideDown();
    }
    else {
      $('.block-researchMaturity').slideUp();
    }
  });

//On change radio buttons
  $('input[class*="radioType-"]').on('change', onChangeRadioButton);

}

function addSelect2() {
  $('form select').select2({
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