$(document).ready(init);

function init() {

  // Declaring Events
  attachEvents();

  // Override function from userManagement.js
  addUser = addUserItem;

}

function attachEvents() {
  // Add Area
  $('.addResearchArea').on('click', addResearchArea);

  // Remove an Area
  $('.removeArea').on('click', removeArea);

  // Add Program
  $('.addResearchProgram').on('click', addResearchProgram);

  // Remove a Program
  $('.removeProgram').on('click', removeProgram);

  // Remove an user item
  $('.removeUserItem').on('click', removeUserItem);

}

// ************************************************************************************************//
// ------------------------------------------- FUNCTIONS ------------------------------------------//
// ************************************************************************************************//

function addResearchArea() {
  console.log("addResearchArea");
}

function removeArea() {
  console.log("removeArea");
}

function addResearchProgram() {
  console.log("addResearchProgram");
}

function removeProgram() {
  console.log("removeProgram");
}

/**
 * This function override the add function of userManagement.js
 * 
 * @param {String} composedName - Whole user name
 * @param {Number} userId - User ID
 * @returns
 */
function addUserItem(composedName,userId) {
  $usersList = $elementSelected.parents(".items-list");
  console.log($usersList);
  var $li = $("#userItem-template").clone(true).removeAttr("id");
  $li.find('.name').html(escapeHtml(composedName));
  $li.find('.user').val(userId);
  $usersList.find("ul").append($li);
  $li.show('slow');
  updateResearchIndex();
  dialog.dialog("close");
}

/**
 * Remove any User item
 */
function removeUserItem() {
  var $parent = $(this).parent();
  var $block = $parent.parent().parent();
  $parent.hide(function() {
    $parent.remove();
    updateResearchIndex();
  });
}

/**
 * Update Research Areas, programs and person indexes
 */
function updateResearchIndex() {

}