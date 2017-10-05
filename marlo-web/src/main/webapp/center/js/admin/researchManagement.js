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
  var $list = $('.researchAreas-list');
  var $item = $('#researchArea-template').clone(true).removeAttr("id");
  $list.append($item);
  $item.show('slow');
  updateResearchIndex();
}

function removeArea() {
  var $parent = $(this).parents('.researchArea');
  $parent.hide(function() {
    $parent.remove();
    updateResearchIndex();
  });
}

function addResearchProgram() {
  var $list = $(this).parents('.researchArea').find('.programs-list');
  var $item = $('#researchProgram-template').clone(true).removeAttr("id");
  $list.append($item);
  $item.show('slow');
  updateResearchIndex();
}

function removeProgram() {
  var $parent = $(this).parents('.researchProgram');
  $parent.hide(function() {
    $parent.remove();
    updateResearchIndex();
  });
}

/**
 * This function override the add function of userManagement.js
 * 
 * @param {String} composedName - Whole user name
 * @param {Number} userId - User ID
 * @returns
 */
function addUserItem(composedName,userId) {
  var cloneItem = $elementSelected.classParam("clone");
  var $usersList = $elementSelected.parents(".items-list");
  var $li = $("#" + cloneItem + "-template").clone(true).removeAttr("id");
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
  $('form .researchArea').each(function(iResearchArea,researchArea) {

    // Update Research Area Index
    $(researchArea).setNameIndexes(1, iResearchArea);

    // Update Research Area - Leaders Index
    $(researchArea).find('.userItem').each(function(iLeader,leader) {
      $(leader).setNameIndexes(2, iLeader);
    });

    // Update Research Area - Programs Index
    $(researchArea).find('.researchProgram').each(function(iResearchProgram,researchProgram) {
      $(researchProgram).setNameIndexes(2, iResearchProgram);

      // Update Research Program - Leaders Index
      $(researchProgram).find('.userItem').each(function(iProgramLeader,programLeader) {
        $(programLeader).setNameIndexes(3, iProgramLeader);
      });
    });

  });
}