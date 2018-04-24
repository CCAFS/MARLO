$(document).ready(function() {

  // Add select2
  addSelect2();

  // Attach Events
  attachEvents();

});

function attachEvents() {
  // On select element
  $('select[class*="elementType-"]').on('change', onSelectElement);

  // On click remove button
  $('[class*="removeElementType-"]').on('click', onClickRemoveElement);
}

function onSelectElement() {
  var $select = $(this);
  var $option = $select.find('option:selected');
  var elementType = $(this).classParam('elementType');
  var maxLimit = $(this).classParam('maxLimit');
  var $list = $('.listType-' + elementType);
  var counted = $list.find('li').length;

  // Verify limit if applicable
  if((maxLimit > 0) && (counted >= maxLimit)) {
    return;
  }

  // Verify repeated selection
  if($list.find('.elementRelationID[value="' + $option.val() + '"]').length) {
    $select.val('-1').trigger('change.select2');
    var notyOptions = jQuery.extend({}, notyDefaultOptions);
    notyOptions.text = 'It was already selected';
    noty(notyOptions);
    return;
  }

  // Clone the new element
  var $element = $('#relationElement-' + elementType + '-template').clone(true).removeAttr("id");
  // Set attributes
  $element.find('.elementRelationID').val($option.val());
  $element.find('.elementName').html($option.text());
  // Show the element
  $element.appendTo($list).hide().show('slow', function() {
    $select.val('-1').trigger('change.select2');
  });

  // Update indexes
  $list.find('li.relationElement').each(function(i,element) {
    $(element).setNameIndexes(1, i);
  });
}

function onClickRemoveElement() {
  var removeElementType = $(this).classParam('removeElementType');
  var $parent = $(this).parent();
  var $list = $('.listType-' + removeElementType);
  $parent.slideUp(function() {
    $parent.remove();

    // Update indexes
    $list.find('li.relationElement').each(function(i,element) {
      $(element).setNameIndexes(1, i);
    });
  });
}

function addSelect2() {
  $('form select').select2({
      width: '100%',
      templateResult: formatList,
      templateSelection: formatList
  });
}

function formatList(state) {
  if(!state.id || (state.id == "-1")) {
    return state.text;
  }
  var result = "<span>" + state.text + "</span>";
  return $(result);
};