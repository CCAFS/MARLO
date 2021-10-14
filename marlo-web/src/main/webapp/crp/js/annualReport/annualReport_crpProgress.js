$(document).ready(init);

function init() {

  // Add Select2
  $('form select').select2({
    width: '100%'
  });

  console.log('CRP Progress');

  attachEvents();
}

function attachEvents() {
  if ($('#actualPhase').html() == 'true') {
    var editor = $('.TA_additionalContribution textarea');
    console.log(editor.prop('disabled', true));
  }

  // Add item
  $('.addSloTarget').on('change', addSloTarget);

  // Remove item
  $('.removeSloTarget').on('click', removeSloTarget);

}

function addSloTarget() {
  var $select = $(this);
  var $option = $select.find('option:selected');
  var $list = $(this).parents("form").find('.sloTargetsList');
  var $item = $('#sloTarget-template').clone(true).removeAttr("id");

  console.log('Change');

  // Verify repeated selection
  var $repeatedElement = $list.find('.indicatorTargetID[value="' + $option.val() + '"]');
  if ($repeatedElement.length) {
    $select.val('-1').trigger('change.select2');
    $repeatedElement.parent().animateCss('shake');
    var notyOptions = jQuery.extend({}, notyDefaultOptions);
    notyOptions.text = 'It was already selected';
    noty(notyOptions);
    return;
  }

  var name = $option.text().split(/-(.+)?/);
  $item.find('.name').html("<strong>" + name[0] + "</strong> <br>" + name[1]);
  $item.find('input.indicatorTargetID').val($option.val());

  // Show the element
  $item.appendTo($list).hide().show('slow', function () {
    $select.val('-1').trigger('change.select2');
  });

  updateIndexes();
}

function removeSloTarget() {
  var $item = $(this).parents('.sloTarget');
  $item.hide(function () {
    $item.remove();
    updateIndexes();
  });
}

function updateIndexes() {
  $(".sloTargetsList").find(".sloTarget").each(function (i, element) {
    $(element).setNameIndexes(1, i);
    $(element).find(".index").html(i + 1);
  });
}