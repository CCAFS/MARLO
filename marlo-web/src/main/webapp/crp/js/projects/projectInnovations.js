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
    $select.parent().animateCss('shake');
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