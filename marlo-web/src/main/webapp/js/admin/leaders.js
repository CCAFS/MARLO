$(document).ready(init);

function init() {

  /* Declaring Events */
  attachEvents();

  /* Override function from userManagement.js */
  addUser = addUserItem;

}

function attachEvents() {
  // Remove an item
  $('.glyphicon-remove').on('click', function() {
    var $parent = $(this).parent();
    var $block = $parent.parent().parent();
    console.log($block);
    $parent.hide(function() {
      $parent.remove();
      checkItems($block);
      updateUsersIndex($block);
    });
  });
}

function addUserItem(composedName,userId) {
  $usersList = $elementSelected.parent().find(".items-list");
  console.log($usersList);
  var $li = $("#user-template").clone(true).removeAttr("id");
  $li.find('.name').html(escapeHtml(composedName));
  $li.find('.id').val(userId);
  $usersList.find("ul").append($li);
  $li.show('slow');
  checkItems($usersList);
  updateUsersIndex($usersList);
  dialog.dialog("close");
}

function checkItems(block) {
  var items = $(block).find('li').length;
  if(items == 0) {
    $(block).find('p').fadeIn();
  } else {
    $(block).find('p').fadeOut();
  }
}

function updateUsersIndex(list) {
  $(list).find('li').each(function(i,item) {
    var customName = 'programUsers[' + i + ']';
    $(item).find('.user').attr('name', customName + '.user.id');
    $(item).find('.id').attr('name', customName + '.id');
  });
}
