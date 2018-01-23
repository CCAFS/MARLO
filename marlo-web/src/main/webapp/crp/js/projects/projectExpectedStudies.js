$(document).ready(init);

function init() {

  // Add Select2
  $('form select').select2({
    width: '100%'
  });

  attachEvents();
}

function attachEvents() {

  // Add a next User
  $('.addExpectedStudy').on('click', addExpectedStudy);

  // Remove a next User
  $('.removeExpectedStudy').on('click', removeExpectedStudy);

}

function addExpectedStudy() {
  var $list = $(this).parents("form").find('.expectedStudies-list');
  var $item = $('#expectedStudy-template').clone(true).removeAttr("id");
  $list.append($item);
  $item.show('slow');
  updateIndexes();
}

function removeExpectedStudy() {

  var $item = $(this).parents('.expectedStudy');
  $item.hide(function() {
    $item.remove();
    updateIndexes();
  });
}

function updateIndexes() {

// Update beneficiaries
  $(".expectedStudies-list").find(".expectedStudy").each(function(i,e) {
    $(e).setNameIndexes(1, i);
    $(e).find(".index").html(i + 1);
  });
}
