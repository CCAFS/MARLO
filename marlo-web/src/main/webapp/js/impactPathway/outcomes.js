$(document).ready(init);

function init() {

  /* Declaring Events */
  attachEvents();

  /* Init Select2 plugin */
  // $('select').select2();
}

function attachEvents() {

  // Add an Outcome
  $('.addOutcome').on('click', addOutcome);
  // Remove an Outcome
  $('.removeOutcome').on('click', removeOutcome);

  // Add a Milestone
  $('.addMilestone').on('click', addMilestone);
  // Remove a Milestone
  $('.removeMilestone').on('click', removeMilestone);

  // Add a Sub IDO
  $('.addSubIdo').on('click', addSubIdo);
  // Remove a Sub IDO
  $('.removeSubIdo').on('click', removeSubIdo);

  // Add an assumption
  $('.addAssumption').on('click', addAssumption);
  // Remove assumption
  $('.removeAssumption').on('click', removeAssumption);

}

/**
 * Outcome Functions
 */

function addOutcome() {
  var $list = $('.outcomes-list');
  var $item = $('#outcome-template').clone(true).removeAttr("id");
  $list.append($item);
  updateOutcomesIndexes($list, "outcome");
  $item.show('slow');
}

function removeOutcome() {
  var $list = $(this).parents('.outcomes-list');
  var $item = $(this).parents('.outcome');
  $item.hide(function() {
    $item.remove();
    updateOutcomesIndexes($list, "outcome");
  });
}

function updateOutcomesIndexes(list,name) {
  $(list).find('.outcome').each(function(i,item) {

  });
}

/**
 * Milestone Functions
 */

function addMilestone() {
  var $list = $(this).parents('.outcome').find('.milestones-list');
  var $item = $('#milestone-template').clone(true).removeAttr("id");
  $list.append($item);
  updateMilestonesIndexes($list, "outcome[0].milestones");
  $item.show('slow');
}

function removeMilestone() {
  var $list = $(this).parents('.outcome').find('.milestones-list');
  var $item = $(this).parents('.milestone');
  $item.hide(function() {
    $item.remove();
    updateMilestonesIndexes($list, "outcome[0].milestone");
  });
}

function updateMilestonesIndexes(list,name) {
  $(list).find('.milestone').each(function(i,item) {

  });
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