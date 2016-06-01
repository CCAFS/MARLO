$(document).ready(init);

function init() {

  /* Declaring Events */
  attachEvents();

  /* Override function from userManagement.js */
  addUser = addUserItem;

}

function attachEvents() {

}

function addUserItem(composedName,userId) {
  // var $usersList = $(".users.items-list");
  // var $li = $("#user-template").clone(true).removeAttr("id");
  // $li.find('.name').html(escapeHtml(composedName));
  // $li.find('.id').val(userId);
  // $usersList.find("ul").append($li);
  // $li.show('slow');
  // checkItems($usersList);
  // updateUsersIndex($usersList);
  dialog.dialog("close");
}
