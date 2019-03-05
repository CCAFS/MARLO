$(document).ready(init);

function init() {

  // Add Select2
  $('form select').select2({
    width: '100%'
  });

  attachEvents();
}

function attachEvents() {

  // Add an example of W1/2 use
  $('.addExample').on('click', addExample);

  // Remove an example of W1/2 use
  $('.removeExample').on('click', removeExample);

  // Change expenditure category
  $('select.expenditureCategoriesSelect').on('change', function() {
    var optionSelected = this.value;
    var $block = $(this).parents('.fundingUseExample').find('.otherBlock');

    if(optionSelected == 10) {
      $block.slideDown();
    } else {
      $block.slideUp();
    }
  });

}

function addExample() {
  var $list = $(this).parents("form").find('.listExamples');
  var $item = $('#fundingUseExample-template').clone(true).removeAttr("id");
  $list.append($item);

  // Add select
  $item.find('select').select2({
    width: '100%'
  });

  $item.find('textarea').setTrumbowyg();

  $item.show('slow');
  updateIndexes();
}

function removeExample() {
  var $item = $(this).parents('.fundingUseExample');
  $item.hide(function() {
    $item.remove();
    updateIndexes();
  });
}

function updateIndexes() {
  $(".listExamples").find(".fundingUseExample").each(function(i,element) {
    $(element).setNameIndexes(1, i);
    $(element).find(".index").html(i + 1);
  });
}