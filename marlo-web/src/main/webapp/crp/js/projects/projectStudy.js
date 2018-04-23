var caseStudiesName;
var $elementsBlock;

$(document).ready(init);

function init() {

  // Add Events
  attachEvents();

  // Add select2
  addSelect2();

}

function attachEvents() {

  // On change radio buttons
  $('input[class*="radioType-"]').on('change', onChangeRadioButton);

  // On select element
  $('select[class*="elementType-"]').on('change', onSelectElement);

  // On click remove button
  $('[class*="removeElementType-"]').on('click', onClickRemoveElement);

}

function onChangeRadioButton() {
  var thisValue = this.value === "true";
  var radioType = $(this).classParam('radioType');
  if(thisValue) {
    $('.block-' + radioType).slideDown();
  } else {
    $('.block-' + radioType).slideUp();
  }
}

function onSelectElement() {
  var thisValue = this.value === "true";
  var elementType = $(this).classParam('elementType');

  var $element = $('#keyContributor-template').clone(true).removeAttr("id");
  var $list = $('.listType-' + elementType);

  $element.appendTo($list).hide().show('slow');
  console.log(elementType);
  console.log($list);
}

function onClickRemoveElement() {
  var removeElementType = $(this).classParam('removeElementType');
  var $parent = $(this).parent();
  console.log(removeElementType);
  $parent.slideUp(function() {
    $parent.remove();
  });
}

function addSelect2() {
  $('form select').select2({
    width: '100%'
  });
}
