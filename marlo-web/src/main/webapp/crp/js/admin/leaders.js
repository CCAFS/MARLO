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
    $parent.hide(function() {
      $parent.remove();
      checkItems($block);
      updateUsersIndex($block, $block.parent().find('.inputName-input').text());
    });
  });
}

function addUserItem(composedName,userId) {
  $usersList = $elementSelected.parent().find(".items-list");
  var $li = $("#user-template").clone(true).removeAttr("id");
  $li.find('.name').html(escapeHtml(composedName));
  $li.find('.user').val(userId);
  $usersList.find("ul").append($li);
  $li.show('slow');
  checkItems($usersList);
  updateUsersIndex($usersList, $elementSelected.find('.inputName-input').text());
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

function updateUsersIndex(list,name) {
  $(list).find('li').each(function(i,item) {


    var customName = name + '[' + i + ']';
    $(item).find('.user').attr('name', customName + '.user.id');
    $(item).find('.role').attr('name', customName + '.role.id');
    $(item).find('.id').attr('name', customName + '.id');
  });
}
