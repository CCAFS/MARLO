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
    $li.show('slow');
    checkItems();
    dialog.dialog("close");
  }

}

function attachEvents() {
  $('.glyphicon-remove').on('click', removeUser);
  $('.addFlagship').on('click', addItem);
}

function addItem() {
  var $parent = $(this).parents('.row');
  var itemAcronym = $('#acronym-input').val();
  var itemName = $('#acronym-name').val();
}

function removeUser() {
  var $parent = $(this).parent();
  $parent.hide(function() {
    $parent.remove();
    checkItems();
  });
}

function checkItems() {
  updateUserMessage();
  updateIndexses();
}

function updateUserMessage() {
  var items = $usersList.find('li').length;
  if(items == 0) {
    $usersList.find('p').fadeIn();
  } else {
    $usersList.find('p').fadeOut();
  }
}

function updateIndexses() {
  $usersList.find('li').each(function(i,item) {
    var customName = 'programManagmentTeam[' + i + ']'
    $(item).find('.id').attr('name', customName + 'id');
  });
}