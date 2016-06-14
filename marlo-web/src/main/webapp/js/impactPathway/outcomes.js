$(document).ready(init);

function init() {

  /* Declaring Events */
  attachEvents();

  /* Init Select2 plugin */
  // $('select').select2();
}

function attachEvents() {

  // Add an assumption
  $('.addAssumption').on('click', addAssumption);
  // Remove assumption
  $('.removeAssumption').on('click', removeAssumption);

// Add an assumption
  $('.addSubIdo').on('click', addSubIdo);
  // Remove assumption
  $('.removeSubIdo').on('click', removeSubIdo);

}

/**
 * SUB-IDOs Functions
 */

function addSubIdo() {
  var $list = $(this).parents('.outcome').find('.subIdos-list');
  var $item = $('#subIdo-template').clone(true).removeAttr("id");
  $list.append($item);
  updateSubIdosIndexes($list, "outcome[0].subIdos");
  $item.show('slow');
}

function removeSubIdo() {
  var $list = $(this).parents('.outcome').find('.subIdos-list');
  var $item = $(this).parents('.subIdo');
  $item.hide(function() {
    $item.remove();
    updateSubIdosIndexes($list, "outcome[0].subIdos");
  });
}

function updateSubIdosIndexes(list,name) {
  $(list).find('.subIdo').each(function(i,item) {
    var customName = name + '[' + i + '].';
    $(item).find('span.index').text(i + 1);
  });
}

/**
 * Assumptions Functions
 */

function addAssumption() {
  var $assumptionsList = $(this).parents('.subIdo').find('.assumptions-list');
  var $item = $('#assumption-template').clone(true).removeAttr("id");
  $assumptionsList.append($item);
  updateAssumptionsIndexes($assumptionsList, "outcome[0].subIdos[0].assumptions");
  $item.show('slow');
}

function removeAssumption() {
  var $assumptionsList = $(this).parents('.subIdo').find('.assumptions-list');
  var $item = $(this).parents('.assumption');
  $item.hide(function() {
    $item.remove();
    updateAssumptionsIndexes($assumptionsList, "outcome[0].subIdos[0].assumptions");
  });
}

function updateAssumptionsIndexes(list,name) {
  $(list).find('.assumption').each(function(i,item) {
    var customName = name + '[' + i + '].';
    var placeholderText = 'Assumption statement #' + (i + 1);
    $(item).find('.statement').attr('name', '').attr('placeholder', placeholderText);
  });
}