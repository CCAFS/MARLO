$(document).ready(init);

function init() {

  // Add Select2
  $('form select').select2({
    width: '100%'
  });

  $('input.buudgetInput').currencyInput();
  attachEvents();
}

function attachEvents() {

// Add a next User
  $('.addLeverage').on('click', addLeverage);

// Remove a next User
  $('.removeLeverage').on('click', removeLeverage);

}

function addLeverage() {
  var $list = $(this).parents("form").find('.leverage-list');
  var $item = $('#leverage-template').clone(true).removeAttr("id");
  $list.append($item);
  $item.show('slow');
  checkNextUsersItems($list);
  updateIndexes();
}

function removeLeverage() {
  var $list = $(this).parents('.leverage-list');
  var $item = $(this).parents('.leverage');
  $item.hide(function() {
    $item.remove();
    checkNextUsersItems($list);
    updateIndexes();
  });
}

function updateIndexes() {

// Update beneficiaries
  $(".leverage-list").find(".leverage").each(function(i,e) {
    $(e).setNameIndexes(1, i);
    $(e).find(".index").html(i + 1);
  });
}

function checkNextUsersItems(block) {
  var items = $(block).find('.leverage ').length;
  if(items == 0) {
    $(block).parent().find('p.message').fadeIn();
  } else {
    $(block).parent().find('p.message').fadeOut();
  }
}