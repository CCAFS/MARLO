$(document).ready(init);
/* Initializing variables */
var $usersList;

function init() {
  /* Declaring variables */
  $usersList = $(".users-list");

  attachEvents();

  addUser = function(composedName,userId) {
    var $li = $("#user-template").clone(true).removeAttr("id");
    $li.find('.name').html(escapeHtml(composedName));
    $li.find('.id').val(userId);
    $usersList.find("ul").append($li);
    checkItems();
    dialog.dialog("close");
  }

}

function attachEvents() {
  $('.glyphicon-remove').on('click', removeUser);
}

function removeUser() {
  var $parent = $(this).parent();
  $parent.hide(function() {
    $parent.remove();
    checkItems();
  });
}

function checkItems() {
  var items = $usersList.find('li').length;
  if(items == 0) {
    $usersList.find('p').fadeIn();
  } else {
    $usersList.find('p').fadeOut();
  }
}